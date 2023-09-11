package com.bennyplo.virtualreality.ref

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

@Suppress("MaxLineLength")
class MainActivity : AppCompatActivity() {

    private var glView: MyView? = null
    private var mControlsView: View? = null

    override fun onConfigurationChanged(newConfig: Configuration) { //ensure that no matter which orientation, the app will use full screen!
        super.onConfigurationChanged(newConfig)
        mControlsView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        mControlsView!!.systemUiVisibility = uiOptions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main);
        glView = MyView(this)
        //setContentView(R.layout.activity_fullscreen);
        setContentView(glView)
        //set full screen
        mControlsView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        mControlsView!!.systemUiVisibility = uiOptions
    }

}