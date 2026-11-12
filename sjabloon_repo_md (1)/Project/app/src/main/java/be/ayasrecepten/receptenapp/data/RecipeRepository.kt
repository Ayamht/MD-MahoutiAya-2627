package be.ayasrecepten.receptenapp.data

import be.ayasrecepten.receptenapp.network.RetrofitInstance
import com.google.gson.JsonElement

class RecipeRepository {

    suspend fun getRecipes(): JsonElement {
        return RetrofitInstance.api.getRecipes(
            "Bearer  qzpapnvmyro49p6qom72dj21q1laeye"
        )
    }
}
