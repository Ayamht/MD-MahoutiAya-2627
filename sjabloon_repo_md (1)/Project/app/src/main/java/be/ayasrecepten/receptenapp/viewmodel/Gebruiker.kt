package be.ayasrecepten.receptenapp.model

data class Gebruiker(
    val id: Int,
    val naam: String,
    val voornaam: String,
    val nummer: String,
    val laatstActief: String,
    val alias: String,
    val requestCount: Int,
    val ervaring: Int
)