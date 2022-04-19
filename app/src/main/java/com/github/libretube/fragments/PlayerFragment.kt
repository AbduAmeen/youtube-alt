package com.github.libretube.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.R
import com.github.libretube.activities.MainActivity
import com.github.libretube.activities.hideKeyboard
import com.github.libretube.adapters.TrendingAdapter
import com.github.libretube.formatShort
import com.github.libretube.network.PipedApiClient
import com.github.libretube.obj.PipedStream
import com.github.libretube.obj.Streams
import com.github.libretube.views.BaseTextView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.SubtitleConfiguration
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.net.URLEncoder
import kotlin.math.abs

class PlayerFragment : Fragment() {

    private var isFullScreen = false
    private var videoId: String? = null
    private var paused = false
    private var videoType = "MPEG_4"

    private var subtitles = mutableListOf<SubtitleConfiguration>()
    private var qualityOptions: Array<String> = arrayOf("Auto")
    private var videoStreams: HashMap<String, PipedStream> = hashMapOf()
    private var audioStreams: HashMap<String, PipedStream> = hashMapOf()

    private var isSubscribed: Boolean = false

    private lateinit var relatedRecView: RecyclerView
    private lateinit var exoPlayerView: StyledPlayerView
    private lateinit var motionLayout: MotionLayout
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var apiClient: PipedApiClient
    private lateinit var mainActivity: MainActivity
    private var mediaSource: MediaSource? = null

    private lateinit var relDownloadVideo: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoId = it.getString("videoId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()

        motionLayout = view.findViewById<MotionLayout>(R.id.playerMotionLayout)
        val playImageView = view.findViewById<ImageView>(R.id.play_imageView)

        mainActivity = activity as MainActivity
        exoPlayerView = view.findViewById(R.id.player)
        relDownloadVideo = view.findViewById(R.id.relPlayer_download)
        videoId = videoId?.replace("/watch?v=", "")
        apiClient = PipedApiClient.initialize(requireContext(), PlayerFragment::class.toString())

        // Fetch the media streams and initialize the player
        lifecycleScope.launch {
            val response = apiClient.fetchStreams(videoId!!) ?: return@launch

            initializeDataAsync(response)

            val initPlayerElements = lifecycleScope.launch { initializePlayerElements(response) }

            initializeExoPlayer(mediaSource)
            exoPlayer.play()
            initPlayerElements.join()
        }

        motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            var eId = 0
            var sId = 0

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                val mainMotionLayout = mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout)
                mainMotionLayout.progress = abs(progress)
                eId = endId
                sId = startId
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                println(currentId)
                val mainMotionLayout =
                    mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout)
                if (currentId == eId) {
                    view.findViewById<ImageButton>(R.id.quality_select).visibility = View.GONE
                    view.findViewById<ImageButton>(R.id.close_imageButton).visibility = View.GONE
                    view.findViewById<TextView>(R.id.quality_text).visibility = View.GONE
                    mainMotionLayout.progress = 1.toFloat()
                } else if (currentId == sId) {
                    view.findViewById<ImageButton>(R.id.quality_select).visibility = View.VISIBLE
                    view.findViewById<ImageButton>(R.id.close_imageButton).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.quality_text).visibility = View.VISIBLE
                    mainMotionLayout.progress = 0.toFloat()
                }
            }
        })

        motionLayout.progress = 1.toFloat()
        motionLayout.transitionToStart()

        view.findViewById<TextView>(R.id.playerDescription).text = videoId

        val transition: (View) -> Unit = {
            motionLayout.transitionToEnd()
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            mainActivity.supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        view.findViewById<ImageView>(R.id.close_imageView).setOnClickListener(transition)
        view.findViewById<ImageButton>(R.id.close_imageButton).setOnClickListener(transition)

        playImageView.setOnClickListener {
            paused = if (paused) {
                playImageView.setImageResource(R.drawable.ic_pause)
                exoPlayer.play()
                false
            } else {
                playImageView.setImageResource(R.drawable.ic_play)
                exoPlayer.pause()
                true
            }
        }

        view.findViewById<ImageButton>(R.id.fullscreen).setOnClickListener {
            // remember to hide everything when new thing added
            val mainContainer = view.findViewById<ConstraintLayout>(R.id.main_container)
            val linLayout = view.findViewById<LinearLayout>(R.id.linLayout)

            if (!isFullScreen) {
                with(motionLayout) {
                    getConstraintSet(R.id.start).constrainHeight(R.id.player, -1)
                    enableTransition(R.id.yt_transition, false)
                }
                mainContainer.isClickable = true
                linLayout.visibility = View.GONE

                mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                isFullScreen = true
            } else {
                with(motionLayout) {
                    getConstraintSet(R.id.start).constrainHeight(R.id.player, 0)
                    enableTransition(R.id.yt_transition, true)
                }
                mainContainer.isClickable = false
                linLayout.visibility = View.VISIBLE

                mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                isFullScreen = false
            }
        }
        relatedRecView = view.findViewById(R.id.player_recView)

        relatedRecView.layoutManager = GridLayoutManager(
            view.context,
            resources.getInteger(
                R.integer.grid_items
            )
        )

        mainActivity.findViewById<FrameLayout>(R.id.container).visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            exoPlayer.stop()
        } catch (e: Exception) {}
    }

    /*
    * Fetches data and processes the result into the UI
    */
    private suspend fun initializeDataAsync(response: Streams?) {
        val initVideoStreams = lifecycleScope.launch {
            response?.videoStreams?.forEach {
                videoStreams += Pair(it.quality + it.format, it)

                if (!qualityOptions.contains(it.quality!!)) {
                    qualityOptions += it.quality!!
                    return@forEach
                }

                if (mediaSource == null) {
                    val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
                    val videoItem: MediaItem = MediaItem.Builder()
                        .setUri(it.url)
                        .setSubtitleConfigurations(subtitles)
                        .build()
                    val videoSource: MediaSource = DefaultMediaSourceFactory(dataSourceFactory)
                        .createMediaSource(videoItem)
                    val audioSource: MediaSource = DefaultMediaSourceFactory(dataSourceFactory)
                        .createMediaSource(fromUri(response.audioStreams!![0].url!!))

                    mediaSource = MergingMediaSource(videoSource, audioSource)
                }
            }
        }

        val initAudioStreams = lifecycleScope.launch {
            response?.audioStreams?.forEach {
                audioStreams += Pair(it.quality + it.format, it)
            }
        }

        val initSubs = lifecycleScope.launchWhenCreated {
            response?.subtitles?.forEach {
                val mimeType = it.mimeType ?: return@forEach
                val uri = it.url?.toUri() ?: return@forEach

                subtitles.add(
                    SubtitleConfiguration.Builder(uri)
                        .setMimeType(mimeType) // The correct MIME type.
                        .setLanguage(it.code) // The subtitle language.
                        .build()
                )
            }
        }

        joinAll(initSubs, initAudioStreams, initVideoStreams)
    }

    private fun initializePlayerElements(response: Streams?) {
        val channelImage = view?.findViewById<ImageView>(R.id.player_channelImage)
        val token = context?.getSharedPreferences("token", Context.MODE_PRIVATE)?.getString("token", "")
        val playerDescription = view?.findViewById<BaseTextView>(R.id.playerDescription)
        val playerDescriptionArrow = view?.findViewById<ImageView>(R.id.playerDescriptionArrow)

        lifecycleScope.launch {
            relatedRecView.adapter = TrendingAdapter(response?.relatedStreams!!)
        }

        lifecycleScope.launch {
            Picasso.get().load(response?.uploaderAvatar).into(channelImage)
        }

        if (!token.isNullOrEmpty()) {
            val channelId = response?.uploaderUrl?.replace("/channel/", "")
            val subButton = view?.findViewById<MaterialButton>(R.id.player_subscribe)
            setSubscribedButton(subButton!!, channelId!!)
        }

        // Init quality selector
        view?.findViewById<TextView>(R.id.quality_text)?.text = "Auto"
        view?.findViewById<ImageButton>(R.id.quality_select)?.setOnClickListener {
            // Dialog for quality selection
            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it)
            }
            builder?.setTitle(R.string.choose_quality_dialog)

            builder?.setItems(qualityOptions) { _, which ->
                val currentPos = exoPlayer.currentPosition
                if (which == 0) {
                    val mediaItem = MediaItem.Builder()
                        .setUri(response?.hls)
                        .setSubtitleConfigurations(subtitles)
                        .build()
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.seekTo(currentPos)
                } else {
                    val dataSourceFactory = DefaultHttpDataSource.Factory()
                    val videoItem = MediaItem.Builder()
                        .setUri(videoStreams[qualityOptions[which] + videoType]!!.url)
                        .setSubtitleConfigurations(subtitles)
                        .build()
                    val videoSource = DefaultMediaSourceFactory(dataSourceFactory).createMediaSource(videoItem)

                    val audioItem = MediaItem.Builder()
                        .setUri(response?.audioStreams?.get(getMostBitRate(response.audioStreams))?.url!!)
                        .build()
                    val audioSource = DefaultMediaSourceFactory(dataSourceFactory).createMediaSource(audioItem)
                    val mergeSource = MergingMediaSource(videoSource, audioSource)

                    exoPlayer.setMediaSource(mergeSource)
                    exoPlayer.seekTo(currentPos)
                }
                view?.findViewById<TextView>(R.id.quality_text)?.text = qualityOptions[which]
            }
            val dialog = builder?.create()
            dialog?.show()
        }

        // init text of page
        view?.findViewById<ConstraintLayout>(R.id.playerTitleLayout)?.setOnClickListener {
            if (playerDescription?.visibility == View.GONE) {
                playerDescription.visibility = View.VISIBLE
                playerDescriptionArrow?.rotation = 180F
            } else {
                playerDescription?.visibility = View.GONE
                playerDescriptionArrow?.rotation = 0F
            }
        }
        view?.findViewById<TextView>(R.id.playerTitle)?.text = response?.title

        response?.description?.let {
            playerDescription?.setTextFromHtml(it)
        }

        view?.findViewById<TextView>(R.id.playerViewsInfo)?.text = "${response?.views?.formatShort()} views • ${response?.uploadDate}"
        view?.findViewById<TextView>(R.id.textLike)?.text = response?.likes?.formatShort()
        view?.findViewById<TextView>(R.id.player_channelName)?.text = response?.uploader

        // buttons
        view?.findViewById<ConstraintLayout>(R.id.player_channel)?.setOnClickListener {
            val bundle = bundleOf("channel_id" to response?.uploaderUrl)

            mainActivity.navController.navigate(R.id.channel, bundle)
            mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).transitionToEnd()
            view?.findViewById<MotionLayout>(R.id.playerMotionLayout)?.transitionToEnd()
        }

        view?.findViewById<RelativeLayout>(R.id.relPlayer_share)?.setOnClickListener {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val intent = Intent()
            // TODO: Add this to resource strings
            val defaultInstanceUrl = "https://piped.kavin.rocks/"
            var url = "${defaultInstanceUrl}watch?v=$videoId"
            val instance = sharedPreferences.getString("instance", defaultInstanceUrl)!!

            if (instance != defaultInstanceUrl) {
                url += "&instance=${URLEncoder.encode(instance, "UTF-8")}"
            }

            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, url)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share Url To:"))
        }
    }

    private fun initializeExoPlayer(mediaSource: MediaSource?) {
        exoPlayer = ExoPlayer.Builder(requireView().context)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()

        with(exoPlayerView) {
            setShowSubtitleButton(true)
            setShowNextButton(false)
            setShowPreviousButton(false)
            controllerHideOnTouch = true
            player = exoPlayer
        }

        mediaSource?.let { exoPlayer.setMediaSource(it) }

        with(exoPlayer) {
            addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    exoPlayerView.keepScreenOn = !(
                        playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED ||
                            !playWhenReady
                        )

                    if (playWhenReady && playbackState == Player.STATE_READY) {
                        view?.findViewById<ImageView>(R.id.play_imageView)?.setImageResource(R.drawable.ic_pause)
                    } else if (playWhenReady) {
                        view?.findViewById<ImageView>(R.id.play_imageView)?.setImageResource(R.drawable.ic_play)
                    } else {
                        view?.findViewById<ImageView>(R.id.play_imageView)?.setImageResource(R.drawable.ic_play)
                    }
                }
            })
            prepare()
        }
    }

    private fun setSubscribedButton(button: MaterialButton, channelId: String) {
        lifecycleScope.launchWhenCreated {
            val response = apiClient.isSubscribed(channelId)
            val colorPrimary = TypedValue()
            val colorText = TypedValue()

            (context as Activity).theme.resolveAttribute(
                android.R.attr.colorPrimary,
                colorPrimary,
                true
            )

            (context as Activity).theme.resolveAttribute(R.attr.colorOnSurface, colorText, true)

            if (response == null) {
                return@launchWhenCreated
            }

            button.setOnClickListener {
                lifecycleScope.launchWhenCreated {
                    if (isSubscribed) {
                        if (apiClient.unsubscribe(channelId)) {
                            isSubscribed = false
                        }
                        button.text = getString(R.string.subscribe)
                        button.setTextColor(colorPrimary.data)
                    } else {
                        if (apiClient.subscribe(channelId)) {
                            isSubscribed = true
                        }
                        button.text = getString(R.string.unsubscribe)
                        button.setTextColor(colorPrimary.data)
                    }
                }
            }

            if (response) {
                isSubscribed = true
                button.text = getString(R.string.unsubscribe)
                button.setTextColor(colorText.data)
            }
        }
    }

    private fun getMostBitRate(audios: List<PipedStream>): Int {
        var bitrate = 0
        var index = 0

        for ((i, audio) in audios.withIndex()) {
            val q = audio.quality!!.replace(" kbps", "").toInt()
            if (q > bitrate) {
                bitrate = q
                index = i
            }
        }
        return index
    }
}
