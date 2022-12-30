package tech.tyman.composablefiles.ui.components.files

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import tech.tyman.composablefiles.data.component.DirectoryEntry
import tech.tyman.composablefiles.utils.iconInfo

@Composable
fun FileIconComponent(file: DirectoryEntry) {
    // Create image loader that supports images, svgs, gifs, and videos
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(ImageDecoderDecoder.Factory())
            add(SvgDecoder.Factory())
            add(GifDecoder.Factory())
            add(VideoFrameDecoder.Factory())
        }
        .build()

    // Load image and video files with coil
    val mimeType = file.mimeType ?: ""
    if (mimeType.startsWith("image/") || mimeType.startsWith("video/")) {
        AsyncImage(
            model = file.path,
            contentDescription = file.name,
            modifier = Modifier
                .size(size = 40.dp)
                .clip(shape = RoundedCornerShape(8.dp)),
            imageLoader = imageLoader
        )

    } else {
        val (icon, description) = file.iconInfo
        if (file.isDirectory)
        {
            val files =file.fileSystemEntry.listFiles()?.size
            Box {
                Icon(
                    icon,
                    contentDescription = description,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                )
                Text(
                    files.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp))
            }
        }else{
            Icon(
                icon,
                contentDescription = description,
                modifier = Modifier
                    .size(60.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
            )
        }

    }

    Spacer(modifier = Modifier.padding(all = 8.dp))
}