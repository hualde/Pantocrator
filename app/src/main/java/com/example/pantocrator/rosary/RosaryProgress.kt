package com.example.pantocrator.rosary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RosaryProgress(
    currentBead: RosaryBead,
    currentDecade: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Progreso general
            Text(
                text = when {
                    currentDecade == 0 -> "Oraciones Iniciales"
                    currentDecade in 1..5 -> "Década $currentDecade de 5"
                    else -> "Oraciones Finales"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )

            // Oración actual
            Text(
                text = "Oración Actual",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
} 