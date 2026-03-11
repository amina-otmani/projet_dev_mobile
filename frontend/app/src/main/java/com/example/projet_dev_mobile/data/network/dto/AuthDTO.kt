package com.example.projet_dev_mobile.data.network.dto

import com.example.projet_dev_mobile.data.entity.enum.RoleType
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val login: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val message: String? = null,
    val user: UserDto? = null,
    val error: String? = null
)

@Serializable
data class UserDto(
    val id: Int,
    val login: String,
    val role: RoleType
)