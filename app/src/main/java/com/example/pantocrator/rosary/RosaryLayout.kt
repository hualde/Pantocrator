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

@Composable
fun RosaryLayout(
    beads: List<RosaryBead>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // Indicador de scroll
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PanTool,
                    contentDescription = "Deslizar horizontalmente",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Desliza lateralmente para ver más",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Cruz (sin conectores)
            RosaryBeadView(
                bead = beads[0],
                showConnectorBefore = false,
                showConnectorAfter = false,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Cuentas iniciales
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
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
} 