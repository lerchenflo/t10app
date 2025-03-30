package com.lerchenflo.t10elementekatalog.data

import com.lerchenflo.t10elementekatalog.model.Element
import java.io.File
import java.util.prefs.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DesktopSaveFileManager : SaveFileManager {
    private val preferences = Preferences.userRoot().node("com.lerchenflo.t10elementekatalog")
    
    override suspend fun saveElements(elements: List<Element>, filePath: String) = withContext(Dispatchers.IO) {
        val file = File(filePath)
        file.bufferedWriter().use { writer ->
            elements.forEach { element ->
                writer.write("${element.name}${SaveFileManager.DELIMITER}" +
                    "${element.description}${SaveFileManager.DELIMITER}" +
                    "${element.difficulty}${SaveFileManager.DELIMITER}" +
                    "${element.elementType}${SaveFileManager.DELIMITER}" +
                    "${element.category}${SaveFileManager.DELIMITER}" +
                    "${element.points}\n")
            }
        }
    }

    override suspend fun loadElements(filePath: String): List<Element> = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) return@withContext emptyList()

        file.bufferedReader().useLines { lines ->
            lines.map { line ->
                val parts = line.split(SaveFileManager.DELIMITER)
                Element(
                    name = parts[0],
                    description = parts[1],
                    difficulty = parts[2].toInt(),
                    elementType = parts[3].toInt(),
                    category = parts[4].toInt(),
                    points = parts[5].toFloat()
                )
            }.toList()
        }
    }

    override suspend fun getLastOpenedFile(): String? = withContext(Dispatchers.IO) {
        preferences.get("LAST_OPENED_FILE", null)
    }

    override suspend fun setLastOpenedFile(filePath: String) = withContext(Dispatchers.IO) {
        preferences.put("LAST_OPENED_FILE", filePath)
        preferences.flush()
    }
} 