package uz.tubeme.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uz.tubeme.app.R
import uz.tubeme.app.data.local.UserPrefs
import uz.tubeme.app.data.repository.TubeMeRepository
import uz.tubeme.app.ui.Routes
import uz.tubeme.app.ui.theme.NeonGradient
import uz.tubeme.app.ui.theme.NeonPink

@Composable
fun AppTopBar(navController: NavController) {
    val ctx = LocalContext.current
    val prefs = remember { UserPrefs.get(ctx) }
    val profileName by prefs.profileNameFlow.collectAsState(initial = "T")
    val readSet by prefs.readNewsFlow.collectAsState(initial = emptySet())
    val news by TubeMeRepository.news.collectAsState()
    val hasUnread = news.any { it.id.toString() !in readSet }

    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(NeonGradient),
                contentAlignment = Alignment.Center
            ) {
                Text("T", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.weight(1f))

        IconButton(onClick = { navController.navigate(Routes.SEARCH) }) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = stringResource(R.string.search),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Box {
            IconButton(onClick = { NotificationsDialogState.show(news) }) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.notifications),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            if (hasUnread) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp, top = 10.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonPink)
                )
            }
        }
        Box(
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(NeonGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                (profileName.firstOrNull()?.uppercase() ?: "T"),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    NotificationsDialogHost()
}
