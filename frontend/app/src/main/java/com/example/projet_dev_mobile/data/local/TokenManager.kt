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

    // --- NOUVELLES MÉTHODES ---
    fun saveRole(role: String) {
        prefs.edit().putString("user_role", role).apply()
    }

    fun getRole(): String? = prefs.getString("user_role", null)

    fun saveLogin(login: String) {
        prefs.edit().putString("user_login", login).apply()
    }

    fun getLogin(): String? = prefs.getString("user_login", null)

    // --- ANCIENNES MÉTHODES (Gardées au cas où d'autres parties du code l'utilisent) ---
    fun saveTokens(token: String, refreshToken: String) {
        prefs.edit().putString("jwt_token", token).apply()
        prefs.edit().putString("refresh_token", refreshToken).apply()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    // On efface tout lors de la déconnexion
    fun clear() = prefs.edit().clear().apply()
}