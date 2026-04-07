package com.example.projet_dev_mobile.data.repository

import android.util.Log
import com.example.projet_dev_mobile.data.entity.enum.EtatSuivi
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
                Log.w("SuiviRepository", "getSuivisByFestival($festivalId) failed with HTTP ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("SuiviRepository", "Error in getSuivisByFestival($festivalId)", e)
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
            if (!response.isSuccessful) {
                Log.w(
                    "SuiviRepository",
                    "prendreContact failed with HTTP ${response.code()} for festivalId=$festivalId, editeurId=$editeurId"
                )
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(
                "SuiviRepository",
                "Error in prendreContact(festivalId=$festivalId, editeurId=$editeurId)",
                e
            )
            false
        }
    }

    suspend fun updateSuivi(
        festivalId: Int,
        editeurId: Int,
        etat: EtatSuivi,
        compteRendu: String? = null,
        responsableId: Int? = null
    ): Boolean {
        return try {
            val response = apiService.updateSuivi(
                request = UpdateSuiviRequest(
                    festival_id = festivalId,
                    editeur_id = editeurId,
                    etat = etat.name,
                    compte_rendu = compteRendu,
                    responsable_id = responsableId
                )
            )
            if (!response.isSuccessful) {
                Log.w(
                    "SuiviRepository",
                    "updateSuivi failed with HTTP ${response.code()} for festivalId=$festivalId, editeurId=$editeurId, etat=${etat.name}"
                )
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(
                "SuiviRepository",
                "Error in updateSuivi(festivalId=$festivalId, editeurId=$editeurId, etat=${etat.name})",
                e
            )
            false
        }
    }
}
