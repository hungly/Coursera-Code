package com.bennyplo.designgraphicswithopengl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private var glView: MyView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main);
        glView = MyView(this)
        //setContentView(R.layout.activity_fullscreen);
        setContentView(glView)
        //set full screen
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
