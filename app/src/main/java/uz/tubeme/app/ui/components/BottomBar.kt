package uz.tubeme.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uz.tubeme.app.R
import uz.tubeme.app.ui.Routes
import uz.tubeme.app.ui.theme.BorderSoft
import uz.tubeme.app.ui.theme.NeonBlue

private data class TabDef(val route: String, val icon: ImageVector, val labelRes: Int)

@Composable
fun BottomBar(navController: NavController, currentRoute: String?) {
    val tabs = listOf(
        TabDef(Routes.HOME, Icons.Outlined.Home, R.string.nav_home),
        TabDef(Routes.COLLECTIONS, Icons.Outlined.GridView, R.string.nav_collections),
        TabDef(Routes.SEARCH, Icons.Outlined.Search, R.string.nav_search),
        TabDef(Routes.FAVORITES, Icons.Outlined.Favorite, R.string.nav_favorites),
        TabDef(Routes.PROFILE, Icons.Outlined.Person, R.string.nav_profile)
    )

    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderSoft)
        )
        Row(
            Modifier
                .fillMaxWidth()
                .height(68.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val active = currentRoute == tab.route
                NavTab(
                    icon = tab.icon,
                    label = stringResource(tab.labelRes),
                    selected = active,
                    onClick = {
                        if (currentRoute != tab.route) {
                            navController.navigate(tab.route) {
                                popUpTo(Routes.HOME) { inclusive = false; saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavTab(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (selected) NeonBlue else MaterialTheme.colorScheme.onSurfaceVariant
    val barW by animateDpAsState(if (selected) 24.dp else 0.dp, tween(220), label = "barW")
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            color = tint,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
        Spacer(Modifier.height(2.dp))
        Box(
            Modifier
                .width(barW)
                .height(2.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(NeonBlue)
        )
    }
}
