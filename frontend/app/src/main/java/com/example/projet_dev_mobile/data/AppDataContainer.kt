    package com.example.projet_dev_mobile.data

    import android.content.Context
    import com.example.projet_dev_mobile.data.network.RetrofitInstance
    import com.example.projet_dev_mobile.data.repository.FestivalsRepository

    interface AppContainer {
        val festivalsRepository: FestivalsRepository
    }

    class AppDataContainer(private val context: Context) : AppContainer {

        private val apiService by lazy {
            RetrofitInstance.getApiService(context)
        }

        // injection du service dans les repository
        override val festivalsRepository: FestivalsRepository by lazy {
            FestivalsRepository(apiService)
        }
    }