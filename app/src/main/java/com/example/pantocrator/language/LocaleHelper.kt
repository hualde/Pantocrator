package com.example.pantocrator.language

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, language: String): Context {
        val locale = when (language) {
            "English" -> Locale("en")
            "Español" -> Locale("es")
            "Français" -> Locale("fr")
            "Italiano" -> Locale("it")
            "Português" -> Locale("pt")
            else -> Locale("es") // Español por defecto
        }
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }
} 