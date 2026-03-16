package com.example.projet_dev_mobile.utils

import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

fun formatLongDate(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH)
    return sdf.format(date)
}