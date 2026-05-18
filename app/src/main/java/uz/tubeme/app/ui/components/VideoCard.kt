package uz.tubeme.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uz.tubeme.app.data.model.Video
import uz.tubeme.app.ui.theme.NeonGradient
import uz.tubeme.app.util.effectiveThumbnail

/**
 * Three variants:
 *  - VideoCardCarousel (wide poster 160x90 style for "carousel" sections)
 *  - VideoCardPortrait (160x240 poster for "grid" / portrait)
 *  - VideoCardHorizontal (full-width row, like history / continue)
 */

@Composable
fun VideoCardCarousel(
    video: Video,
    onClick: (Video) -> Unit,
    progress: Float? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(video) }
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = video.effectiveThumbnail(),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // gradient overlay
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))
                        )
                    )
            )
            // play icon
            Box(
                Modifier
                    .align(Alignment.Center)
                    .size(36.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White)
            }
            // rating
            if (video.rating.isNotBlank()) {
                Row(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFD53A), modifier = Modifier.size(11.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(video.rating, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            if (progress != null && progress > 0f) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(3.dp)
                        .background(NeonGradient)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            video.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            fontWeight = FontWeight.SemiBold
        )
        if (video.genre.isNotBlank() || video.year.isNotBlank()) {
            Text(
                buildString {
                    if (video.genre.isNotBlank()) append(video.genre)
                    if (video.genre.isNotBlank() && video.year.isNotBlank()) append(" • ")
                    if (video.year.isNotBlank()) append(video.year)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun VideoCardPortrait(
    video: Video,
    onClick: (Video) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(140.dp)
            .clickable { onClick(video) }
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = video.effectiveThumbnail(),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)))
                    )
            )
            if (video.rating.isNotBlank()) {
                Row(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFD53A), modifier = Modifier.size(10.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(video.rating, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            video.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun VideoCardHorizontal(
    video: Video,
    onClick: (Video) -> Unit,
    progress: Float? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick(video) }
            .padding(8.dp)
    ) {
        Box(
            Modifier
                .width(140.dp)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = video.effectiveThumbnail(),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (progress != null && progress > 0f) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(3.dp)
                        .background(NeonGradient)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Text(video.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(
                listOfNotNull(
                    video.genre.takeIf { it.isNotBlank() },
                    video.year.takeIf { it.isNotBlank() },
                    video.country.takeIf { it.isNotBlank() }
                ).joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
