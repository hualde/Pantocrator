package com.example.pantocrator

import android.content.Context
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
import com.example.pantocrator.language.LocaleHelper
import com.example.pantocrator.ui.theme.PantocratorTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.collectAsState
import com.example.pantocrator.data.SettingsDataStore
import com.example.pantocrator.api.DeepSeekApi
import com.example.pantocrator.audio.MusicPlayer

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private lateinit var settingsDataStore: SettingsDataStore
    private lateinit var musicPlayer: MusicPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsDataStore = SettingsDataStore(this)
        musicPlayer = MusicPlayer(this)
        
        enableEdgeToEdge()
        setContent {
            // Observar el tema actual
            val isDarkTheme by settingsDataStore.isDarkTheme.collectAsState(initial = false)
            var currentScreen by remember { mutableStateOf(Screen.Home) }
            
            // Observar el idioma actual
            val currentLanguage by settingsDataStore.language.collectAsState(initial = "Español")
            
            // Observar el estado de la música
            val isMusicEnabled by settingsDataStore.isMusicEnabled.collectAsState(initial = true)
            
            // Aplicar el idioma actual
            LaunchedEffect(currentLanguage) {
                LocaleHelper.setLocale(this@MainActivity, currentLanguage)
            }
            
            // Controlar la música basado en la preferencia
            LaunchedEffect(isMusicEnabled) {
                if (isMusicEnabled) {
                    musicPlayer.startMusic()
                } else {
                    musicPlayer.stopMusic()
                }
            }
            
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
                                            contentDescription = stringResource(id = R.string.back)
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
                            modifier = Modifier.padding(padding),
                            currentLanguage = currentLanguage
                        )
                        Screen.Settings -> SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onThemeChanged = { newTheme ->
                                lifecycleScope.launch {
                                    settingsDataStore.saveDarkTheme(newTheme)
                                }
                            },
                            currentLanguage = currentLanguage,
                            onLanguageChanged = { language ->
                                lifecycleScope.launch {
                                    settingsDataStore.saveLanguage(language)
                                    recreate()
                                }
                            },
                            isMusicEnabled = isMusicEnabled,
                            onMusicEnabledChanged = { enabled ->
                                lifecycleScope.launch {
                                    settingsDataStore.saveMusicEnabled(enabled)
                                }
                            },
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer.stopMusic()
    }

    override fun attachBaseContext(newBase: Context) {
        // Usar runBlocking para obtener el idioma guardado de forma síncrona
        val language = runBlocking {
            SettingsDataStore(newBase).language.first()
        }
        super.attachBaseContext(LocaleHelper.setLocale(newBase, language))
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
            Text(stringResource(id = R.string.confession_button))
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
            Text(stringResource(id = R.string.settings_button))
        }
    }
}

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    currentLanguage: String,
    onLanguageChanged: (String) -> Unit,
    isMusicEnabled: Boolean,
    onMusicEnabledChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Control de tema oscuro
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

        // Control de música
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.background_music),
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = isMusicEnabled,
                onCheckedChange = onMusicEnabledChanged
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
                    onClick = { onLanguageChanged(language) },
                    shape = MaterialTheme.shapes.medium,
                    color = if (language == currentLanguage) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant,
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
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = language,
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (language == currentLanguage) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfesionScreen(
    modifier: Modifier = Modifier,
    currentLanguage: String
) {
    var userInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var isLoading by remember { mutableStateOf(false) }
    val deepSeekApi = remember { DeepSeekApi() }
    val scope = rememberCoroutineScope()
    
    // Obtener los strings fuera de la corrutina
    val errorMessage = stringResource(id = R.string.api_error)
    val initialMessage = stringResource(id = R.string.initial_confession_message)

    // Mensaje inicial del sacerdote
    LaunchedEffect(initialMessage) {
        if (messages.isEmpty()) {
            messages = listOf(
                Message(
                    text = initialMessage,
                    type = MessageType.PRIEST
                )
            )
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Area de mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            reverseLayout = true
        ) {
            items(messages.asReversed()) { message ->
                ChatMessage(
                    message = message,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Area de entrada de texto
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text(stringResource(id = R.string.type_message)) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                FilledIconButton(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            val userMessage = userInput
                            userInput = ""
                            isLoading = true
                            messages = messages + Message(userMessage, MessageType.USER)
                            
                            scope.launch {
                                try {
                                    val response = deepSeekApi.getChatResponse(userMessage, currentLanguage)
                                    messages = messages + Message(response, MessageType.PRIEST)
                                } catch (e: Exception) {
                                    messages = messages + Message(errorMessage, MessageType.PRIEST)
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = userInput.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = stringResource(id = R.string.send_message)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessage(
    message: Message,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.type == MessageType.USER) 
            Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (message.type == MessageType.USER)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (message.type == MessageType.PRIEST) {
                    Icon(
                        Icons.Default.Church,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

data class Message(
    val text: String,
    val type: MessageType
)

enum class MessageType {
    USER, PRIEST
}