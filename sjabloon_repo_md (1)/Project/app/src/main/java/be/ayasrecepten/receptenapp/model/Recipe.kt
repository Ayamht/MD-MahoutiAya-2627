package be.ayasrecepten.receptenapp.model
// data ophalen

data class Recipe(
    val id: Int,
    val titel: String,
    val ingredienten: String,
    val stappen: String,
    val tijd: Int,
    val datumAangemaakt: String,
    val gebruikerAangemaaktId: Int,
    val gebruikerAangemaaktAlias: String,
    val afbeelding: String? = null,
    val like: Int,
    val commentaar: Int,
    val gebruikerGeliked:Boolean
)

