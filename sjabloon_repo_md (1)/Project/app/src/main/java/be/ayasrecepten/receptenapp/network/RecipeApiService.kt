package be.ayasrecepten.receptenapp.network

import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Body

data class GebruikerUpdateRequest(
    val alias: String,
    val ervaring: Int
)

interface RecipeApiService {

    @GET("recepten")
    suspend fun getRecipes(
        @Header("Authorization") authorization: String
    ): JsonElement

    @GET("gebruiker")
    suspend fun getGebruiker(
        @Header("Authorization") authorization: String
    ): JsonElement

    @FormUrlEncoded
    @POST("gebruiker")
    suspend fun updateGebruikerForm(
        @Header("Authorization") authorization: String,
        @Field("alias") alias: String,
        @Field("ervaring") ervaring: Int
    ): JsonElement

    @POST("gebruiker")
    suspend fun updateGebruikerBody(
        @Header("Authorization") authorization: String,
        @Body request: GebruikerUpdateRequest
    ): JsonElement
}
