package com.example.projet_dev_mobile.data.repository

import com.example.projet_dev_mobile.data.entity.enum.RoleType
import com.example.projet_dev_mobile.data.network.APIService
import com.example.projet_dev_mobile.data.network.RoleUpdateClick
import com.example.projet_dev_mobile.data.network.dto.UserDto

class AdminRepository(private val apiService: APIService) {
    suspend fun getAllUsers(): List<UserDto>? {
        val reponse = apiService.getUsers()
        return if (reponse.isSuccessful) reponse.body() else null
    }

    suspend fun updateUserRole(userId: Int, role: RoleType): Boolean {
        val reponse = apiService.updateUserRole(userId, RoleUpdateClick(role))
        return reponse.isSuccessful
    }

    suspend fun deleteUser(userId: Int): Boolean {
        val reponse = apiService.deleteUser(userId)
        return reponse.isSuccessful
    }
}