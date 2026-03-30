package com.example.projet_dev_mobile.utils

fun String.matchesSearchQuery(query: String): Boolean {
    val normalized = query.trim()
    if (normalized.isBlank()) return true
    return this.startsWith(normalized, ignoreCase = true)
}