package uz.tubeme.app

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import uz.tubeme.app.data.local.UserPrefs
import uz.tubeme.app.ui.TubeMeRoot
import uz.tubeme.app.ui.theme.TubeMeTheme
import uz.tubeme.app.util.LocaleHelper
import uz.tubeme.app.util.LocalLanguage

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = runBlocking { UserPrefs.get(newBase).languageFlow.first() }
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        val prefs = UserPrefs.get(this)
        setContent {
            val theme by prefs.themeFlow.collectAsState(initial = "dark")
            val language by prefs.languageFlow.collectAsState(initial = "uz")
            TubeMeTheme(useDark = theme != "light") {
                CompositionLocalProvider(LocalLanguage provides language) {
                    TubeMeRoot()
                }
            }
        }
    }
}
