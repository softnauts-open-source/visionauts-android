package com.softnauts.visionauts.common

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor class used to check network connection state.
 */
class NetworkStateInterceptor @Inject constructor(
        private val connectivityManager: ConnectivityManager) :
        Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!connectivityManager.isNetworkAvailable()) {
            throw NetworkConnectionException("no internet connection")
        }

        val response = chain.proceed(chain.request())

        if (response.isSuccessful) {
            return response
        } else {
            val code = response.code()
            val message = response.message()
            throw NetworkConnectionException(message)
        }
    }
}
