package tech.tyman.composablefiles.ui.screens

import androidx.compose.runtime.Composable
import tech.tyman.composablefiles.data.FileSystem
import tech.tyman.composablefiles.ui.components.DirectoryComponent

@Composable
fun DirectoryScreen(path: String, fileSystem: FileSystem) {
    DirectoryComponent(path = path, fileSystem = fileSystem)
}
