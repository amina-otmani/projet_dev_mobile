package com.example.projet_dev_mobile.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZoneTarifaire(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("festival_id")
    val festivalId: Int = 0,

    val nom: String,

    @SerialName("prix_table")
    val prixTable: Double,

    @SerialName("prix_m2")
    val prixM2: Double,

    @SerialName("zonesPlan")
    val zonesPlan: List<ZonePlan> = emptyList()
)