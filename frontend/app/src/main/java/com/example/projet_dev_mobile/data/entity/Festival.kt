package com.example.projet_dev_mobile.data.entity

data class Festival(
    val id: Int = 0,
    val nom: String,
    val date_debut: String,
    val date_fin: String,

    val stock_tables_petites: Int = 0,
    val stock_tables_grandes: Int = 0,
    val stock_tables_mairie: Int = 0,

    val zonesTarifaires: List<ZoneTarifaire> = emptyList()
) {
    val totalTables: Int
        get() = stock_tables_petites + stock_tables_grandes + stock_tables_mairie
}