package be.ayasrecepten.receptenapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.JsonParser
import com.google.gson.JsonObject
import com.example.receptenapp.ui.theme.ReceptenappTheme
import be.ayasrecepten.receptenapp.viewmodel.RecipeViewmodel

class MainActivity : ComponentActivity() {

    private val vm: RecipeViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReceptenappTheme {
                ReceptenScherm(
                    jsonTekst = vm.resultaat.value,
                    vernieuw = { vm.laadRecepten() },
                    delen = { tekst ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, tekst)
                        }
                        startActivity(Intent.createChooser(intent, "Deel recept"))
                    }
                )
            }
        }
    }
}

@Composable
fun ReceptenScherm(
    jsonTekst: String,
    vernieuw: () -> Unit,
    delen: (String) -> Unit
) {
    var gekozen by remember { mutableStateOf<JsonObject?>(null) }
    var sorteerLikes by remember { mutableStateOf(false) }

    val recepten = remember(jsonTekst, sorteerLikes) {
        try {
            val root = JsonParser.parseString(jsonTekst)
            val lijst = if (root.isJsonArray) {
                root.asJsonArray
            } else {
                root.asJsonObject.getAsJsonArray("data")
            }

            val items = lijst.mapNotNull {
                if (it.isJsonObject) it.asJsonObject else null
            }

            if (sorteerLikes) {
                items.sortedByDescending {
                    it.get("likes")?.asInt ?: 0
                }
            } else {
                items
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Mijn recepten", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        Row {
            Button(onClick = vernieuw) {
                Text("Vernieuwen")
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = { sorteerLikes = !sorteerLikes }) {
                Text(if (sorteerLikes) "Normale volgorde" else "Sorteer op likes")
            }
        }

        Spacer(Modifier.height(12.dp))

        if (gekozen != null) {
            val recept = gekozen!!

            Button(onClick = { gekozen = null }) {
                Text("Terug")
            }

            Text(
                recept.get("titel")?.asString ?: "Recept",
                style = MaterialTheme.typography.headlineSmall
            )
            Text("Ingrediënten: ${recept.get("ingredienten")?.asString ?: ""}")
            Text("Bereiding: ${recept.get("stappen")?.asString ?: ""}")
            Text("Tijd: ${recept.get("tijd")?.asString ?: "?"} minuten")

            Button(onClick = {
                delen(
                    "${recept.get("titel")?.asString}\n" +
                            "${recept.get("ingredienten")?.asString}\n" +
                            "${recept.get("stappen")?.asString}"
                )
            }) {
                Text("Delen")
            }
        } else if (recepten.isEmpty()) {
            Text(
                if (jsonTekst.startsWith("Fout")) jsonTekst
                else "Geen recepten gevonden of gegevens worden geladen."
            )
        } else {
            LazyColumn {
                items(recepten) { recept ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { gekozen = recept }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            recept.get("titel")?.asString ?: "Onbekend recept",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Bereidingstijd: " +
                                    "${recept.get("tijd")?.asString ?: "?"} minuten"
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ReceptToevoegen(
    opslaan: (String, String, String, Int) -> Unit
) {
    var titel by remember { mutableStateOf("") }
    var ingredienten by remember { mutableStateOf("") }
    var stappen by remember { mutableStateOf("") }
    var tijd by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Nieuw recept",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = titel,
            onValueChange = { titel = it },
            label = { Text("Titel") }
        )

        OutlinedTextField(
            value = ingredienten,
            onValueChange = { ingredienten = it },
            label = { Text("Ingrediënten") }
        )

        OutlinedTextField(
            value = stappen,
            onValueChange = { stappen = it },
            label = { Text("Bereidingsstappen") }
        )

        OutlinedTextField(
            value = tijd,
            onValueChange = { tijd = it },
            label = { Text("Tijd in minuten") }
        )

        Button(
            onClick = {
                val minuten = tijd.toIntOrNull()

                if (
                    titel.isNotBlank() &&
                    ingredienten.isNotBlank() &&
                    stappen.isNotBlank() &&
                    minuten != null
                ) {
                    opslaan(titel, ingredienten, stappen, minuten)
                }
            }
        ) {
            Text("Recept opslaan")
        }
    }
}

