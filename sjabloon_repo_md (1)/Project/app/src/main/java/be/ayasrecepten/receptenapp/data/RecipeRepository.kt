package be.ayasrecepten.receptenapp.data

import be.ayasrecepten.receptenapp.network.RetrofitInstance
import com.google.gson.JsonElement

class RecipeRepository {

    private val authorization = "Bearer qzpapnvmyro49p6qom72dj21q1laeye"

    suspend fun getRecipes(): JsonElement {
        return RetrofitInstance.api.getRecipes(authorization)
    }

    // Gegevens van de gebruiker ophalen
    suspend fun getGebruiker(): JsonElement {
        return RetrofitInstance.api.getGebruiker(authorization)
    }

    // Alias en kookervaring aanpassen
    suspend fun updateGebruiker(
        alias: String,
        ervaring: Int
    ): JsonElement {
        require(ervaring in 0..99) {
            "Ervaring moet tussen 0 en 99 liggen"
        }

        return RetrofitInstance.api.updateGebruikerForm(
            authorization,
            alias,
            ervaring
        )
    }
}