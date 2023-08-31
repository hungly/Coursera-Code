package com.bennyplo.virtualreality

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private var glView: MyView? = null

    override fun onConfigurationChanged(newConfig: Configuration) { //ensure that no matter which orientation, the app will use full screen!
            super.onConfigurationChanged(newConfig)
            setupFullscreen()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            glView = MyView(this)
            setContentView(glView)
            // set full screen
            setupFullscreen()
        }

    private fun setupFullscreen() {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            glView?.let {
                WindowInsetsControllerCompat(window, it)
            }?.let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
}