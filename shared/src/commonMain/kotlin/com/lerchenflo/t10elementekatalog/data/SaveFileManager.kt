package com.lerchenflo.t10elementekatalog.data

import com.lerchenflo.t10elementekatalog.model.Element

interface SaveFileManager {
    suspend fun saveElements(elements: List<Element>, filePath: String)
    suspend fun loadElements(filePath: String): List<Element>
    suspend fun getLastOpenedFile(): String?
    suspend fun setLastOpenedFile(filePath: String)
    
    companion object {
        const val DELIMITER = ";"
    }
} 