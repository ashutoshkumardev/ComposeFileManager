package tech.tyman.composablefiles

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import tech.tyman.composablefiles.data.FileSystemType
import tech.tyman.composablefiles.data.filesystems.LocalFileSystem
import tech.tyman.composablefiles.data.navigation.Location
import tech.tyman.composablefiles.data.navigation.LocationType
import tech.tyman.composablefiles.ui.screens.DirectoryScreen
import tech.tyman.composablefiles.ui.theme.ComposableFilesTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                onActivityResult.launch(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:${BuildConfig.APPLICATION_ID}")))
            } else {
                onPermissionsGranted()
            }
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        } else {
            this.onPermissionsGranted()
        }
    }

    private val onActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                this.onPermissionsGranted()
            } else {
                this.onPermissionsRejected()
            }
        }


    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { actionMap ->
                when (actionMap.key) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        if (actionMap.value) {
                            this.onPermissionsGranted()
                        } else {
                            this.onPermissionsRejected()
                        }
                    }

                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        if (actionMap.value) {
                            this.onPermissionsGranted()
                        } else {
                            this.onPermissionsRejected()
                        }
                    }
                }
            }
        }

    /**
     * The ending callback for once permissions are successfully granted, this renders the compose app.
     */
    private fun onPermissionsGranted() {
        setContent {
            ComposableFilesTheme {
                val navController = rememberNavController()
                val defaultLocation = Location(
                    fileSystem = FileSystemType.LOCAL,
                    path = Environment.getExternalStorageDirectory().absolutePath
                )

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    NavHost(
                        navController = navController,
                        startDestination = "directory/{location}") {
                        composable(route = "directory/{location}",
                            arguments = listOf(
                                // A key that is parsed into an object (i.e. "local" -> LocalFileSystem)
                                navArgument("location") {
                                    type = LocationType()
                                    defaultValue = defaultLocation
                                }
                            )
                        ) { entry ->
                            val location = entry.arguments!!.getParcelable<Location>("location")!!
                            DirectoryScreen(
                                path = location.path,
                                // New file systems should add an entry here for navigation
                                fileSystem = when (location.fileSystem) {
                                    FileSystemType.LOCAL -> LocalFileSystem()
                                }
                            )
                        }
                    }

                }
            }
        }
    }

    /**
     * The ending callback for if permissions are rejected, this sends a toast error and then closes the app.
     */
    private fun onPermissionsRejected() {
        Toast.makeText(
            this,
            "This app requires external storage permissions to function.",
            Toast.LENGTH_LONG
        ).show()
        this.finishAndRemoveTask()
    }

}