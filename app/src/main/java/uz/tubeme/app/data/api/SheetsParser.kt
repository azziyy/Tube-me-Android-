package uz.tubeme.app.data.api

import org.json.JSONArray
import org.json.JSONObject
import uz.tubeme.app.data.model.Collection
import uz.tubeme.app.data.model.NewsItem
import uz.tubeme.app.data.model.Season
import uz.tubeme.app.data.model.Video

/**
 * Parse GVIZ JSON wrapper:
 *   /*O_o*/google.visualization.Query.setResponse({...});
 */
object SheetsParser {

    private fun unwrap(text: String): JSONObject? {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start < 0 || end <= start) return null
        return try {
            JSONObject(text.substring(start, end + 1))
        } catch (e: Exception) { null }
    }

    private fun cellString(rowCells: JSONArray, idx: Int): String {
        if (idx >= rowCells.length()) return ""
        val cell = rowCells.optJSONObject(idx) ?: return ""
        if (cell.isNull("v")) return ""
        val v = cell.opt("v") ?: return ""
        return v.toString().trim()
    }

    private fun toRows(json: JSONObject?): List<JSONArray> {
        val table = json?.optJSONObject("table") ?: return emptyList()
        val rows = table.optJSONArray("rows") ?: return emptyList()
        val out = mutableListOf<JSONArray>()
        for (i in 0 until rows.length()) {
            val row = rows.optJSONObject(i) ?: continue
            val cArr = row.optJSONArray("c") ?: continue
            out.add(cArr)
        }
        return out
    }

    fun parseVideos(raw: String): List<Video> {
        val rows = toRows(unwrap(raw))
        return rows.map { c ->
            val title = cellString(c, 2)
            val rawId = cellString(c, 11)
            val videoId = rawId.ifBlank {
                title.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
            }
            val heroRaw = cellString(c, 12).lowercase()
            Video(
                section = cellString(c, 0),
                type = cellString(c, 1).lowercase().ifBlank { "carousel" },
                title = title,
                description = cellString(c, 3),
                thumbnail = cellString(c, 4),
                videoUrl = cellString(c, 5),
                genre = cellString(c, 6),
                language = cellString(c, 7),
                country = cellString(c, 8),
                year = cellString(c, 9),
                rating = cellString(c, 10),
                videoId = videoId,
                hero = heroRaw == "true" || heroRaw == "1" || heroRaw == "yes"
            )
        }.filter { it.section.isNotBlank() || it.title.isNotBlank() }
    }

    fun parseCollections(raw: String): List<Collection> {
        val rows = toRows(unwrap(raw))
        // raw rows -> intermediate
        data class Row(
            val title: String,
            val description: String,
            val thumbnail: String,
            val season: String,
            val videoIds: List<String>,
            val collectionId: String
        )
        val parsed = rows.map { c ->
            val title = cellString(c, 0)
            val ids = cellString(c, 4)
                .split(Regex("[,\\n;|]+"))
                .map { it.trim() }
                .filter { it.isNotBlank() }
            val cid = cellString(c, 5).ifBlank {
                title.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
            }
            Row(
                title = title,
                description = cellString(c, 1),
                thumbnail = cellString(c, 2),
                season = cellString(c, 3),
                videoIds = ids,
                collectionId = cid
            )
        }.filter { it.title.isNotBlank() }

        // Group by collectionId
        val grouped = LinkedHashMap<String, Collection>()
        parsed.forEach { row ->
            val existing = grouped[row.collectionId]
            if (existing == null) {
                grouped[row.collectionId] = Collection(
                    collectionId = row.collectionId,
                    title = row.title,
                    description = row.description,
                    thumbnail = row.thumbnail,
                    seasons = listOf(Season(row.season, row.videoIds))
                )
            } else {
                val newThumb = if (existing.thumbnail.isBlank() && row.thumbnail.isNotBlank()) row.thumbnail else existing.thumbnail
                val newDesc = if (existing.description.isBlank() && row.description.isNotBlank()) row.description else existing.description
                grouped[row.collectionId] = existing.copy(
                    thumbnail = newThumb,
                    description = newDesc,
                    seasons = existing.seasons + Season(row.season, row.videoIds)
                )
            }
        }
        return grouped.values.toList()
    }

    fun parseNews(raw: String): List<NewsItem> {
        val rows = toRows(unwrap(raw))
        return rows.mapIndexed { i, c ->
            NewsItem(
                id = i,
                title = cellString(c, 0),
                description = cellString(c, 1),
                image = cellString(c, 2)
            )
        }.filter { it.title.isNotBlank() }
    }
}
