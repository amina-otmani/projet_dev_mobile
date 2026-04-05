package com.example.projet_dev_mobile.data.repository

import com.example.projet_dev_mobile.data.network.APIService
import com.example.projet_dev_mobile.data.network.dto.PrendreContactRequest
import com.example.projet_dev_mobile.data.network.dto.SuiviDto
import com.example.projet_dev_mobile.data.network.dto.UpdateSuiviRequest

class SuiviRepository(private val apiService: APIService) {

    suspend fun getSuivisByFestival(festivalId: Int): List<SuiviDto>? {
        return try {
            val response = apiService.getSuivisByFestival(festivalId)
            if (response.isSuccessful) {
                response.body().orEmpty()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun prendreContact(festivalId: Int, editeurId: Int): Boolean {
        return try {
            val response = apiService.prendreContact(
                request = PrendreContactRequest(
                    festival_id = festivalId,
                    editeur_id = editeurId
                )
            )
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    suspend fun updateSuivi(
        festivalId: Int,
        editeurId: Int,
        etat: String,
        compteRendu: String? = null,
        responsableId: Int? = null
    ): Boolean {
        return try {
            val response = apiService.updateSuivi(
                request = UpdateSuiviRequest(
                    festival_id = festivalId,
                    editeur_id = editeurId,
                    etat = etat,
                    compte_rendu = compteRendu,
                    responsable_id = responsableId
                )
            )
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }
}
