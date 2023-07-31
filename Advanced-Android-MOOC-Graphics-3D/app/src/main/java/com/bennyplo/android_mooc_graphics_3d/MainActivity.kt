package com.bennyplo.android_mooc_graphics_3d

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mMyView: MyView? = null //a custom view for drawing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main);
        supportActionBar?.hide() //hide the title bar
        mMyView = MyView(this)
        setContentView(mMyView)
    }

    override fun onResume() {
        super.onResume()
        mMyView?.startRender()
    }

    override fun onPause() {
        super.onPause()
        mMyView?.stopRender()
    }
}
