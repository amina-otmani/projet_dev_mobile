package com.example.projet_dev_mobile.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.entity.enum.RoleType
import com.example.projet_dev_mobile.data.network.dto.UserDto
import com.example.projet_dev_mobile.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val adminRepository: AdminRepository) : ViewModel() {

    var users = mutableStateListOf<UserDto>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            isLoading = true
            val fetchedUsers = adminRepository.getAllUsers()
            if (fetchedUsers != null) {
                users.clear()
                users.addAll(fetchedUsers)
            }
            isLoading = false
        }
    }

    fun updateUserRole(userId: Int, newRole: RoleType){
        viewModelScope.launch {
            val success = adminRepository.updateUserRole(userId, newRole)
            if (success) fetchUsers()
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            val success = adminRepository.ddeleteUser(userId)
            if (success) fetchUsers()
        }
    }

}