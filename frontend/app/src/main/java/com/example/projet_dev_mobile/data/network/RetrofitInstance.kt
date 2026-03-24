package com.example.projet_dev_mobile.data.network

import android.content.Context
import com.example.projet_dev_mobile.BuildConfig
import com.example.projet_dev_mobile.data.local.TokenManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.URL

object RetrofitInstance {
    private val BASE_URL = BuildConfig.BASE_URL

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Volatile
    private var apiService: APIService? = null

    fun getApiService(context: Context): APIService {
        val existing = apiService
        if (existing != null) return existing

        return synchronized(this) {
            val cached = apiService
            if (cached != null) return cached

            val tokenManager = TokenManager(context)

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val host = try {
                URL(BASE_URL).host
            } catch (e: Exception) {
                ""
            }

            val clientBuilder = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(AuthInterceptor(tokenManager))
                .hostnameVerifier { hostname, _ -> hostname == host }

            val client = clientBuilder.build()
            val contentType = "application/json".toMediaType()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(json.asConverterFactory(contentType))
                .client(client)
                .build()

            val service = retrofit.create(APIService::class.java)
            apiService = service
            service
        }
    }
}