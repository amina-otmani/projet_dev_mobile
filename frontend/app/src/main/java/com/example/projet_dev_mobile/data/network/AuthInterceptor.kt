package com.example.projet_dev_mobile.data.network

import com.example.projet_dev_mobile.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = tokenManager.getToken()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Cookie", "access_token=$token")
        }

        return chain.proceed(requestBuilder.build())
    }
}