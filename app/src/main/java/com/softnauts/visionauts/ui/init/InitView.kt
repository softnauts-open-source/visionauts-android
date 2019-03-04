package com.softnauts.visionauts.ui.init

/**
 * InitActivity view interface.
 */
interface InitView {
    /**
     * Method used to show progress bar loader.
     */
    fun showLoader()
    /**
     * Method used to hide progress bar loader.
     */
    fun hideLoader()
    /**
     * Method used to show error message dialog.
     */
    fun showErrorMessage(message: String)
    /**
     * Method used to show error message dialog when downloading data for the first time.
     */
    fun showFirstRunDownloadDataError()
}