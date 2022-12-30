package tech.tyman.composablefiles.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import okio.IOException
import tech.tyman.composablefiles.data.FileSystem
import tech.tyman.composablefiles.data.component.DirectoryEntry
import tech.tyman.composablefiles.data.component.DirectoryInfo
import tech.tyman.composablefiles.ui.components.files.FileListComponent
import tech.tyman.composablefiles.utils.replaceWith
import tech.tyman.composablefiles.utils.showToast

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DirectoryComponent(path: String, fileSystem: FileSystem) {
    fileSystem.load()
    var directory by remember { mutableStateOf(
        DirectoryInfo(fileSystem.getEntry(path))
    ) }
    val selectedFiles = remember { mutableStateListOf<DirectoryEntry>() }
    // There is likely a better way to do this, but I do not know how, so I just create another mutable state
    var files by remember { mutableStateOf(directory.files) }

    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )
    val coroutineScope = rememberCoroutineScope()

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheet() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                /* DirectoryTopBarComponent(
                     title = if (selectedFiles.size > 0) selectedFiles.size.toString() else directory.name,
                     directory = directory,
                     onButton = {
                         when (it) {
                             TopBarAction.HOME -> {
                                 selectedFiles.clear()
                                 directory = directory.clone(Environment.getExternalStorageDirectory().absolutePath)
                                 files = directory.files
                             }
                             TopBarAction.RELOAD -> {
                                 selectedFiles.clear()
                                 directory = directory.clone()
                                 files = directory.files
                             }
                             TopBarAction.DELETE -> {
                                 val failed = mutableListOf<DirectoryEntry>()
                                 selectedFiles.popAll { entry ->
                                     if (!entry.delete()) failed.add(entry)
                                 }
                                 directory = directory.clone()
                                 files = directory.files
                                 if (failed.size > 0) context.showToast("Failed to delete some files: ${
                                     failed.joinToString(", ") { f -> f.name }
                                 }")
                                 else context.showToast("Successfully deleted files!")
                             }
                             TopBarAction.DONE_SELECTING -> {
                                 selectedFiles.clear()
                             }
                             TopBarAction.MENU -> {
                                 // TODO
                             }
                         }
                     },
                     buttonState = if (selectedFiles.size > 0) TopBarState.SELECTION else TopBarState.DEFAULT
                 )*/

                Text(
                    directory.name,
                    fontSize = 50.sp,
                    maxLines = 1,
                    overflow= TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(8.dp))

                FileListComponent(
                    parent = directory.getParent(),
                    files = files,
                    selectedFiles = selectedFiles,
                    onFileClick = {
                        // TODO Don't ignore clicking on files
                        if (!it.isDirectory) return@FileListComponent
                        // Clear all selected files
                        selectedFiles.clear()
                        // Set state to the new directory, catching any errors
                        try {
                            directory = DirectoryInfo(it.fileSystemEntry)
                            files = directory.files
                        } catch (e: IOException) {
                            context.showToast("Unable to read directory!")
                        }
                    },
                    onFileSelect = { selectedFiles.replaceWith(it)
                            coroutineScope.launch {
                                if (sheetState.isVisible) sheetState.hide()
                                else sheetState.show()
                            }
                    }
                )
            }
        }
    }
}