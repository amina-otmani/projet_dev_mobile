package com.example.projet_dev_mobile.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun formatLongDate(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH)
            .withZone(ZoneId.systemDefault()) // Adapte l'heure au fuseau horaire du téléphone de l'utilisateur

        formatter.format(instant)
    } catch (e: Exception) {
        dateString
    }
}