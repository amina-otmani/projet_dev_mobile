package com.example.projet_dev_mobile.data.network

import android.content.Context
import com.example.projet_dev_mobile.data.local.TokenManager
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

object RetrofitInstance {
    // Remplacement du port 8080 par 4000
    private const val BASE_URL = "http://10.0.2.2:4000/api/"

    fun getApiService(context: Context): APIService {
        val tokenManager = TokenManager(context)

        // 1. Intercepteur de logs pour voir ce qui se passe
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. Gestionnaire de cookies automatique
        val cookieManager = CookieManager()
        val cookieJar = JavaNetCookieJar(cookieManager)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(tokenManager))
            .cookieJar(cookieJar) // Ajout des cookies
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(APIService::class.java)
    }
}