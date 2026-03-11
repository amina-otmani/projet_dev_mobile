package com.example.projet_dev_mobile.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(token: String, refreshToken: String) {
        prefs.edit().putString("jwt_token", token).apply()
        prefs.edit().putString("refresh_token", refreshToken).apply()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun clear() = prefs.edit().clear().apply()
}