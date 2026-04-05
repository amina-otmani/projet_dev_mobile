package com.example.projet_dev_mobile.data.entity.enum

import kotlinx.serialization.Serializable

@Serializable
enum class EtatReservation {
    PRESENT,
    FACTUREE,
    PAYEE
}