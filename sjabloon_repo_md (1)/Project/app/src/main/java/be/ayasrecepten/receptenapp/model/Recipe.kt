package be.ayasrecepten.receptenapp.model

data class Recipe(
    val id: Int,
    val titel: String,
    val ingredienten: String,
    val stappen: String,
    val tijd: Int
)