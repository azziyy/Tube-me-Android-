package uz.tubeme.app.data.model

/**
 * Video item from Google Sheets (videos tab)
 * Columns: section, type, title, description, thumbnail, video,
 *          genre, language, country, year, rating, videoId, hero
 */
data class Video(
    val section: String = "",
    val type: String = "carousel",
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val videoUrl: String = "",
    val genre: String = "",
    val language: String = "",
    val country: String = "",
    val year: String = "",
    val rating: String = "",
    val videoId: String = "",
    val hero: Boolean = false
)

/** A season inside a collection */
data class Season(
    val name: String = "",
    val videoIds: List<String> = emptyList()
)

/**
 * Collection item from Google Sheets (collections tab)
 * Columns: title, description, thumbnail, season, videoIds(comma), collectionId
 */
data class Collection(
    val collectionId: String = "",
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val seasons: List<Season> = emptyList()
)

/**
 * News item (news tab)
 * Columns: title, description, image
 */
data class NewsItem(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val image: String = ""
)

/** A logical home-section with its items */
data class Section(
    val name: String,
    val type: String,
    val items: List<Video>
)

/** Watch progress for "Continue Watching" */
data class Progress(
    val videoId: String,
    val current: Long,
    val duration: Long,
    val updatedAt: Long
)

/** Detect what kind of media URL we have */
enum class MediaKind { YOUTUBE, HLS, MP4, UNKNOWN }

fun detectMediaKind(url: String): MediaKind {
    if (url.isBlank()) return MediaKind.UNKNOWN
    val low = url.lowercase()
    val yt = Regex("""(youtube\.com|youtu\.be|youtube-nocookie\.com)""")
    if (yt.containsMatchIn(low)) return MediaKind.YOUTUBE
    if (low.contains(".m3u8")) return MediaKind.HLS
    if (low.contains(".mp4") || low.contains(".mkv") || low.contains(".webm")) return MediaKind.MP4
    // fallback: try ExoPlayer for unknown direct URLs
    return MediaKind.MP4
}

/** Extract YouTube video id from a URL */
fun extractYouTubeId(url: String): String? {
    if (url.isBlank()) return null
    // youtu.be/<id>
    Regex("""youtu\.be/([A-Za-z0-9_-]{11})""").find(url)?.let { return it.groupValues[1] }
    // youtube.com/watch?v=<id>
    Regex("""[?&]v=([A-Za-z0-9_-]{11})""").find(url)?.let { return it.groupValues[1] }
    // youtube.com/embed/<id>
    Regex("""embed/([A-Za-z0-9_-]{11})""").find(url)?.let { return it.groupValues[1] }
    // shorts
    Regex("""shorts/([A-Za-z0-9_-]{11})""").find(url)?.let { return it.groupValues[1] }
    return null
}
