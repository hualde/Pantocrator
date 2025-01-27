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
    val isInitialBeads = currentBeadIndex < 6
    
    // Solo mostrar la tarjeta si no estamos en las cuentas iniciales
    if (!isInitialBeads) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = mystery.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
                )

                // Determinar el tipo de oración y la década actual
                val currentDecade = (currentBeadIndex - 6) / 12
                val positionInDecade = (currentBeadIndex - 6) % 12

                // Seleccionar el texto a mostrar según la posición
                val displayText = when {
                    // Padrenuestro (inicio de década) o Gloria (fin de década)
                    positionInDecade == 0 || positionInDecade == 11 -> mystery.reflection
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
} 