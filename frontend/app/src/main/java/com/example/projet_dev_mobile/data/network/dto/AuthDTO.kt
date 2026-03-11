package com.example.projet_dev_mobile.data.network.dto

import com.example.projet_dev_mobile.data.entity.enum.RoleType
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val login: String, // Le backend attend "login", pas "email"
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: Int,
    val login: String,
    val nom: String? = null,
    val prenom: String? = null,
    val email: String? = null,
    val role: RoleType
)