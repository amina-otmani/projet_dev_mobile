package com.example.projet_dev_mobile.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ZonePlan(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("zone_tarifaire_id")
    val zoneTarifaireId: Int = 0,

    val nom: String,

    @SerialName("nombre_tables")
    val nbTables: Int
)