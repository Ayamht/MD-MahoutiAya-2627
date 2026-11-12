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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.example.receptenapp.ui.theme.ReceptenappTheme
import be.ayasrecepten.receptenapp.viewmodel.RecipeViewmodel

class MainActivity : ComponentActivity() {

    private val vm: RecipeViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReceptenappTheme {
                var toonGebruiker by remember {
                    mutableStateOf(false)
                }

                if (toonGebruiker) {
                    GebruikerScherm(
                        jsonTekst = vm.gebruiker.value,
                        melding = vm.melding.value,
                        opslaan = { alias, ervaring ->
                            vm.updateGebruiker(alias, ervaring)
                        },
                        terug = {
                            toonGebruiker = false
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp)
                    ) {
                        Button(
                            onClick = {
                                vm.laadGebruiker()
                                toonGebruiker = true
                            },
                            modifier = Modifier.padding(
                                horizontal = 16.dp
                            )
                        ) {
                            Text("Mijn profiel")
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            ReceptenScherm(
                                jsonTekst = vm.resultaat.value,
                                vernieuw = {
                                    vm.laadRecepten()
                                },
                                delen = { tekst ->
                                    val intent =
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                tekst
                                            )
                                        }

                                    startActivity(
                                        Intent.createChooser(
                                            intent,
                                            "Deel recept"
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
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
    var gekozen by remember {
        mutableStateOf<JsonObject?>(null)
    }

    var sorteerLikes by remember {
        mutableStateOf(false)
    }

    var sorteerDatum by remember {
        mutableStateOf(false)
    }

    var toonFormulier by remember {
        mutableStateOf(false)
    }

    // Recepten uit de API-response halen
    val recepten = remember(
        jsonTekst,
        sorteerLikes,
        sorteerDatum
    ) {
        try {
            val root = JsonParser.parseString(jsonTekst)

            val lijst = if (root.isJsonArray) {
                root.asJsonArray
            } else {
                root.asJsonObject.getAsJsonArray("data")
            }

            val items = lijst.mapNotNull {
                if (it.isJsonObject) it.asJsonObject
                else null
            }

            when {
                sorteerLikes -> items.sortedByDescending {
                    it.get("likes")?.asInt
                        ?: it.get("like")?.asInt
                        ?: 0
                }

                sorteerDatum -> items.sortedByDescending {
                    it.get("datumAangemaakt")?.asString ?: ""
                }

                else -> items
            }

        } catch (e: Exception) {
            emptyList<JsonObject>()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 8.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Text(
            text = "Mijn recepten",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = vernieuw) {
            Text("Vernieuwen")
        }

        Row {
            Button(
                onClick = {
                    sorteerLikes = !sorteerLikes
                    sorteerDatum = false
                }
            ) {
                Text("Likes")
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = {
                    sorteerDatum = !sorteerDatum
                    sorteerLikes = false
                }
            ) {
                Text("Datum")
            }
        }

        Button(
            onClick = {
                toonFormulier = !toonFormulier
                gekozen = null
            }
        ) {
            Text(
                if (toonFormulier) "Sluiten"
                else "Nieuw recept"
            )
        }

        Spacer(Modifier.height(12.dp))

        if (toonFormulier) {

            ReceptToevoegen { titel, ingredienten, stappen, tijd ->
                // Het formulier werkt voorlopig lokaal.
                // Voor opslaan op de server is nog
                // een POST-aanroep nodig.
            }

        } else if (gekozen != null) {

            val recept = gekozen!!

            Button(
                onClick = {
                    gekozen = null
                }
            ) {
                Text("Terug")
            }

            Text(
                text = recept.get("titel")?.asString ?: "Recept",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Ingrediënten: " +
                        (recept.get("ingredienten")?.asString ?: "")
            )

            Text(
                "Bereiding: " +
                        (recept.get("stappen")?.asString ?: "")
            )

            Text(
                "Tijd: " +
                        (recept.get("tijd")?.asString ?: "?") +
                        " minuten"
            )

            Text(
                "❤️ Likes: " +
                        (recept.get("likes")?.asInt
                            ?: recept.get("like")?.asInt
                            ?: 0)
            )

            Button(
                onClick = {
                    delen(
                        "${recept.get("titel")?.asString ?: "Recept"}\n" +
                                "${recept.get("ingredienten")?.asString ?: ""}\n" +
                                "${recept.get("stappen")?.asString ?: ""}"
                    )
                }
            ) {
                Text("Delen")
            }

        } else if (recepten.isEmpty()) {

            Text(
                if (jsonTekst.startsWith("Fout")) {
                    jsonTekst
                } else {
                    "Geen recepten gevonden of gegevens worden geladen."
                }
            )

        } else {

            LazyColumn {
                items(recepten) { recept ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                gekozen = recept
                            }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = recept.get("titel")?.asString
                                ?: "Onbekend recept",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "❤️ Likes: " +
                                    (recept.get("likes")?.asInt
                                        ?: recept.get("like")?.asInt
                                        ?: 0)
                        )

                        Text(
                            text = "Bereidingstijd: " +
                                    (recept.get("tijd")?.asString ?: "?") +
                                    " minuten"
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
    var titel by remember {
        mutableStateOf("")
    }

    var ingredienten by remember {
        mutableStateOf("")
    }

    var stappen by remember {
        mutableStateOf("")
    }

    var tijd by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Nieuw recept",
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

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val minuten = tijd.toIntOrNull()

                if (
                    titel.isNotBlank() &&
                    ingredienten.isNotBlank() &&
                    stappen.isNotBlank() &&
                    minuten != null &&
                    minuten > 0
                ) {
                    opslaan(
                        titel,
                        ingredienten,
                        stappen,
                        minuten
                    )
                }
            }
        ) {
            Text("Recept opslaan")
        }
    }
}

// NIEUW: Gebruikersscherm voor de examenopdracht

@Composable
fun GebruikerScherm(
    jsonTekst: String,
    melding: String,
    opslaan: (String, Int) -> Unit,
    terug: () -> Unit
) {
    var alias by remember {
        mutableStateOf("")
    }

    var ervaring by remember {
        mutableStateOf("")
    }

    val gebruiker = remember(jsonTekst) {
        try {
            JsonParser.parseString(jsonTekst)
                .asJsonObject
                .getAsJsonObject("data")
        } catch (e: Exception) {
            null
        }
    }

    // Invoervelden vullen met de gegevens uit de API
    LaunchedEffect(gebruiker) {
        if (gebruiker != null) {
            alias = gebruiker.get("alias")?.asString ?: ""
            ervaring = gebruiker.get("ervaring")?.asString ?: ""
        }
    }

    val requestCount = try {
        gebruiker?.get("requestCount")?.asInt
    } catch (e: Exception) {
        null
    }

    val huidigeErvaring = try {
        gebruiker?.get("ervaring")?.asInt
    } catch (e: Exception) {
        null
    }

    val aantalJaren = ervaring.toIntOrNull()

    val geldig = alias.isNotBlank() &&
            aantalJaren != null &&
            aantalJaren in 0..99

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                top = 40.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Button(onClick = terug) {
            Text("Terug")
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Mijn profiel",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        if (gebruiker == null) {
            Text(
                if (jsonTekst.startsWith("Fout")) {
                    jsonTekst
                } else {
                    "Gebruikersgegevens laden..."
                }
            )
        } else {

            // Bestaande gegevens
            Text(
                "Naam: " +
                        (gebruiker.get("naam")?.asString ?: "")
            )

            Text(
                "Voornaam: " +
                        (gebruiker.get("voornaam")?.asString ?: "")
            )

            Text(
                "Nummer: " +
                        (gebruiker.get("nummer")?.asString ?: "")
            )

            Text(
                "Laatst actief: " +
                        (gebruiker.get("laatstActief")?.asString ?: "")
            )

            Spacer(Modifier.height(12.dp))

            // NIEUW: aantal requests
            Text(
                "Aantal requests: ${requestCount ?: "Onbekend"}"
            )

            // NIEUW: kookervaring
            Text(
                "Kookervaring: " +
                        (huidigeErvaring?.let { "$it jaar" } ?: "Onbekend")
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Gegevens aanpassen",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            // Alias aanpassen
            OutlinedTextField(
                value = alias,
                onValueChange = {
                    alias = it
                },
                label = {
                    Text("Alias")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // NIEUW: ervaring aanpassen
            OutlinedTextField(
                value = ervaring,
                onValueChange = {
                    ervaring = it
                },
                label = {
                    Text("Ervaring in jaren")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                isError = ervaring.isNotEmpty() &&
                        (aantalJaren == null || aantalJaren !in 0..99),
                modifier = Modifier.fillMaxWidth()
            )

            if (aantalJaren == null || aantalJaren !in 0..99) {
                Text(
                    text = "Ervaring moet een getal tussen 0 en 99 zijn.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(16.dp))

            // Alias en ervaring naar de API sturen
            Button(
                onClick = {
                    if (geldig && aantalJaren != null) {
                        opslaan(alias, aantalJaren)
                    }
                },
                enabled = geldig
            ) {
                Text("Opslaan")
            }

            if (melding.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(melding)
            }
        }
    }
}
