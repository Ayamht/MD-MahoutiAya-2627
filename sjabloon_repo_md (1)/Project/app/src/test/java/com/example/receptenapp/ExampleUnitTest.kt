package com.example.receptenapp

import be.ayasrecepten.receptenapp.network.RetrofitInstance
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testApiGebruiker() = runBlocking {
        val res = RetrofitInstance.api.getGebruiker("Bearer qzpapnvmyro49p6qom72dj21q1laeye")
        fail("GEBRUIKER RESPONSE: $res")
    }
}