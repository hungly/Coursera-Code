package com.bennyplo.animation

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var _textureBitmap: Bitmap? = null
    private var glView: MyView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        imageFileSearch()
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

    override fun onPause() {
        super.onPause()
        glView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        glView?.onResume()
    }

    fun getTextureBitmap(): Bitmap? = _textureBitmap

    private fun getBitmapFile(filePath: Uri): Bitmap? {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(filePath, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val bitmap = android.graphics.BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return bitmap
    }

    private fun imageFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.data?.also { uri ->
                    try {
                        _textureBitmap = getBitmapFile(uri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }.launch(intent)
    }

}