package com.lerchenflo.t10elementekatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.lerchenflo.t10elementekatalog.data.AndroidSaveFileManager
import com.lerchenflo.t10elementekatalog.model.Element
import com.lerchenflo.t10elementekatalog.ui.screens.MainScreen
import com.lerchenflo.t10elementekatalog.ui.theme.T10AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var saveFileManager: AndroidSaveFileManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        saveFileManager = AndroidSaveFileManager(this)
        
        setContent {
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
                        // TODO: Navigate to element creator
                    },
                    onOpenPointCounter = {
                        // TODO: Navigate to point counter
                    }
                )
            }
        }
    }
} 