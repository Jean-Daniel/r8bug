package com.xooloo.r8bug.webservices

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.PUT
import retrofit2.http.Path
import timber.log.Timber


interface MessageApi {
    @FormUrlEncoded
    @PUT("messages/chat/{chat_id}")
    fun sendBatchAck(
        @Path("chat_id") chatId: Long,
        @Field("message_id") untilMessageId: Long,
        @Field("action") action: String
    ): Call<Void>
}

object MessageWebService {


    @HiltWorker
    class BatchNotify @AssistedInject constructor(
        @Assisted appContext: Context,
        @Assisted params: WorkerParameters,
        private val api: WebService<MessageApi>
    ) : CoroutineWorker(appContext, params) {

        override suspend fun doWork(): Result {
            Timber.i("Start sending distributed message ACKs")

            val chat = inputData.getLong("chat", 0)
            val isRead = inputData.getBoolean("read", false)
            val untilMessageId = inputData.getLong("untilMessageId", 0)

            val response =
                api.call { sendBatchAck(chat, untilMessageId, if (isRead) "read" else "ack") }

            // On API error, just ignore the error by marking the message as distributed.
            if (response is ApiEmptyResponse || (response is ApiErrorResponse && response.isApiError)) {
                Timber.i("This is great: %s", response)
            } else {
                Timber.e("Message ack failed: %s", response)
                // some error not related to API usage -> network error, server down, … ?
                return Result.retry()
            }

            return Result.success()
        }
    }

}

