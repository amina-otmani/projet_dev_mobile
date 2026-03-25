package com.example.projet_dev_mobile

import android.app.Application
import com.example.projet_dev_mobile.data.AppContainer
import com.example.projet_dev_mobile.data.AppDataContainer

class FestivalApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // On initialise le container en lui passant le contexte de l'application
        container = AppDataContainer(this)
    }
}