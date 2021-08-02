package com.xooloo.r8bug.webservices

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.awaitResponse
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException


suspend fun <T> apiCall(context: Context, call: Call<T>): ApiResponse<T> =
    withContext<ApiResponse<T>>(Dispatchers.IO) {
        // Don't keep context ref in async call, unless we are sure it will survive the call.
        val ctxt = context.applicationContext
        try {
            ApiResponse.create(ctxt, call.awaitResponse())
        } catch (ioe: IOException) {
            // A problem occurred talking to the server. May retry the request
            if (ioe is UnknownHostException) { // dns error generaly means network is down.
                Timber.e(ioe, "API network error")
            } else {
                val method = call.request().method
                val path = call.request().url.encodedPath
                Timber.e(ioe, "API network error: $method $path")
            }
            ApiResponse.error("", ioe, false)
        } catch (t: Throwable) {
            val method = call.request().method
            val path = call.request().url.encodedPath
            Timber.e(t, "API call error: $method $path")
            ApiResponse.error(t.message ?: "", t, true)
        }
    }

// Wrapper that allow attaching context to retrofit 'interface'
open class WebService<T>(val context: Context, val srv: T) {

    suspend inline fun <R> call(action: (T.() -> Call<R>)): ApiResponse<R> {
        return onResponse(apiCall(context, srv.action()))
    }

    @PublishedApi
    internal open fun <R> onResponse(response: ApiResponse<R>): ApiResponse<R> = response
}

