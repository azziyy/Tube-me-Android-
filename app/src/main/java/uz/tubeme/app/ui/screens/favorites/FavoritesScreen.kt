package uz.tubeme.app.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uz.tubeme.app.R
import uz.tubeme.app.data.local.UserPrefs
import uz.tubeme.app.data.model.Video
import uz.tubeme.app.data.repository.TubeMeRepository
import uz.tubeme.app.ui.Routes
import uz.tubeme.app.ui.components.EmptyState
import uz.tubeme.app.ui.components.VideoCardHorizontal
import androidx.compose.ui.platform.LocalContext

@Composable
fun FavoritesScreen(navController: NavController) {
    val ctx = LocalContext.current
    val prefs = remember { UserPrefs.get(ctx) }
    val videos by TubeMeRepository.videos.collectAsState()
    val favs by prefs.favoritesFlow.collectAsState(initial = emptySet())
    val saved by prefs.savedFlow.collectAsState(initial = emptySet())
    val history by prefs.historyFlow.collectAsState(initial = emptyList())
    val progress by prefs.progressFlow.collectAsState(initial = emptyMap())

    var tab by remember { mutableStateOf(0) }
    val tabs = listOf(
        R.string.tab_liked, R.string.tab_saved, R.string.tab_continue, R.string.tab_history
    )

    val likedList = remember(favs, videos) { videos.filter { it.videoId in favs } }
    val savedList = remember(saved, videos) { videos.filter { it.videoId in saved } }
    val continueList = remember(history, progress, videos) {
        history.mapNotNull { id ->
            val v = videos.firstOrNull { it.videoId == id } ?: return@mapNotNull null
            val p = progress[id] ?: return@mapNotNull null
            val ratio = if (p.second > 0) p.first.toFloat() / p.second.toFloat() else 0f
            if (ratio in 0.02f..0.92f) v to ratio else null
        }
    }
    val historyList = remember(history, videos) {
        history.mapNotNull { id -> videos.firstOrNull { it.videoId == id } }
    }

    Column(Modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.library_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        ScrollableTabRow(selectedTabIndex = tab, edgePadding = 12.dp) {
            tabs.forEachIndexed { i, res ->
                Tab(
                    selected = tab == i,
                    onClick = { tab = i },
                    text = { Text(stringResource(res), fontWeight = FontWeight.SemiBold) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        when (tab) {
            0 -> ListOrEmpty(likedList, R.string.empty_liked, R.string.empty_liked_sub, navController)
            1 -> ListOrEmpty(savedList, R.string.empty_saved, R.string.empty_saved_sub, navController)
            2 -> ListOrEmptyWithProgress(continueList, R.string.empty_continue, R.string.empty_continue_sub, navController)
            3 -> ListOrEmpty(historyList, R.string.empty_history, R.string.empty_history_sub, navController)
        }
    }
}

@Composable
private fun ListOrEmpty(
    list: List<Video>,
    emptyT: Int,
    emptyS: Int,
    nav: NavController
) {
    if (list.isEmpty()) {
        EmptyState(stringResource(emptyT), stringResource(emptyS), Modifier.fillMaxSize())
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(list) { v -> VideoCardHorizontal(v) { nav.navigate(Routes.video(v.videoId)) } }
    }
}

@Composable
private fun ListOrEmptyWithProgress(
    list: List<Pair<Video, Float>>,
    emptyT: Int,
    emptyS: Int,
    nav: NavController
) {
    if (list.isEmpty()) {
        EmptyState(stringResource(emptyT), stringResource(emptyS), Modifier.fillMaxSize())
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(list) { (v, p) ->
            VideoCardHorizontal(v, progress = p) { nav.navigate(Routes.video(v.videoId)) }
        }
    }
}
