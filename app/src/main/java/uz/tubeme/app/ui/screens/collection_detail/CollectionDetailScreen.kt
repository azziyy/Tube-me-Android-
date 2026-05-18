package uz.tubeme.app.ui.screens.collection_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import uz.tubeme.app.R
import uz.tubeme.app.data.repository.TubeMeRepository
import uz.tubeme.app.ui.Routes
import uz.tubeme.app.ui.components.EmptyState
import uz.tubeme.app.ui.components.VideoCardHorizontal
import uz.tubeme.app.ui.theme.NeonGradient
import uz.tubeme.app.util.effectiveThumbnail

@Composable
fun CollectionDetailScreen(navController: NavController, collectionId: String) {
    val list by TubeMeRepository.collections.collectAsState()
    val videos by TubeMeRepository.videos.collectAsState()
    val collection = list.firstOrNull { it.collectionId == collectionId }
    var selectedSeason by remember { mutableStateOf(0) }

    if (collection == null) {
        EmptyState(stringResource(R.string.collection_not_found), "", Modifier.fillMaxSize())
        return
    }
    val season = collection.seasons.getOrNull(selectedSeason) ?: collection.seasons.firstOrNull()
    val episodes = season?.videoIds?.mapNotNull { id -> videos.firstOrNull { it.videoId == id } } ?: emptyList()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = collection.thumbnail,
                    contentDescription = collection.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.95f)
                            )
                        )
                )
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.45f))
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = Color.White)
                }
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        collection.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (collection.description.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            collection.description,
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3
                        )
                    }
                }
            }
        }

        if (collection.seasons.size > 1) {
            item {
                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    collection.seasons.forEachIndexed { i, s ->
                        val active = i == selectedSeason
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (active) NeonGradient
                                    else androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.surface)
                                )
                                .clickable { selectedSeason = i }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                s.name.ifBlank { stringResource(R.string.season) + " ${i + 1}" },
                                color = if (active) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        if (episodes.isEmpty()) {
            item {
                EmptyState(
                    stringResource(R.string.no_episodes),
                    stringResource(R.string.no_episodes_sub),
                    modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
                )
            }
        } else {
            items(episodes) { ep ->
                Box(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                    VideoCardHorizontal(ep) { navController.navigate(Routes.video(ep.videoId)) }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}


