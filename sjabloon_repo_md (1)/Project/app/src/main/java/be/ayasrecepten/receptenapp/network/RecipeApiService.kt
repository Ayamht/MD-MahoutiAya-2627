package be.ayasrecepten.receptenapp.network

import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Header

interface RecipeApiService {

    @GET("recepten")
    suspend fun getRecipes(
        @Header("Authorization") authorization: String
    ): JsonElement
}
