package com.github.libretube.home

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.github.libretube.R
import com.github.libretube.databinding.ActivityMainBinding
import com.github.libretube.player.PlayerFragment
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity() : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    lateinit var binding: ActivityMainBinding

    private val _navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment }
    val navController get() = _navHostFragment.navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot &&
            intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
            intent.action != null &&
            intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        DynamicColors.applyToActivitiesIfAvailable(application)
        setContentView(binding.root)
        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.bottomNav.setupWithNavController(navController)
        viewModel.preferences =
            getSharedPreferences(getString(R.string.preference_instance), Context.MODE_PRIVATE)
        viewModel.setOrientationMode(resources.configuration.orientation)

        initNavigation()
    }

    private fun initNavigation() {
        setupCurrentVideoClicked()
    }

    fun bindPlayerChannelClickedEvent(
        lifecycleOwner: LifecycleOwner,
        navController: NavController
    ) {
        viewModel.channelClickedEvent.observe(lifecycleOwner) {
            if (!it.isNullOrBlank()) {
                val args = Bundle()
                args.putString("id", it)
                navController.navigate(R.id.channelFragment, args)
                // TODO: Change this logic to act as an event
                viewModel.setChannel("")
            }
        }
    }

    private fun setupCurrentVideoClicked() {
        viewModel.videoUrl.observe(this) {
            if (!it.isNullOrBlank()) {
                val args = Bundle()
                args.putString("videoUrl", it)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<PlayerFragment>(R.id.playerFragmentContainer, "player", args)
                    disallowAddToBackStack()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val orientation = newConfig.orientation

        viewModel.setOrientationMode(orientation)

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            println("Portrait")
            unsetFullscreen()
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            println("Landscape")
            setFullscreen()
        }
    }

    private fun setFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
    }

    private fun unsetFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
            window.insetsController?.apply {
                show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }
    }
}
