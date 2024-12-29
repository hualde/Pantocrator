package com.example.pantocrator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pantocrator.ui.theme.PantocratorTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf(Screen.Home) }
            
            PantocratorTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { 
                                Text(
                                    text = when(currentScreen) {
                                        Screen.Home -> stringResource(id = R.string.app_name)
                                        Screen.Confesion -> stringResource(id = R.string.confession)
                                        Screen.Settings -> stringResource(id = R.string.settings)
                                    }
                                )
                            },
                            navigationIcon = {
                                if (currentScreen != Screen.Home) {
                                    IconButton(onClick = { currentScreen = Screen.Home }) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "Volver"
                                        )
                                    }
                                }
                            },
                            actions = {
                                if (currentScreen == Screen.Home) {
                                    IconButton(onClick = { currentScreen = Screen.Settings }) {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = stringResource(id = R.string.settings)
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) { padding ->
                    when (currentScreen) {
                        Screen.Home -> HomeScreen(
                            onConfesionClick = { currentScreen = Screen.Confesion },
                            onSettingsClick = { currentScreen = Screen.Settings },
                            modifier = Modifier.padding(padding)
                        )
                        Screen.Confesion -> ConfesionScreen(
                            modifier = Modifier.padding(padding)
                        )
                        Screen.Settings -> SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onThemeChanged = { isDarkTheme = it },
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }
        }
    }
}

enum class Screen {
    Home,
    Confesion,
    Settings
}

@Composable
fun HomeScreen(
    onConfesionClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedButton(
            onClick = onConfesionClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(
                Icons.Default.Church,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Confesión")
        }
        
        OutlinedButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Ajustes")
        }
    }
}

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tema oscuro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.dark_theme),
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onThemeChanged
            )
        }

        Divider()

        // Selector de idioma
        Text(
            text = stringResource(id = R.string.language),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val languages = listOf(
                "Español" to "🇪🇸",
                "English" to "🇬🇧",
                "Français" to "🇫🇷",
                "Italiano" to "🇮🇹",
                "Português" to "🇵🇹"
            )

            items(languages) { (language, flag) ->
                Surface(
                    onClick = { /* Implementar cambio de idioma */ },
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = flag,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = language,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfesionScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Aquí va el contenido del chat
    }
}