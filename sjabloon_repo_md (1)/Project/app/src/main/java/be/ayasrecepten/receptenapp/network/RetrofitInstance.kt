package be.ayasrecepten.receptenapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Project Test - Code : SDFGBRTY

object RetrofitInstance {

    private const val BASE_URL =
        "https://stevenop.be/recipes/api/"

    val api: RecipeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(RecipeApiService::class.java)
    }
}
