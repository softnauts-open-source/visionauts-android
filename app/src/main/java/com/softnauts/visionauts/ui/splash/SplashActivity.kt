package com.softnauts.visionauts.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.softnauts.visionauts.ui.init.InitActivity

/**
 * Splash screen activity class.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InitActivity.start(this)
        finish()
    }

}
