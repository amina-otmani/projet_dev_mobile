package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// pour que le nom soit unique
@Entity(indices = [Index(value = ["nom"], unique = true)] )
data class Festival(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nom: String,
    val date_debut: Long,
    val date_fin: Long,
    val lieu: String,
    val stock_tables_petites: Int = 0,
    val stock_tables_grandes: Int = 0,
    val stock_tables_mairie: Int = 0
) {
    val totalTables: Int
        get() = stock_tables_petites + stock_tables_grandes + stock_tables_mairie
}
