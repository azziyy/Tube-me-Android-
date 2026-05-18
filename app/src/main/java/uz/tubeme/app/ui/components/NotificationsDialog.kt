package uz.tubeme.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.tubeme.app.R
import uz.tubeme.app.data.local.UserPrefs
import uz.tubeme.app.data.model.NewsItem

/** Lightweight global state for the notifications dialog */
object NotificationsDialogState {
    val isOpen = MutableStateFlow(false)
    val items = MutableStateFlow<List<NewsItem>>(emptyList())
    fun show(news: List<NewsItem>) {
        items.value = news
        isOpen.value = true
    }
    fun close() { isOpen.value = false }
}

@Composable
fun NotificationsDialogHost() {
    val opened by NotificationsDialogState.isOpen.collectAsState()
    val data by NotificationsDialogState.items.collectAsState()
    if (!opened) return
    val ctx = LocalContext.current
    val prefs = remember { UserPrefs.get(ctx) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(data) {
        // mark all as read on open
        prefs.markNewsRead(data.map { it.id.toString() }.toSet())
    }

    Dialog(
        onDismissRequest = { NotificationsDialogState.close() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.notifications_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { NotificationsDialogState.close() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null)
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (data.isEmpty()) {
                    Text(
                        stringResource(R.string.no_notifications),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.heightIn(max = 480.dp)
                    ) {
                        items(data) { item ->
                            NewsRow(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsRow(item: NewsItem) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.image.isNotBlank()) {
            AsyncImage(
                model = item.image,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black)
            )
            Spacer(Modifier.width(10.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            if (item.description.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(item.description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
            }
        }
    }
}
