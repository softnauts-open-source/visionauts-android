package com.softnauts.visionauts.common

import java.io.IOException

/**
 * Exception class thrown when there is connection problem when making api call.
 */
open class NetworkConnectionException(message: String) : IOException(message)