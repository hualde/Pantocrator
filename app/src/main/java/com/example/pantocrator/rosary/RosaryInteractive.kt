package com.example.pantocrator.rosary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pantocrator.R

// Tipos de cuentas del rosario
enum class BeadType {
    CROSS,           // Cruz
    LARGE_BEAD,      // Cuenta grande (Padrenuestro)
    SMALL_BEAD       // Cuenta pequeña (Avemaría)
}

// Estados posibles de cada cuenta
enum class BeadState {
    NOT_STARTED,     // No iniciada
    ACTIVE,          // Cuenta actual
    COMPLETED        // Completada
}

// Clase que representa cada cuenta del rosario
data class RosaryBead(
    val type: BeadType,
    val position: Int,
    var state: BeadState = BeadState.NOT_STARTED,
    val prayerType: String
)

// Clase para manejar el estado del rosario
class RosaryState {
    private val reflections = RosaryReflections()
    var currentMystery by mutableStateOf<RosaryMystery?>(null)
        private set

    // Lista de todas las cuentas del rosario
    val beads = mutableStateListOf<RosaryBead>()
    
    // Índice de la cuenta actual
    var currentBeadIndex by mutableStateOf(0)
        private set

    // Día de la semana y índice del misterio seleccionado
    private var currentDayOfWeek = 1
    private var selectedMysteryType = 0

    fun setMystery(dayOfWeek: Int, mysteryIndex: Int) {
        currentDayOfWeek = dayOfWeek
        selectedMysteryType = mysteryIndex
        updateCurrentMystery()
    }

    private fun updateCurrentMystery() {
        // Calcular la década actual
        val currentDecade = if (currentBeadIndex < 6) {
            0 // Cuentas iniciales
        } else {
            // Restar las cuentas iniciales (6) y dividir por 12 (Padrenuestro + 10 Avemarías + Gloria)
            ((currentBeadIndex - 6) / 12).coerceIn(0, 4)
        }

        // Seleccionar el misterio según el tipo y la década
        currentMystery = when (selectedMysteryType) {
            0 -> reflections.joyfulMysteries[currentDecade]
            1 -> reflections.sorrowfulMysteries[currentDecade]
            2 -> reflections.gloriousMysteries[currentDecade]
            3 -> reflections.luminousMysteries[currentDecade]
            else -> reflections.joyfulMysteries[currentDecade]
        }
    }
    
    // Avanzar a la siguiente cuenta
    fun moveToNextBead() {
        if (currentBeadIndex < beads.size - 1) {
            beads[currentBeadIndex] = beads[currentBeadIndex].copy(state = BeadState.COMPLETED)
            currentBeadIndex++
            beads[currentBeadIndex] = beads[currentBeadIndex].copy(state = BeadState.ACTIVE)
            updateCurrentMystery()
        }
    }
    
    // Retroceder a la cuenta anterior
    fun moveToPreviousBead() {
        if (currentBeadIndex > 0) {
            beads[currentBeadIndex] = beads[currentBeadIndex].copy(state = BeadState.NOT_STARTED)
            currentBeadIndex--
            beads[currentBeadIndex] = beads[currentBeadIndex].copy(state = BeadState.ACTIVE)
            updateCurrentMystery()
        }
    }
    
    init {
        // Inicializar el rosario con el primer misterio
        updateCurrentMystery()
        
        // Cruz
        beads.add(RosaryBead(BeadType.CROSS, 0, BeadState.ACTIVE, "cross"))
        
        // Cuenta grande inicial
        beads.add(RosaryBead(BeadType.LARGE_BEAD, 1, BeadState.NOT_STARTED, "our_father"))
        
        // 3 cuentas pequeñas iniciales (Ave María)
        for (i in 2..4) {
            beads.add(RosaryBead(BeadType.SMALL_BEAD, i, BeadState.NOT_STARTED, "hail_mary"))
        }
        
        // Cuenta grande (Gloria)
        beads.add(RosaryBead(BeadType.LARGE_BEAD, 5, BeadState.NOT_STARTED, "glory"))
        
        // 5 décadas
        var position = 6
        repeat(5) {
            // Cuenta grande (Padrenuestro)
            beads.add(RosaryBead(BeadType.LARGE_BEAD, position, BeadState.NOT_STARTED, "our_father"))
            position++
            
            // 10 cuentas pequeñas (Ave María)
            repeat(10) {
                beads.add(RosaryBead(BeadType.SMALL_BEAD, position, BeadState.NOT_STARTED, "hail_mary"))
                position++
            }
            
            // Cuenta grande (Gloria)
            beads.add(RosaryBead(BeadType.LARGE_BEAD, position, BeadState.NOT_STARTED, "glory"))
            position++
        }
    }
}

@Composable
fun InteractiveRosaryScreen(
    modifier: Modifier = Modifier,
    dayOfWeek: Int,
    mysteryIndex: Int
) {
    val rosaryState = remember { RosaryState() }
    
    // Establecer el misterio actual
    LaunchedEffect(dayOfWeek, mysteryIndex) {
        rosaryState.setMystery(dayOfWeek, mysteryIndex)
    }
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Reflexión del misterio
        rosaryState.currentMystery?.let { mystery ->
            RosaryReflectionView(
                mystery = mystery,
                currentBeadIndex = rosaryState.currentBeadIndex,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Área del rosario gráfico
        RosaryLayout(
            beads = rosaryState.beads,
            currentBeadIndex = rosaryState.currentBeadIndex,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Botones de control
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { rosaryState.moveToPreviousBead() },
                enabled = rosaryState.currentBeadIndex > 0
            ) {
                Text("Anterior")
            }
            
            Button(
                onClick = { rosaryState.moveToNextBead() },
                enabled = rosaryState.currentBeadIndex < rosaryState.beads.size - 1
            ) {
                Text("Completar")
            }
        }
    }
} 