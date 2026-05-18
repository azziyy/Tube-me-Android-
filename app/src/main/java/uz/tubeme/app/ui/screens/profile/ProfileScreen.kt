package uz.tubeme.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uz.tubeme.app.R
import uz.tubeme.app.data.local.UserPrefs
import uz.tubeme.app.ui.theme.NeonGradient

@Composable
fun ProfileScreen(navController: NavController) {
    val ctx = LocalContext.current
    val prefs = remember { UserPrefs.get(ctx) }
    val scope = rememberCoroutineScope()
    val name by prefs.profileNameFlow.collectAsState(initial = "TubeMe User")
    val theme by prefs.themeFlow.collectAsState(initial = "dark")
    val language by prefs.languageFlow.collectAsState(initial = "uz")
    val quality by prefs.qualityFlow.collectAsState(initial = "auto")
    val speed by prefs.speedFlow.collectAsState(initial = "1.0")
    val autoplay by prefs.autoplayFlow.collectAsState(initial = true)
    val notif by prefs.notifFlow.collectAsState(initial = true)

    var showLangPick by remember { mutableStateOf(false) }
    var showQualityPick by remember { mutableStateOf(false) }
    var showSpeedPick by remember { mutableStateOf(false) }
    var showNameEdit by remember { mutableStateOf(false) }
    var showClearHistory by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(NeonGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        name.firstOrNull()?.uppercase() ?: "T",
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(NeonGradient)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        stringResource(R.string.plan_premium),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showNameEdit = true },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(stringResource(R.string.edit_profile))
                }
            }
        }

        // Playback group
        item { GroupTitle(stringResource(R.string.group_playback)) }
        item {
            SettingRow(
                Icons.Outlined.PlayArrow,
                stringResource(R.string.streaming_quality),
                quality.uppercase(),
                onClick = { showQualityPick = true }
            )
        }
        item {
            SettingRow(
                Icons.Outlined.Speed,
                stringResource(R.string.playback_speed),
                "${speed}x",
                onClick = { showSpeedPick = true }
            )
        }
        item {
            SettingSwitch(
                Icons.Outlined.PlayArrow,
                stringResource(R.string.autoplay),
                stringResource(R.string.autoplay_sub),
                autoplay
            ) { scope.launch { prefs.setAutoplay(it) } }
        }

        // App group
        item { GroupTitle(stringResource(R.string.group_app)) }
        item {
            SettingSwitch(
                Icons.Outlined.Brush,
                stringResource(R.string.dark_mode),
                stringResource(R.string.dark_mode_sub),
                theme != "light"
            ) { dark -> scope.launch { prefs.setTheme(if (dark) "dark" else "light") } }
        }
        item {
            SettingSwitch(
                Icons.Outlined.Notifications,
                stringResource(R.string.notifications_setting),
                stringResource(R.string.notifications_setting_sub),
                notif
            ) { scope.launch { prefs.setNotifications(it) } }
        }
        item {
            SettingRow(
                Icons.Outlined.Language,
                stringResource(R.string.language),
                when (language) { "uz" -> "O'zbek"; "ru" -> "Русский"; else -> "English" },
                onClick = { showLangPick = true }
            )
        }

        // Storage group
        item { GroupTitle(stringResource(R.string.group_storage)) }
        item {
            SettingRow(
                Icons.Outlined.History,
                stringResource(R.string.clear_history),
                stringResource(R.string.clear_history_sub),
                onClick = { showClearHistory = true }
            )
        }
        item {
            SettingRow(
                Icons.Outlined.DeleteSweep,
                stringResource(R.string.clear_cache),
                stringResource(R.string.clear_cache_sub),
                onClick = {
                    scope.launch {
                        prefs.clearProgress()
                    }
                }
            )
        }

        // About
        item { GroupTitle(stringResource(R.string.group_about)) }
        item {
            SettingRow(
                Icons.AutoMirrored.Outlined.OpenInNew,
                stringResource(R.string.about_app),
                stringResource(R.string.version),
                onClick = {}
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.footer_made),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    // Dialogs ----------------------------------------------------------------

    if (showLangPick) PickDialog(
        title = stringResource(R.string.language),
        options = listOf("uz" to "O'zbek", "ru" to "Русский", "en" to "English"),
        selected = language,
        onDismiss = { showLangPick = false },
        onPick = {
            scope.launch {
                prefs.setLanguage(it)
                showLangPick = false
                // recreate to apply locale
                (ctx as? android.app.Activity)?.recreate()
            }
        }
    )
    if (showQualityPick) PickDialog(
        title = stringResource(R.string.streaming_quality),
        options = listOf("auto" to "Auto", "1080" to "1080p", "720" to "720p", "480" to "480p"),
        selected = quality,
        onDismiss = { showQualityPick = false },
        onPick = { scope.launch { prefs.setQuality(it); showQualityPick = false } }
    )
    if (showSpeedPick) PickDialog(
        title = stringResource(R.string.playback_speed),
        options = listOf("0.5" to "0.5x", "1.0" to "1.0x", "1.25" to "1.25x", "1.5" to "1.5x", "2.0" to "2.0x"),
        selected = speed,
        onDismiss = { showSpeedPick = false },
        onPick = { scope.launch { prefs.setSpeed(it); showSpeedPick = false } }
    )
    if (showNameEdit) NameEditDialog(
        initial = name,
        onDismiss = { showNameEdit = false },
        onSave = { scope.launch { prefs.setProfileName(it); showNameEdit = false } }
    )
    if (showClearHistory) AlertDialog(
        onDismissRequest = { showClearHistory = false },
        title = { Text(stringResource(R.string.clear_history_confirm)) },
        confirmButton = {
            TextButton(onClick = {
                scope.launch { prefs.clearHistory(); showClearHistory = false }
            }) { Text(stringResource(R.string.clear_yes)) }
        },
        dismissButton = {
            TextButton(onClick = { showClearHistory = false }) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun GroupTitle(text: String) {
    Text(
        text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, value: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheck: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheck)
    }
}

@Composable
private fun PickDialog(
    title: String,
    options: List<Pair<String, String>>,
    selected: String,
    onDismiss: () -> Unit,
    onPick: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { (key, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onPick(key) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = key == selected, onClick = { onPick(key) })
                        Spacer(Modifier.width(6.dp))
                        Text(label)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) } }
    )
}

@Composable
private fun NameEditDialog(initial: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_profile)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.display_name)) },
                singleLine = true
            )
        },
        confirmButton = { TextButton(onClick = { onSave(text.ifBlank { "TubeMe User" }) }) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
