package uz.tubeme.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import uz.tubeme.app.data.api.SheetsClient
import uz.tubeme.app.data.api.SheetsParser
import uz.tubeme.app.data.model.Collection
import uz.tubeme.app.data.model.NewsItem
import uz.tubeme.app.data.model.Section
import uz.tubeme.app.data.model.Video

/**
 * Single source of truth backed by Google Sheets.
 * Provides in-memory cache + reactive flows.
 */
object TubeMeRepository {

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    val collections: StateFlow<List<Collection>> = _collections.asStateFlow()

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList())
    val news: StateFlow<List<NewsItem>> = _news.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    suspend fun refreshAll(force: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true
            val v = fetchVideos()
            val c = fetchCollections()
            val n = fetchNews()
            _videos.value = v
            _collections.value = c
            _news.value = n
            _lastError.value = null
        } catch (e: Exception) {
            _lastError.value = e.localizedMessage ?: "Tarmoqda xatolik"
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchVideos(): List<Video> = withRetry {
        val raw = SheetsClient.api.getSheet(SheetsClient.SHEET_ID, gid = SheetsClient.GID_VIDEOS)
        SheetsParser.parseVideos(raw)
    }

    private suspend fun fetchCollections(): List<Collection> = withRetry {
        val raw = SheetsClient.api.getSheet(SheetsClient.SHEET_ID, gid = SheetsClient.GID_COLLECTIONS)
        SheetsParser.parseCollections(raw)
    }

    private suspend fun fetchNews(): List<NewsItem> = withRetry {
        val raw = SheetsClient.api.getSheet(SheetsClient.SHEET_ID, gid = SheetsClient.GID_NEWS)
        SheetsParser.parseNews(raw)
    }

    private suspend fun <T> withRetry(retries: Int = 3, block: suspend () -> T): T {
        var lastErr: Exception? = null
        repeat(retries) { attempt ->
            try { return block() } catch (e: Exception) {
                lastErr = e
                kotlinx.coroutines.delay(400L * (attempt + 1))
            }
        }
        throw lastErr ?: RuntimeException("Failed")
    }

    /** Find a single video by id from cached list */
    fun findVideo(videoId: String?): Video? {
        if (videoId.isNullOrBlank()) return null
        return _videos.value.firstOrNull { it.videoId == videoId }
    }

    /** Group videos by section, preserving first-seen order */
    fun sections(): List<Section> {
        val map = linkedMapOf<String, MutableList<Video>>()
        val typeMap = mutableMapOf<String, String>()
        _videos.value.forEach { v ->
            if (v.section.isBlank()) return@forEach
            if (!map.containsKey(v.section)) {
                map[v.section] = mutableListOf()
                typeMap[v.section] = v.type
            }
            map[v.section]!!.add(v)
        }
        return map.map { (name, list) -> Section(name, typeMap[name] ?: "carousel", list) }
    }

    fun heroes(): List<Video> = _videos.value.filter { it.hero && it.thumbnail.isNotBlank() }

    fun moreLikeThis(video: Video, limit: Int = 8): List<Video> {
        val list = _videos.value.filter {
            it.videoId != video.videoId &&
                    (it.genre.equals(video.genre, true) || it.section.equals(video.section, true))
        }
        return list.shuffled().take(limit)
    }
}
