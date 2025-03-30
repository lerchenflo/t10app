package com.lerchenflo.t10elementekatalog

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lerchenflo.t10elementekatalog.data.DesktopSaveFileManager
import com.lerchenflo.t10elementekatalog.model.Element
import com.lerchenflo.t10elementekatalog.ui.screens.MainScreen
import com.lerchenflo.t10elementekatalog.ui.theme.T10AppTheme
import java.io.File
import kotlinx.coroutines.runBlocking

fun main() = application {
    val saveFileManager = DesktopSaveFileManager()
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "T10 Elementekatalog"
    ) {
        T10AppTheme {
            var elements by remember { mutableStateOf<List<Element>>(emptyList()) }
            
            LaunchedEffect(Unit) {
                val lastOpenedFile = saveFileManager.getLastOpenedFile()
                if (lastOpenedFile != null) {
                    elements = saveFileManager.loadElements(lastOpenedFile)
                }
            }
            
            MainScreen(
                elements = elements,
                onCreateElement = {
                    // TODO: Show element creator dialog
                },
                onOpenPointCounter = {
                    // TODO: Show point counter dialog
                }
            )
        }
    }
} 