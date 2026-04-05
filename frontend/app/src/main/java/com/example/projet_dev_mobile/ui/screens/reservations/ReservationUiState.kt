package com.example.projet_dev_mobile.ui.screens.reservation

import com.example.projet_dev_mobile.data.network.dto.ReservationDto

data class ReservationUiState(
    val isLoading: Boolean = false,
    val reservations: List<ReservationDto> = emptyList(),
    val errorMessage: String? = null
)