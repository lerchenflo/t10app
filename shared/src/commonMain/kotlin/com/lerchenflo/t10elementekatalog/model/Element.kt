package com.lerchenflo.t10elementekatalog.model

data class Element(
    val name: String,
    val description: String,
    val difficulty: Int,
    val elementType: Int,
    val category: Int,
    val points: Float
) {
    fun getDifficultyName(): String = Constants.difficultyNames[difficulty]
    fun getElementTypeName(): String = Constants.elementTypeNames[elementType]
    fun getCategoryName(): String = Constants.elementCategoryNames[category]
} 