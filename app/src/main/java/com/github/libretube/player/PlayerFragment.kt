package com.github.libretube.player

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.libretube.R
import com.github.libretube.databinding.PlayerFragmentBinding
import com.github.libretube.feed.FeedAdapter
import com.github.libretube.home.HomeFragment
import com.github.libretube.home.MainActivity
import com.github.libretube.home.MainActivityViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.roundToInt

@AndroidEntryPoint
class PlayerFragment : Fragment() {
    private lateinit var _binding: PlayerFragmentBinding
    private val binding get() = _binding
    private var isMinimized = false
    private val playerViewModel: PlayerViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity
    private var exoPlayer: ExoPlayer? = null
    private lateinit var dataSourceFactory: DataSource.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSourceFactory = createDataSourceFactory()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = PlayerFragmentBinding.inflate(inflater, container, false)
        _binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)
        Log.d(
            HomeFragment::class.java.toString(),
            "NavController: ${findNavController().backQueue
                .map {
                    it.destination
                }
                .filterNot {
                    it is NavGraph
                }
                .joinToString(" > ") {
                    it.displayName.split('/')[1]
                }}"
        )

        mainActivity = activity as MainActivity
        binding.playerviewmodel = playerViewModel

        mainActivity.binding.mainMotionLayout.transitionToEnd()
        mainActivity.bindPlayerChannelClickedEvent(viewLifecycleOwner, findNavController())
        binding.playerMotionLayout.transitionToStart()

        initNavigation()
        initTextElements()
        initPlayerElements()
        initExoPlayer()
        initButtonRow()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.release()
    }

    private fun initNavigation() {
        playerViewModel.videoClickedEvent.observe(viewLifecycleOwner) {
            if (it != null) {
                // binding.scrollView2.fullScroll(View.FOCUS_UP)
                activityViewModel.setCurrentPlayingVideo(it.url)
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            if (!isMinimized) {
                minimize()
            } else {
                isEnabled = false
                activity?.onBackPressed()
            }
        }

        playerViewModel.channelClickedEvent.observe(viewLifecycleOwner) {
            minimize()
            activityViewModel.setChannel(it)
        }
    }

    private fun handleOrientation(orientation: Int) {
        when (orientation) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> {}
            else -> throw IllegalArgumentException("Must be either Configuration.Landscape or Portrait")
        }

        var orientationDegree = 0

        activity?.requestedOrientation = orientation

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            orientationDegree = 90
        }

        object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientation: Int) {
                val epsilon = 8
                val reverseDegree = orientationDegree + 180
                if (epsilonCheck(orientation, orientationDegree, epsilon) ||
                    epsilonCheck(orientation, reverseDegree, epsilon)
                ) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
                    disable()
                }
            }
            private fun epsilonCheck(a: Int, b: Int, epsilon: Int): Boolean {
                return abs(a - b) < epsilon
            }
        }.enable()
    }

    private fun initPlayerElements() {
        val adapter = FeedAdapter(playerViewModel)

        binding.relatedVideos.adapter = adapter
        binding.relatedVideos.layoutManager =
            GridLayoutManager(context, resources.getInteger(R.integer.grid_items))

        playerViewModel.paused.observe(viewLifecycleOwner) {
            if (it) {
                binding.playImageView.setImageResource(R.drawable.ic_pause)
                exoPlayer?.play()
            } else {
                binding.playImageView.setImageResource(R.drawable.ic_play)
                exoPlayer?.pause()
            }
        }

        playerViewModel.relatedVideos.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.submitList(it)
            }
        }
        activityViewModel.orientationMode.observe(viewLifecycleOwner) {
            // remember to hide everything when new thing added
            changeLayoutOrientation(it)
        }

        binding.closeImageView.setOnClickListener {
            mainActivity.supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        binding.playerMotionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
                binding.player.run {
                    useController = false
                }
            }
            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                if (endId == R.id.minimized || startId == R.id.minimized) {
                    mainActivity.binding.mainMotionLayout.progress = 1 - progress
                }
            }
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                mainActivity.binding.mainMotionLayout.progress =
                    mainActivity.binding.mainMotionLayout.progress.roundToInt().toFloat()

                if (currentId != R.id.minimized) {
                    binding.player.useController = true
                    isMinimized = false
                } else {
                    isMinimized = true
                }
            }
            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })
        // Hide the navbar when fragment opens
        mainActivity.binding.mainMotionLayout.transitionToEnd()
    }

    private fun changeLayoutOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.linLayout.visibility = View.GONE
            binding.mainContainer.isClickable = true

            binding.playerMotionLayout.setTransition(R.id.fullscreen)
            binding.playerMotionLayout.transitionToEnd()
            binding.player.keepScreenOn = true
        } else {
            binding.linLayout.visibility = View.VISIBLE
            binding.mainContainer.isClickable = false
            binding.playerMotionLayout.run {
                if (progress == 1F) {
                    setTransition(R.id.fullscreen)
                    transitionToStart()
                    setTransition(R.id.yt_transition)
                }
            }
            binding.player.keepScreenOn = false
        }
    }

    private fun initButtonRow() {
        binding.relPlayerShare.setOnClickListener {
            val intent = Intent()
            val url =
                "${getString(R.string.default_instance)}watch?v=${playerViewModel.videoUrl.value}"

            intent.run {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, "Share Url To:"))
        }
    }

    private fun initTextElements() {
        binding.playerTitleLayout.setOnClickListener {
            if (binding.playerDescription.visibility == View.GONE) {
                binding.playerDescription.visibility = View.VISIBLE
                binding.playerDescriptionArrow.rotation = 180F
            } else {
                binding.playerDescription.visibility = View.GONE
                binding.playerDescriptionArrow.rotation = 0F
            }
        }
    }

    private fun initExoPlayer() {
        if (playerViewModel.exoPlayer == null && exoPlayer == null) {
            val trackSelector = DefaultTrackSelector(requireContext())

            exoPlayer = ExoPlayer.Builder(requireContext()).apply {
                setMediaSourceFactory(createMediaSourceFactory())
                setTrackSelector(trackSelector)
            }.build()
        }

        with(binding.player) {
            setShowSubtitleButton(true)
            setShowNextButton(false)
            setShowPreviousButton(false)
            setControllerOnFullScreenModeChangedListener {
                if (activityViewModel.orientationMode.value == Configuration.ORIENTATION_LANDSCAPE) {
                    handleOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
                } else {
                    handleOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
                }
            }
            controllerHideOnTouch = true
            player = exoPlayer
        }

        playerViewModel.streamUrl.observe(viewLifecycleOwner) {
            if (it != null) {
                setExoPlayerMedia(it)
            }
        }
    }

    private fun setExoPlayerMedia(string: String, startDuration: Long = 0L) {
        exoPlayer?.playWhenReady = true
        exoPlayer?.setMediaItem(MediaItem.fromUri(string), startDuration)
        exoPlayer?.prepare()
    }
    /** Returns a [DataSource.Factory].  */
    @Synchronized
    private fun createDataSourceFactory(): DataSource.Factory {
        return DefaultDataSource.Factory(requireContext(), DefaultHttpDataSource.Factory())
    }

    private fun createMediaSourceFactory(): MediaSource.Factory {
        return DefaultMediaSourceFactory(dataSourceFactory)
    }

    private fun minimize() {
        binding.playerMotionLayout.transitionToEnd()
        mainActivity.binding.mainMotionLayout.transitionToStart()
    }
}
