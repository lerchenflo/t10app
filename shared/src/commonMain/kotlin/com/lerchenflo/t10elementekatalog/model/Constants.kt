package com.lerchenflo.t10elementekatalog.model

object Constants {
    const val DIFFICULTY_EASY = 0
    const val DIFFICULTY_MEDIUM = 1
    const val DIFFICULTY_HARD = 2

    const val ELEMENT_TYPE_JUMP = 0
    const val ELEMENT_TYPE_TURN = 1
    const val ELEMENT_TYPE_BALANCE = 2
    const val ELEMENT_TYPE_STRENGTH = 3
    const val ELEMENT_TYPE_FLEXIBILITY = 4

    const val ELEMENT_CATEGORY_A = 0
    const val ELEMENT_CATEGORY_B = 1
    const val ELEMENT_CATEGORY_C = 2
    const val ELEMENT_CATEGORY_D = 3

    val difficultyNames = listOf("Leicht", "Mittel", "Schwer")
    val elementTypeNames = listOf("Sprung", "Drehung", "Balance", "Kraft", "Beweglichkeit")
    val elementCategoryNames = listOf("A", "B", "C", "D")

    const val FILE_EXTENSION = ".t10"
    const val SHARED_PREFS_NAME = "T10_PREFS"
    const val LAST_OPENED_FILE = "LAST_OPENED_FILE"
} 