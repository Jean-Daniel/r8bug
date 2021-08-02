package com.xooloo.r8bug.webservices

import android.content.Context
import com.squareup.moshi.Moshi
import com.xooloo.r8bug.R
import com.xooloo.r8bug.model.messages.ModelAdapter
import com.xooloo.r8bug.model.util.DateTimeAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiHttpClient

@Module
@InstallIn(SingletonComponent::class)
object WebServicesModule {

    @Provides
    @Singleton
    fun moshi(): Moshi {
        return Moshi.Builder()
            .add(ModelAdapter())
            .add(DateTimeAdapter())
            .build()
    }

    @Provides
    @Singleton
    @ApiHttpClient
    fun apiClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        return builder
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private inline fun <reified T> validatingApi(
        context: Context,
        @ApiHttpClient client: OkHttpClient,
        moshi: Moshi
    ): WebService<T> {
        val srv = Retrofit.Builder()
            .baseUrl(context.getString(R.string.url_api))
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build().create(T::class.java)
        return WebService(context, srv)
    }

    @Provides
    @Singleton
    fun messageApi(
        @ApplicationContext context: Context,
        @ApiHttpClient client: OkHttpClient,
        moshi: Moshi
    ): WebService<MessageApi> = validatingApi(context, client, moshi)
}

