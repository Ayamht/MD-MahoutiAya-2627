package be.ayasrecepten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import be.ayasrecepten.receptenapp.data.RecipeRepository

class RecipeViewmodel : ViewModel() {

    private val repository = RecipeRepository()

    // Recepten
    var resultaat = mutableStateOf("Recepten laden....")
        private set

    // Gebruikersgegevens
    var gebruiker = mutableStateOf("")
        private set

    // Meldingen
    var melding = mutableStateOf("")
        private set

    init {
        laadRecepten()
        laadGebruiker()
    }

    // Recepten ophalen
    fun laadRecepten() {
        viewModelScope.launch {
            try {
                val antwoord = repository.getRecipes()
                resultaat.value = antwoord.toString()
            } catch (e: Exception) {
                resultaat.value = "Fout bij ophalen: ${e.message}"
            }
        }
    }

    // Gebruiker ophalen
    fun laadGebruiker() {
        viewModelScope.launch {
            try {
                val antwoord = repository.getGebruiker()
                gebruiker.value = antwoord.toString()
            } catch (e: Exception) {
                gebruiker.value = "Fout bij gebruiker: ${e.message}"
            }
        }
    }

    // Alias en ervaring aanpassen
    fun updateGebruiker(alias: String, ervaring: Int) {

        if (ervaring !in 0..99) {
            melding.value = "Ervaring moet tussen 0 en 99 liggen"
            return
        }

        viewModelScope.launch {
            try {
                repository.updateGebruiker(alias, ervaring)
                melding.value = "Gebruiker aangepast!"
                laadGebruiker()
            } catch (e: Exception) {
                melding.value = "Fout bij opslaan: ${e.message}"
            }
        }
    }
}
