package com.example.pantocrator.rosary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RosaryBeadView(
    bead: RosaryBead,
    showConnectorBefore: Boolean = true,
    showConnectorAfter: Boolean = true,
    modifier: Modifier = Modifier
) {
    val beadSize = when (bead.type) {
        BeadType.CROSS -> 48.dp
        BeadType.LARGE_BEAD -> 40.dp
        BeadType.SMALL_BEAD -> 32.dp
    }
    
    val beadColor = when (bead.state) {
        BeadState.NOT_STARTED -> MaterialTheme.colorScheme.surfaceVariant
        BeadState.ACTIVE -> MaterialTheme.colorScheme.primary
        BeadState.COMPLETED -> MaterialTheme.colorScheme.secondary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        // Conector antes de la cuenta
        if (showConnectorBefore) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
        }

        // La cuenta (cruz o circular)
        if (bead.type == BeadType.CROSS) {
            Box(
                modifier = Modifier
                    .width(beadSize)
                    .height(beadSize * 1.3f)
            ) {
                // Vertical
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(8.dp)
                        .background(beadColor)
                        .align(Alignment.Center)
                )
                // Horizontal
                Box(
                    modifier = Modifier
                        .width(beadSize)
                        .height(8.dp)
                        .background(beadColor)
                        .align(Alignment.Center)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(beadSize)
                    .clip(CircleShape)
                    .background(beadColor)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
            )
        }

        // Conector después de la cuenta
        if (showConnectorAfter) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
        }
    }
} 