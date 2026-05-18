package uz.tubeme.app.player

import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import uz.tubeme.app.data.model.MediaKind
import uz.tubeme.app.data.model.detectMediaKind
import uz.tubeme.app.data.model.extractYouTubeId

/**
 * SmartPlayer: pick a backend based on the URL.
 *   - YouTube  → official YouTubePlayerView (IFrame wrapper)
 *   - HLS      → ExoPlayer + HlsMediaSource (.m3u8)
 *   - MP4/etc. → ExoPlayer + ProgressiveMediaSource (.mp4 / .mkv / .webm / generic CDN)
 *
 * onProgress(currentMs, durationMs) is fired ~once per second while playing.
 */
@UnstableApi
@Composable
fun SmartPlayer(
    url: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    onProgress: (Long, Long) -> Unit = { _, _ -> }
) {
    val kind = remember(url) { detectMediaKind(url) }
    Box(modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
        when (kind) {
            MediaKind.YOUTUBE -> YouTubePart(url, autoPlay, onProgress)
            else -> ExoPart(url, kind, autoPlay, onProgress)
        }
    }
}

@Composable
private fun YouTubePart(url: String, autoPlay: Boolean, onProgress: (Long, Long) -> Unit) {
    val ytId = remember(url) { extractYouTubeId(url) ?: "" }
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { ctx ->
            val view = YouTubePlayerView(ctx)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            view.enableAutomaticInitialization = true
            lifecycleOwner.lifecycle.addObserver(view)

            view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                private var duration = 0L
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    if (ytId.isNotBlank()) {
                        if (autoPlay) youTubePlayer.loadVideo(ytId, 0f)
                        else youTubePlayer.cueVideo(ytId, 0f)
                    }
                }
                override fun onVideoDuration(youTubePlayer: YouTubePlayer, d: Float) {
                    duration = (d * 1000).toLong()
                }
                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    onProgress((second * 1000).toLong(), duration)
                }
            })
            view
        }
    )
}

@UnstableApi
@Composable
private fun ExoPart(url: String, kind: MediaKind, autoPlay: Boolean, onProgress: (Long, Long) -> Unit) {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val exo = remember(url) {
        ExoPlayer.Builder(ctx).build().apply {
            val item = MediaItem.fromUri(url)
            val source = if (kind == MediaKind.HLS) {
                HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(item)
            } else {
                DefaultMediaSourceFactory(ctx).createMediaSource(item)
            }
            setMediaSource(source)
            prepare()
            playWhenReady = autoPlay
        }
    }

    LaunchedEffect(exo) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            try {
                if (exo.duration > 0 && exo.playbackState == Player.STATE_READY) {
                    onProgress(exo.currentPosition, exo.duration)
                }
            } catch (_: Exception) {}
        }
    }

    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exo.playWhenReady = false
                Lifecycle.Event.ON_RESUME -> if (autoPlay) exo.playWhenReady = true
                Lifecycle.Event.ON_DESTROY -> exo.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(obs)
            exo.release()
        }
    }

    AndroidView(
        factory = { c ->
            PlayerView(c).apply {
                useController = true
                player = exo
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
