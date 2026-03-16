package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.projet_dev_mobile.data.entity.enum.RoleType

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val login: String,
    val password_hash: String,
    // valeur par défault
    val role: RoleType = RoleType.NO_ROLE
)