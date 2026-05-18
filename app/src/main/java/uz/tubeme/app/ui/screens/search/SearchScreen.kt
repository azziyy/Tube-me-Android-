package uz.tubeme.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uz.tubeme.app.R
import uz.tubeme.app.data.model.Video
import uz.tubeme.app.data.repository.TubeMeRepository
import uz.tubeme.app.ui.Routes
import uz.tubeme.app.ui.components.EmptyState
import uz.tubeme.app.ui.components.VideoCardHorizontal

@Composable
fun SearchScreen(navController: NavController) {
    val videos by TubeMeRepository.videos.collectAsState()
    var query by remember { mutableStateOf("") }

    val results = remember(query, videos) {
        if (query.length < 2) emptyList()
        else videos.filter { v -> v.matches(query) }.take(80)
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        Text(
            stringResource(R.string.search_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Outlined.Close, null)
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        when {
            query.length < 2 -> {
                // Trending suggestions = first videos as chips
                Text(
                    stringResource(R.string.trending_searches),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                val suggestions = remember(videos) {
                    videos.map { it.genre }.filter { it.isNotBlank() }.distinct().take(8)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                ) {
                    suggestions.forEach { tag ->
                        AssistChip(
                            onClick = { query = tag },
                            label = { Text(tag) }
                        )
                    }
                }
            }
            results.isEmpty() -> {
                EmptyState(
                    stringResource(R.string.no_results),
                    stringResource(R.string.no_results_sub),
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Text(
                    stringResource(R.string.result_count).replace("{n}", results.size.toString()),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(results) { v ->
                        VideoCardHorizontal(v) { navController.navigate(Routes.video(v.videoId)) }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

private fun Video.matches(q: String): Boolean {
    val s = q.trim().lowercase()
    return title.lowercase().contains(s) ||
            genre.lowercase().contains(s) ||
            description.lowercase().contains(s) ||
            section.lowercase().contains(s) ||
            country.lowercase().contains(s) ||
            language.lowercase().contains(s)
}


