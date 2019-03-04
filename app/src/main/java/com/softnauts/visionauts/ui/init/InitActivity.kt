package com.softnauts.visionauts.ui.init

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.afollestad.materialdialogs.MaterialDialog
import com.softnauts.visionauts.R
import com.softnauts.visionauts.ui.main.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_init.*
import javax.inject.Inject

/**
 * Init screen activity class.
 */
class InitActivity : DaggerAppCompatActivity(), InitView, InitRouter {

    @Inject
    lateinit var presenter: InitPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        presenter.onActivityCreate()
    }

    override fun onDestroy() {
        presenter.onActivityDestroy()
        super.onDestroy()
    }

    /**
     * Method used to show progress bar loader.
     */
    override fun showLoader() {
        progressBar.visibility = VISIBLE
    }

    /**
     * Method used to hide progress bar loader.
     */
    override fun hideLoader() {
        progressBar.visibility = INVISIBLE
    }

    /**
     * Method used to show error message dialog.
     */
    override fun showErrorMessage(message: String) {
        MaterialDialog(this).show {
            title(R.string.error)
            message(R.string.unknown_error)
            positiveButton(R.string.ok) {
                finish()
            }
        }
    }

    /**
     * Method used to show error message dialog when downloading data for the first time.
     */
    override fun showFirstRunDownloadDataError() {
        MaterialDialog(this).show {
            title(R.string.error)
            message(R.string.first_run_download_data_error)
            positiveButton(R.string.retry) {
                presenter.downloadBeaconData()
            }
            negativeButton(R.string.cancel) {
                finish()
            }
        }
    }

    /**
     * Method used to navigate to MainActivity.
     */
    override fun navigateToMainActivity() {
        MainActivity.start(this)
        finish()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, InitActivity::class.java))
        }
    }
}
