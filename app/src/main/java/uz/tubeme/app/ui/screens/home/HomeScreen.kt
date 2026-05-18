package uz.tubeme.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.tubeme.app.R
import uz.tubeme.app.data.local.UserPrefs
import uz.tubeme.app.data.model.Video
import uz.tubeme.app.data.repository.TubeMeRepository
import uz.tubeme.app.ui.Routes
import uz.tubeme.app.ui.components.*
import uz.tubeme.app.ui.theme.NeonGradient
import uz.tubeme.app.util.effectiveThumbnail

@Composable
fun HomeScreen(navController: NavController) {
    val videos by TubeMeRepository.videos.collectAsState()
    val isLoading by TubeMeRepository.isLoading.collectAsState()
    val ctx = LocalContext.current
    val prefs = remember { UserPrefs.get(ctx) }
    val history by prefs.historyFlow.collectAsState(initial = emptyList())
    val progress by prefs.progressFlow.collectAsState(initial = emptyMap())
    val scope = rememberCoroutineScope()

    val sections = remember(videos) { TubeMeRepository.sections() }
    val heroes = remember(videos) { TubeMeRepository.heroes() }
    val continueWatching = remember(history, progress, videos) {
        history.mapNotNull { id ->
            val v = videos.firstOrNull { it.videoId == id } ?: return@mapNotNull null
            val p = progress[id] ?: return@mapNotNull null
            val ratio = if (p.second > 0) p.first.toFloat() / p.second.toFloat() else 0f
            if (ratio in 0.02f..0.92f) v to ratio else null
        }.take(15)
    }

    if (videos.isEmpty() && isLoading) {
        HomeSkeleton()
        return
    }
    if (videos.isEmpty()) {
        EmptyState(
            stringResource(R.string.empty_no_content),
            stringResource(R.string.empty_no_content_sub),
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (heroes.isNotEmpty()) {
            item { HeroCarousel(heroes) { navController.navigate(Routes.video(it.videoId)) } }
        }

        if (continueWatching.isNotEmpty()) {
            item {
                SectionHeader(stringResource(R.string.home_continue))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(continueWatching) { (v, ratio) ->
                        VideoCardCarousel(
                            video = v,
                            progress = ratio,
                            onClick = { navController.navigate(Routes.video(it.videoId)) }
                        )
                    }
                }
            }
        }

        items(sections) { section ->
            SectionHeader(
                title = section.name,
                onSeeAll = { navController.navigate(Routes.section(section.name)) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(section.items) { v ->
                    if (section.type == "grid" || section.type == "portrait") {
                        VideoCardPortrait(v) { navController.navigate(Routes.video(it.videoId)) }
                    } else {
                        VideoCardCarousel(v) { navController.navigate(Routes.video(it.videoId)) }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun HeroCarousel(heroes: List<Video>, onPlay: (Video) -> Unit) {
    var index by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(heroes) {
        if (heroes.size <= 1) return@LaunchedEffect
        while (true) {
            delay(5500)
            index = (index + 1) % heroes.size
        }
    }
    val v = heroes.getOrNull(index) ?: return
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AsyncImage(
            model = v.effectiveThumbnail(),
            contentDescription = v.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.55f to Color.Black.copy(alpha = 0.5f),
                        1f to Color.Black.copy(alpha = 0.92f)
                    )
                )
        )
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp)
        ) {
            GradientChip(stringResource(R.string.hero_featured))
            Spacer(Modifier.height(8.dp))
            Text(
                v.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2
            )
            if (v.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    v.description,
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onPlay(v) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Default.PlayArrow, null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.hero_play), fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onPlay(v) },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Default.Info, null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.hero_info))
                }
            }
        }
        // dots
        if (heroes.size > 1) {
            Row(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                heroes.forEachIndexed { i, _ ->
                    Box(
                        Modifier
                            .size(if (i == index) 18.dp else 6.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (i == index) Color.White else Color.White.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSkeleton() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Shimmer(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f),
            cornerRadius = 18.dp
        )
        repeat(2) {
            Shimmer(Modifier.width(160.dp).height(20.dp), cornerRadius = 6.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(3) {
                    Shimmer(
                        Modifier
                            .width(160.dp)
                            .aspectRatio(16f / 9f),
                        cornerRadius = 12.dp
                    )
                }
            }
        }
    }
}
