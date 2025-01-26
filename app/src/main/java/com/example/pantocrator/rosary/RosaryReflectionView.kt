package com.example.pantocrator.rosary

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pantocrator.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RosaryReflectionView(
    mystery: RosaryMystery,
    currentBeadIndex: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título: mostrar "Oraciones Iniciales" durante las primeras cuentas
            val isInitialBeads = currentBeadIndex < 6
            Text(
                text = if (isInitialBeads) stringResource(R.string.initial_prayers) else mystery.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
            )

            // Determinar el tipo de oración y la década actual
            val currentDecade = if (isInitialBeads) 0 else (currentBeadIndex - 6) / 12
            val positionInDecade = if (isInitialBeads) currentBeadIndex else (currentBeadIndex - 6) % 12

            // Seleccionar el texto a mostrar según la posición
            val displayText = when {
                // Cuentas iniciales: mostrar texto introductorio
                isInitialBeads -> stringResource(R.string.initial_prayers_reflection)
                // Padrenuestro (inicio de década)
                positionInDecade == 0 -> mystery.reflection
                // Gloria (fin de década)
                positionInDecade == 11 -> mystery.reflection
                // Ave María (durante la década)
                else -> mystery.shortMeditations[currentDecade]
            }
            
            AnimatedContent(
                targetState = displayText,
                transitionSpec = {
                    fadeIn() + slideInVertically { height -> height } togetherWith
                    fadeOut() + slideOutVertically { height -> -height }
                }
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
} 