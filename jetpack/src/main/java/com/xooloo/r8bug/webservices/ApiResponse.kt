package com.xooloo.r8bug.webservices


import android.content.Context
import retrofit2.Response
import timber.log.Timber

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
sealed class ApiResponse<T> {
    companion object {
        fun <T> create(ctxt: Context, response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse(response.raw())
                } else {
                    ApiSuccessResponse(body, response.raw())
                }
            } else if (response.code() >= 500) {
                // May be retry later
                Timber.w("API internal server error")
                ApiErrorResponse(
                    "internal server error: ${response.code()}",
                    false,
                    response = response.raw()
                )
            } else {
                val msg = ""
                Timber.w("API error: ${response.code()} ($msg)")
                ApiErrorResponse(msg, true, response = response.raw())
            }
        }

        // low-level error (invalid response, network error, …)
        fun <T> error(msg: String, error: Throwable, isApiError: Boolean): ApiResponse<T> {
            return ApiErrorResponse(msg, isApiError, error = error)
        }
    }
}

/**
 * separate class for HTTP 204 resposes so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T>(val response: okhttp3.Response) : ApiResponse<T>()

data class ApiSuccessResponse<T>(val body: T, val response: okhttp3.Response) : ApiResponse<T>()

// isApiError:
//      true: Server properly handle the response but reply with an error. This request must not be retried.
//      false: Network or server failure error that should trigger a retry if possible.
// Note: status is relevant only if isApiError is true.
data class ApiErrorResponse<T>(
    val errorMessage: String,
    val isApiError: Boolean,
    val error: Throwable? = null,
    val response: okhttp3.Response? = null
) : ApiResponse<T>() {

    val status: Int
        get() = response?.code ?: 500

    override fun toString(): String {
        return "Api error: { $errorMessage, status: $status, isApiError: $isApiError }"
    }
}
