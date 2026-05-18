package uz.tubeme.app.util

import uz.tubeme.app.data.model.Video
import uz.tubeme.app.data.model.extractYouTubeId

/**
 * If the user puts a YouTube URL in the "thumbnail" column blank, try to derive
 * a thumbnail from the video URL.
 */
fun Video.effectiveThumbnail(): String {
    if (thumbnail.isNotBlank()) return thumbnail
    extractYouTubeId(videoUrl)?.let { id ->
        return "https://i.ytimg.com/vi/$id/hqdefault.jpg"
    }
    return ""
}
