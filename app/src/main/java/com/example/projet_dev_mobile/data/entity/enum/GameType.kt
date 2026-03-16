package com.example.projet_dev_mobile.data.entity.enum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
enum class GameType {
    @SerialName("Action") ACTION,
    @SerialName("Aventure") AVENTURE,
    @SerialName("RPG") RPG,
    @SerialName("Reflexion") REFLEXION,
    @SerialName("Simulation") SIMULATION,
    @SerialName("Strategie") STRATEGIE,
    @SerialName("Sport") SPORT,
    @SerialName("Carte") CARTE
}