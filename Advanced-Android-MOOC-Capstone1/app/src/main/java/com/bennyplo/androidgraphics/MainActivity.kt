package com.bennyplo.androidgraphics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mMyView: MyView? = null // A custom view for drawing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Replace the view with my custom designed view
        mMyView = MyView(this)
        setContentView(mMyView)
    }

}