package uz.tubeme.app.ui.screens.video

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uz.tubeme.app.R
import uz.tubeme.app.data.local.UserPrefs
import uz.tubeme.app.data.repository.TubeMeRepository
import uz.tubeme.app.player.SmartPlayer
import uz.tubeme.app.ui.Routes
import uz.tubeme.app.ui.components.EmptyState
import uz.tubeme.app.ui.components.SectionHeader
import uz.tubeme.app.ui.components.VideoCardCarousel

@UnstableApi
@Composable
fun VideoScreen(navController: NavController, videoId: String) {
    val ctx = LocalContext.current
    val prefs = remember { UserPrefs.get(ctx) }
    val scope = rememberCoroutineScope()

    val videos by TubeMeRepository.videos.collectAsState()
    val favSet by prefs.favoritesFlow.collectAsState(initial = emptySet())
    val savedSet by prefs.savedFlow.collectAsState(initial = emptySet())

    val video = remember(videos, videoId) { videos.firstOrNull { it.videoId == videoId } }

    LaunchedEffect(videoId) {
        if (video != null) prefs.pushHistory(videoId)
    }

    if (video == null) {
        Column(Modifier.fillMaxSize().padding(top = 60.dp)) {
            EmptyState(stringResource(R.string.video_not_found), "")
        }
        return
    }

    val isFav = videoId in favSet
    val isSaved = videoId in savedSet
    val related = remember(video, videos) { TubeMeRepository.moreLikeThis(video) }

    LazyColumn(Modifier.fillMaxSize()) {
        // Player + back button
        item {
            Box(Modifier.fillMaxWidth().background(Color.Black)) {
                SmartPlayer(
                    url = video.videoUrl,
                    onProgress = { current, duration ->
                        scope.launch { prefs.setProgress(videoId, current, duration) }
                    }
                )
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = Color.White)
                }
            }
        }

        // Title block
        item {
            Column(Modifier.padding(16.dp)) {
                Text(
                    video.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    listOfNotNull(
                        video.genre.takeIf { it.isNotBlank() },
                        video.year.takeIf { it.isNotBlank() },
                        video.country.takeIf { it.isNotBlank() },
                        video.rating.takeIf { it.isNotBlank() }?.let { "★ $it" }
                    ).joinToString(" • "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                if (video.description.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        video.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Actions
        item {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionBtn(
                    icon = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    label = stringResource(R.string.action_like),
                    tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                ) {
                    scope.launch { prefs.toggleFavorite(videoId) }
                }
                ActionBtn(
                    icon = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    label = stringResource(R.string.action_save),
                    tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                ) {
                    scope.launch { prefs.toggleSaved(videoId) }
                }
                ActionBtn(
                    icon = Icons.Outlined.Share,
                    label = stringResource(R.string.action_share),
                    tint = MaterialTheme.colorScheme.onSurface
                ) {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, video.title)
                        putExtra(Intent.EXTRA_TEXT, "${video.title}\n${video.videoUrl}")
                    }
                    ctx.startActivity(Intent.createChooser(intent, null))
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (related.isNotEmpty()) {
            item {
                SectionHeader(stringResource(R.string.more_like_this))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(related, key = { it.videoId }) { rv ->
                        VideoCardCarousel(
                            video = rv,
                            onClick = {
                                navController.navigate(Routes.video(rv.videoId)) {
                                    popUpTo(Routes.video(videoId)) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
        item { Spacer(Modifier.height(28.dp)) }
    }
}

@Composable
private fun ActionBtn(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(26.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = tint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}
