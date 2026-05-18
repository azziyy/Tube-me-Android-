package uz.tubeme.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "tubeme_prefs")

/**
 * Persistent storage for favorites, saved, history, progress, settings.
 * Backed by DataStore (Preferences).
 */
class UserPrefs(private val context: Context) {

    private object Keys {
        val FAVORITES = stringSetPreferencesKey("favorites")     // set of videoId
        val SAVED = stringSetPreferencesKey("saved")             // set of videoId
        val HISTORY = stringPreferencesKey("history")            // JSON array of {id, ts}
        val PROGRESS = stringPreferencesKey("progress")          // JSON object: {id: {current, duration, ts}}
        val READ_NEWS = stringSetPreferencesKey("read_news")

        val THEME = stringPreferencesKey("theme")                // dark / light
        val LANGUAGE = stringPreferencesKey("language")          // uz / ru / en
        val QUALITY = stringPreferencesKey("quality")
        val SPEED = stringPreferencesKey("speed")
        val AUTOPLAY = stringPreferencesKey("autoplay")
        val PIP = stringPreferencesKey("pip")
        val NOTIFICATIONS = stringPreferencesKey("notifications")
        val PROFILE_NAME = stringPreferencesKey("profile_name")
    }

    // --- Favorites ---
    val favoritesFlow: Flow<Set<String>> =
        context.dataStore.data.map { it[Keys.FAVORITES] ?: emptySet() }

    suspend fun toggleFavorite(videoId: String): Boolean {
        var added = false
        context.dataStore.edit { p ->
            val cur = p[Keys.FAVORITES]?.toMutableSet() ?: mutableSetOf()
            if (cur.contains(videoId)) cur.remove(videoId) else { cur.add(videoId); added = true }
            p[Keys.FAVORITES] = cur
        }
        return added
    }

    // --- Saved ---
    val savedFlow: Flow<Set<String>> =
        context.dataStore.data.map { it[Keys.SAVED] ?: emptySet() }

    suspend fun toggleSaved(videoId: String): Boolean {
        var added = false
        context.dataStore.edit { p ->
            val cur = p[Keys.SAVED]?.toMutableSet() ?: mutableSetOf()
            if (cur.contains(videoId)) cur.remove(videoId) else { cur.add(videoId); added = true }
            p[Keys.SAVED] = cur
        }
        return added
    }

    // --- History ---
    /** Returns ordered list of videoIds (most recent first) */
    val historyFlow: Flow<List<String>> =
        context.dataStore.data.map { p ->
            val json = p[Keys.HISTORY] ?: return@map emptyList()
            try {
                val arr = JSONArray(json)
                val out = mutableListOf<String>()
                for (i in 0 until arr.length()) {
                    val o = arr.optJSONObject(i) ?: continue
                    val id = o.optString("id"); if (id.isNotBlank()) out.add(id)
                }
                out
            } catch (e: Exception) { emptyList() }
        }

    suspend fun pushHistory(videoId: String) {
        context.dataStore.edit { p ->
            val arr = try { JSONArray(p[Keys.HISTORY] ?: "[]") } catch (e: Exception) { JSONArray() }
            // Remove duplicates
            val newArr = JSONArray()
            newArr.put(JSONObject().put("id", videoId).put("ts", System.currentTimeMillis()))
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                if (o.optString("id") != videoId && newArr.length() < 100) newArr.put(o)
            }
            p[Keys.HISTORY] = newArr.toString()
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { it[Keys.HISTORY] = "[]" }
    }

    // --- Progress ---
    val progressFlow: Flow<Map<String, Triple<Long, Long, Long>>> =
        context.dataStore.data.map { p ->
            val json = p[Keys.PROGRESS] ?: return@map emptyMap()
            try {
                val obj = JSONObject(json)
                val out = mutableMapOf<String, Triple<Long, Long, Long>>()
                obj.keys().forEach { k ->
                    val o = obj.optJSONObject(k) ?: return@forEach
                    out[k] = Triple(o.optLong("current"), o.optLong("duration"), o.optLong("ts"))
                }
                out
            } catch (e: Exception) { emptyMap() }
        }

    suspend fun setProgress(videoId: String, current: Long, duration: Long) {
        if (videoId.isBlank() || duration < 5000) return
        context.dataStore.edit { p ->
            val obj = try { JSONObject(p[Keys.PROGRESS] ?: "{}") } catch (e: Exception) { JSONObject() }
            obj.put(videoId, JSONObject()
                .put("current", current)
                .put("duration", duration)
                .put("ts", System.currentTimeMillis()))
            p[Keys.PROGRESS] = obj.toString()
        }
    }

    suspend fun clearProgress() { context.dataStore.edit { it[Keys.PROGRESS] = "{}" } }

    // --- News read ---
    val readNewsFlow: Flow<Set<String>> =
        context.dataStore.data.map { it[Keys.READ_NEWS] ?: emptySet() }

    suspend fun markNewsRead(ids: Set<String>) {
        context.dataStore.edit { p ->
            val cur = p[Keys.READ_NEWS]?.toMutableSet() ?: mutableSetOf()
            cur.addAll(ids)
            p[Keys.READ_NEWS] = cur
        }
    }

    // --- Settings ---
    val themeFlow = context.dataStore.data.map { it[Keys.THEME] ?: "dark" }
    val languageFlow = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "uz" }
    val qualityFlow = context.dataStore.data.map { it[Keys.QUALITY] ?: "auto" }
    val speedFlow = context.dataStore.data.map { it[Keys.SPEED] ?: "1.0" }
    val autoplayFlow = context.dataStore.data.map { (it[Keys.AUTOPLAY] ?: "true") == "true" }
    val pipFlow = context.dataStore.data.map { (it[Keys.PIP] ?: "true") == "true" }
    val notifFlow = context.dataStore.data.map { (it[Keys.NOTIFICATIONS] ?: "true") == "true" }
    val profileNameFlow = context.dataStore.data.map { it[Keys.PROFILE_NAME] ?: "TubeMe User" }

    suspend fun setTheme(v: String) = context.dataStore.edit { it[Keys.THEME] = v }
    suspend fun setLanguage(v: String) = context.dataStore.edit { it[Keys.LANGUAGE] = v }
    suspend fun setQuality(v: String) = context.dataStore.edit { it[Keys.QUALITY] = v }
    suspend fun setSpeed(v: String) = context.dataStore.edit { it[Keys.SPEED] = v }
    suspend fun setAutoplay(v: Boolean) = context.dataStore.edit { it[Keys.AUTOPLAY] = v.toString() }
    suspend fun setPip(v: Boolean) = context.dataStore.edit { it[Keys.PIP] = v.toString() }
    suspend fun setNotifications(v: Boolean) = context.dataStore.edit { it[Keys.NOTIFICATIONS] = v.toString() }
    suspend fun setProfileName(v: String) = context.dataStore.edit { it[Keys.PROFILE_NAME] = v }

    companion object {
        @Volatile private var INSTANCE: UserPrefs? = null
        fun get(context: Context): UserPrefs =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPrefs(context.applicationContext).also { INSTANCE = it }
            }
    }
}
