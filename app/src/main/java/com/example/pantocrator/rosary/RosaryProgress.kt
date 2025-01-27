package com.example.pantocrator.rosary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun RosaryProgress(
    currentBead: RosaryBead,
    currentDecade: Int,
    modifier: Modifier = Modifier
) {
    var showPrayerDialog by remember { mutableStateOf(false) }

    // Única tarjeta con toda la información
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showPrayerDialog = true },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = when {
                    currentDecade == 0 -> "Oraciones Iniciales"
                    currentDecade in 1..5 -> "Década $currentDecade de 5"
                    else -> "Oraciones Finales"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                text = when (currentBead.type) {
                    BeadType.CROSS -> "Señal de la Cruz"
                    BeadType.LARGE_BEAD -> when (currentBead.prayerType) {
                        "our_father" -> "Padre Nuestro"
                        "glory" -> "Gloria"
                        else -> "Padre Nuestro"
                    }
                    BeadType.SMALL_BEAD -> "Ave María"
                },
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showPrayerDialog) {
        AlertDialog(
            onDismissRequest = { showPrayerDialog = false },
            title = {
                Text(
                    text = when (currentBead.type) {
                        BeadType.CROSS -> "Señal de la Cruz"
                        BeadType.LARGE_BEAD -> when (currentBead.prayerType) {
                            "our_father" -> "Padre Nuestro"
                            "glory" -> "Gloria"
                            else -> "Padre Nuestro"
                        }
                        BeadType.SMALL_BEAD -> "Ave María"
                    }
                )
            },
            text = {
                Text(
                    text = when (currentBead.type) {
                        BeadType.CROSS -> "Por la señal de la Santa Cruz, de nuestros enemigos, líbranos Señor, Dios nuestro. En el nombre del Padre, y del Hijo, y del Espíritu Santo. Amén."
                        BeadType.LARGE_BEAD -> when (currentBead.prayerType) {
                            "our_father" -> "Padre nuestro, que estás en el cielo, santificado sea tu Nombre; venga a nosotros tu reino; hágase tu voluntad en la tierra como en el cielo. Danos hoy nuestro pan de cada día; perdona nuestras ofensas, como también nosotros perdonamos a los que nos ofenden; no nos dejes caer en la tentación, y líbranos del mal. Amén."
                            "glory" -> "Gloria al Padre, al Hijo y al Espíritu Santo. Como era en el principio, ahora y siempre, por los siglos de los siglos. Amén."
                            else -> "Padre Nuestro"
                        }
                        BeadType.SMALL_BEAD -> "Dios te salve María, llena eres de gracia, el Señor es contigo. Bendita tú eres entre todas las mujeres y bendito es el fruto de tu vientre Jesús. Santa María, Madre de Dios, ruega por nosotros pecadores, ahora y en la hora de nuestra muerte. Amén."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrayerDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
} 