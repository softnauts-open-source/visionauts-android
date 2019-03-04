package com.softnauts.visionauts.ui.help

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.softnauts.visionauts.R
import kotlinx.android.synthetic.main.activity_help.*

/**
 * Help screen activity class.
 */
class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        buttonBack.setOnClickListener { onBackPressed() }
    }

    /**
     * Method used to launch HelpActivity.
     */
    companion object {
        fun start(context: Context){
            context.startActivity(Intent(context, HelpActivity::class.java))
        }
    }
}
