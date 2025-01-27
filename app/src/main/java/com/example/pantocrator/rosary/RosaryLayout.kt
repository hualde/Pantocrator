package com.example.pantocrator.rosary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun RosaryLayout(
    beads: List<RosaryBead>,
    currentBeadIndex: Int,
    modifier: Modifier = Modifier
) {
    // Calcular la década actual
    val currentDecade = when {
        currentBeadIndex < 6 -> 0  // Oraciones iniciales
        else -> ((currentBeadIndex - 6) / 12) + 1  // Cada década tiene 12 cuentas (1 Padrenuestro + 10 Avemarías + 1 Gloria)
    }

    // Estados de scroll para cada fila
    val initialScrollState = rememberScrollState()
    val decadeScrollStates = List(5) { rememberScrollState() }
    val coroutineScope = rememberCoroutineScope()

    // Efecto para manejar el scroll automático
    LaunchedEffect(currentBeadIndex) {
        val targetScrollState = when {
            currentBeadIndex < 6 -> initialScrollState
            else -> decadeScrollStates[(currentBeadIndex - 6) / 12]
        }

        // Calcular la posición aproximada de scroll
        val scrollPosition = when {
            currentBeadIndex < 6 -> (currentBeadIndex * 52).toFloat() // 40dp (cuenta) + 12dp (conector)
            else -> {
                val positionInDecade = (currentBeadIndex - 6) % 12
                // Cada cuenta grande = 40dp + 12dp (conector)
                // Cada cuenta pequeña = 32dp + 12dp (conector)
                if (positionInDecade == 0) {
                    0f // Inicio de década (Padrenuestro)
                } else {
                    // Scroll lineal: aumentamos significativamente el scroll por cuenta
                    val scrollPerBead = 120f  // Aumentado de 80f a 120f
                    (scrollPerBead * (positionInDecade - 1)).toFloat()
                }
            }
        }

        coroutineScope.launch {
            targetScrollState.animateScrollTo(scrollPosition.toInt())
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Progreso del rosario
        RosaryProgress(
            currentBead = beads[currentBeadIndex],
            currentDecade = currentDecade,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cruz (sin conectores)
        RosaryBeadView(
            bead = beads[0],
            showConnectorBefore = false,
            showConnectorAfter = false,
            modifier = Modifier
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cuentas iniciales
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.horizontalScroll(initialScrollState)
        ) {
            RosaryBeadView(bead = beads[1], showConnectorBefore = true)
            for (i in 2..4) {
                RosaryBeadView(bead = beads[i])
            }
            RosaryBeadView(bead = beads[5])
        }
        
        // Décadas del rosario
        var currentIndex = 6
        repeat(5) { decade ->
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.horizontalScroll(decadeScrollStates[decade])
            ) {
                // Padrenuestro (cuenta grande)
                RosaryBeadView(
                    bead = beads[currentIndex],
                    showConnectorBefore = true
                )
                currentIndex++
                
                // 10 Avemarías (cuentas pequeñas)
                for (i in 0..9) {
                    RosaryBeadView(bead = beads[currentIndex + i])
                }
                currentIndex += 10
                
                // Gloria al final de la década
                RosaryBeadView(
                    bead = beads[currentIndex],
                    showConnectorAfter = decade != 4
                )
                currentIndex++
            }
        }
    }
} 