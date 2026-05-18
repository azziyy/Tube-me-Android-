package uz.tubeme.app.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun wrap(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val cfg = Configuration(context.resources.configuration)
        cfg.setLocale(locale)
        cfg.setLayoutDirection(locale)
        return context.createConfigurationContext(cfg)
    }
}
