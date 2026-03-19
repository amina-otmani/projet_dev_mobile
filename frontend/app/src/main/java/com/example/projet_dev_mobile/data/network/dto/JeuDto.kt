package com.example.projet_dev_mobile.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.example.projet_dev_mobile.data.entity.Jeu
import com.example.projet_dev_mobile.data.entity.enum.GameType

@Serializable
data class JeuDto(
    val id: Int = 0,
    val editeur_id: Int,
    val nom: String,

    @SerialName("typeg")
    val typeG: GameType,

    val age_min: Int,
    val age_max: Int
) {
    fun toEntity(): Jeu {
        return Jeu(
            id = this.id,
            editeur_id = this.editeur_id,
            nom = this.nom,
            typeG = this.typeG,
            age_min = this.age_min,
            age_max = this.age_max
        )
    }
}

fun Jeu.toDto(): JeuDto {
    return JeuDto(
        id = this.id,
        editeur_id = this.editeur_id,
        nom = this.nom,
        typeG = this.typeG,
        age_min = this.age_min,
        age_max = this.age_max
    )
}