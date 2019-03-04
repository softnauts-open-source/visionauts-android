package com.softnauts.visionauts.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.softnauts.visionauts.R
import com.softnauts.visionauts.common.SimpleOnSeekBarChangeListener
import com.softnauts.visionauts.common.ViewModelFactory
import com.softnauts.visionauts.data.model.RangeSettingsModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * Settings screen activity class.
 */
class SettingsActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(SettingsViewModel::class.java)

        setupViews()
        subscribe()
    }

    override fun onDestroy() {
        unsubscribe()
        super.onDestroy()
    }

    /**
     * Method used to set views values, listeners etc.
     */
    private fun setupViews() {
        buttonBack.setOnClickListener { onBackPressed() }
        seekBar.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.saveRange(progress.toFloat() / 100f)
            }
        })
    }

    /**
     * Method used to register subscription to viewmodel's live data.
     */
    private fun subscribe() {
        viewModel.rangeLiveData.observe(this, Observer { it ->
            it?.apply {
                textProgress.text = getString(R.string.range_progress, this.toString())
                EventBus.getDefault().post(RangeSettingsModel(this))
            }
        })
        viewModel.rangeSingleEvent.observe(this, Observer { it ->
            it?.getContentIfNotHandled()?.apply {
                seekBar.progress = Math.round(this * 100f)
            }
        })
    }

    /**
     * Method used to unregister subscription to viewmodel's live data.
     */
    private fun unsubscribe(){
        viewModel.rangeLiveData.removeObservers(this)
        viewModel.rangeSingleEvent.removeObservers(this)
    }

    /**
     * Method used to launch SettingsActivity.
     */
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}
