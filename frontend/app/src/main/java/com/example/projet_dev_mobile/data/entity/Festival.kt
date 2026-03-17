package com.example.projet_dev_mobile.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Festival(
    @SerialName("festival_id")
    val id: Int = 0,

    @SerialName("festival_nom")
    val nom: String,

    val date_debut: String,
    val date_fin: String,

    val stock_tables_petites: Int = 0,
    val stock_tables_grandes: Int = 0,
    val stock_tables_mairie: Int = 0
) {
    val totalTables: Int
        get() = stock_tables_petites + stock_tables_grandes + stock_tables_mairie
}