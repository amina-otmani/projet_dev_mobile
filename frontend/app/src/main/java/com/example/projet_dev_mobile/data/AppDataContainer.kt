    package com.example.projet_dev_mobile.data

    import android.content.Context
    import com.example.projet_dev_mobile.data.network.RetrofitInstance
    import com.example.projet_dev_mobile.data.repository.AdminRepository
    import com.example.projet_dev_mobile.data.repository.EditeursRepository
    import com.example.projet_dev_mobile.data.repository.FestivalsRepository
    import com.example.projet_dev_mobile.data.repository.JeuxRepository
    import com.example.projet_dev_mobile.data.repository.SuiviRepository

    interface AppContainer {
        val festivalsRepository: FestivalsRepository
        val editeursRepository: EditeursRepository
        val jeuxRepository: JeuxRepository
        val suiviRepository: SuiviRepository
        val adminRepository: AdminRepository
    }

    class AppDataContainer(private val context: Context) : AppContainer {

        private val apiService by lazy {
            RetrofitInstance.getApiService(context)
        }

        // injection du service dans les repository
        override val festivalsRepository: FestivalsRepository by lazy {
            FestivalsRepository(apiService)
        }

        override val editeursRepository: EditeursRepository by lazy {
            EditeursRepository(apiService)
        }

        override val jeuxRepository: JeuxRepository by lazy {
            JeuxRepository(apiService)
        }

        override val suiviRepository: SuiviRepository by lazy {
            SuiviRepository(apiService)
        override val adminRepository: AdminRepository by lazy {
            AdminRepository(apiService)
        }
    }