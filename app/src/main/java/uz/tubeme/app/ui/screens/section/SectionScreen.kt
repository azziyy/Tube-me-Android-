package uz.tubeme.app.ui.screens.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uz.tubeme.app.data.repository.TubeMeRepository
import uz.tubeme.app.ui.Routes
import uz.tubeme.app.ui.components.EmptyState
import uz.tubeme.app.ui.components.VideoCardPortrait

@Composable
fun SectionScreen(navController: NavController, sectionName: String) {
    val videos by TubeMeRepository.videos.collectAsState()
    val items = remember(videos, sectionName) {
        videos.filter { it.section.equals(sectionName, ignoreCase = true) }
    }
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                sectionName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (items.isEmpty()) {
            EmptyState("Bo‘sh", "Bu bo‘limda hozircha videolar yo‘q.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.videoId }) { v ->
                    VideoCardPortrait(
                        video = v,
                        onClick = { navController.navigate(Routes.video(v.videoId)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
