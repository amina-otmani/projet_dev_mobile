package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Editeur(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nom: String
)