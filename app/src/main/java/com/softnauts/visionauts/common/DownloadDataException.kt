package com.softnauts.visionauts.common

import java.lang.Exception

/**
 * Exception class thrown when there is no way to download needed data from server.
 */
class DownloadDataException(message: String) : Exception(message)