package be.ayasrecepten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import be.ayasrecepten.receptenapp.data.RecipeRepository

class RecipeViewmodel : ViewModel() {
    private val repository = RecipeRepository()

    var resultaat = mutableStateOf("Recepten laden....")
        private set

    init {
        laadRecepten()
    }
    fun laadRecepten() {
        viewModelScope.launch {
            try {
                val antwoord = repository.getRecipes()
                resultaat.value = antwoord.toString()
            } catch (e: Exception) {
                resultaat.value = "Fout bij ophalen :${e.message} "

            }
        }
    }
}
