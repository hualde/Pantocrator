package com.example.pantocrator

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import com.example.pantocrator.rosary.InteractiveRosaryScreen

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
            var showSplash by remember { mutableStateOf(true) }
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
                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { 
                                    Text(
                                        text = when(currentScreen) {
                                            Screen.Home -> stringResource(id = R.string.app_name)
                                            Screen.Confesion -> stringResource(id = R.string.confession)
                                            Screen.Settings -> stringResource(id = R.string.settings)
                                            Screen.PrayerGuide -> stringResource(id = R.string.prayer_guide)
                                            Screen.Rosary -> stringResource(id = R.string.rosary)
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
                                onPrayerGuideClick = { currentScreen = Screen.PrayerGuide },
                                onRosaryClick = { currentScreen = Screen.Rosary },
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
                            Screen.PrayerGuide -> PrayerGuideScreen(
                                modifier = Modifier.padding(padding)
                            )
                            Screen.Rosary -> RosaryScreen(
                                modifier = Modifier.padding(padding)
                            )
                        }
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
    Settings,
    PrayerGuide,
    Rosary
}

@Composable
fun HomeScreen(
    onConfesionClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPrayerGuideClick: () -> Unit,
    onRosaryClick: () -> Unit,
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
        
        ElevatedButton(
            onClick = onPrayerGuideClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(
                Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(stringResource(id = R.string.prayer_guide_button))
        }

        ElevatedButton(
            onClick = onRosaryClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(stringResource(id = R.string.rosary_button))
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
fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    onAnimationCompleted: () -> Unit = {}
) {
    var displayedText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }
    
    LaunchedEffect(text) {
        displayedText = ""
        currentIndex = 0
        while (currentIndex < text.length) {
            delay(25) // Velocidad de escritura
            displayedText = text.substring(0, currentIndex + 1)
            currentIndex++
        }
        onAnimationCompleted()
    }
    
    Text(
        text = displayedText,
        style = style,
        modifier = modifier
    )
}

@Composable
fun ChatMessage(
    message: Message,
    onAnimationCompleted: () -> Unit = {},
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
            if (message.type == MessageType.PRIEST && message.isAnimated && !message.isCompleted) {
                AnimatedText(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    onAnimationCompleted = onAnimationCompleted
                )
            } else {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

data class Message(
    val text: String,
    val type: MessageType,
    val isAnimated: Boolean = false,
    val isCompleted: Boolean = !isAnimated
)

enum class MessageType {
    USER, PRIEST
}

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // Mostrar splash durante 2 segundos
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),  // Fondo negro
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.inicio_image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentScale = ContentScale.Fit
        )
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
    var showConfirmationButtons by remember { mutableStateOf(false) }
    var confessionEnded by remember { mutableStateOf(false) }
    val deepSeekApi = remember { DeepSeekApi() }
    val scope = rememberCoroutineScope()
    
    // Obtener los strings fuera de la corrutina
    val errorMessage = stringResource(id = R.string.api_error)
    val initialMessage = stringResource(id = R.string.initial_confession_message)
    val greetingUser = stringResource(id = R.string.confession_greeting_user)
    val greetingPriest = stringResource(id = R.string.confession_greeting_priest)
    val continueMessages = stringArrayResource(id = R.array.continue_confession_priest)
    val finalBlessings = stringArrayResource(id = R.array.final_blessings)

    // Función para marcar un mensaje como completado
    fun completeMessage(index: Int) {
        messages = messages.mapIndexed { i, message ->
            if (i == index) message.copy(isCompleted = true) else message
        }
    }

    // Función para añadir mensajes secuencialmente
    suspend fun addPriestMessages(newMessages: List<String>) {
        for (message in newMessages) {
            messages = messages + Message(message, MessageType.PRIEST, isAnimated = true)
            // Esperar a que el mensaje actual se complete antes de añadir el siguiente
            while (messages.last().isAnimated && !messages.last().isCompleted) {
                delay(100)
            }
        }
    }
    
    // Mensajes iniciales
    LaunchedEffect(initialMessage) {
        if (messages.isEmpty()) {
            messages = listOf(Message(greetingUser, MessageType.USER))
            addPriestMessages(listOf(greetingPriest, initialMessage))
        }
    }

    // Función para dividir el texto en mensajes
    fun splitResponseIntoMessages(response: String): List<String> {
        // Dividir por párrafos (líneas vacías)
        val byParagraphs = response.split("\n\n").filter { it.isNotBlank() }
        if (byParagraphs.size > 1) return byParagraphs

        // Si no hay párrafos, dividir por puntos seguidos de espacio
        val sentences = response.split(". ").filter { it.isNotBlank() }
            .map { if (!it.endsWith(".")) "$it." else it }
        
        // Si hay menos de 2 oraciones, devolver el texto original como único mensaje
        if (sentences.size < 2) return listOf(response)
        
        // Agrupar las oraciones en mensajes más naturales
        return sentences.chunked(2) { chunk -> chunk.joinToString(" ") }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
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
                    onAnimationCompleted = {
                        val index = messages.indexOf(message)
                        if (index != -1) {
                            completeMessage(index)
                        }
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Botones de confirmación
        if (showConfirmationButtons && !confessionEnded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        showConfirmationButtons = false
                        confessionEnded = true
                        scope.launch {
                            addPriestMessages(listOf(finalBlessings.random()))
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = R.string.end_confession))
                }
                
                Button(
                    onClick = { 
                        showConfirmationButtons = false
                        scope.launch {
                            addPriestMessages(listOf(continueMessages.random()))
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = R.string.continue_button))
                }
            }
        }

        // Area de entrada de texto
        if (!showConfirmationButtons && !confessionEnded) {
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
                                        val messagesList = splitResponseIntoMessages(response)
                                        addPriestMessages(messagesList)
                                        delay(1000)
                                        showConfirmationButtons = true
                                    } catch (e: Exception) {
                                        addPriestMessages(listOf(errorMessage))
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
}

@Composable
fun PrayerGuideScreen(
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableStateOf<Int?>(null) }
    var selectedPrayer by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            selectedPrayer != null -> {
                // Mostrar el texto completo de la oración
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { selectedPrayer = null }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                    
                    Text(
                        text = stringResource(id = getResourceIdByName(selectedPrayer!!)),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            selectedCategoryIndex == null -> {
                // Mostrar lista de categorías
                val categories = stringArrayResource(id = R.array.prayer_categories)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories.size) { index ->
                        Surface(
                            onClick = { selectedCategoryIndex = index },
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = categories[index],
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
            
            else -> {
                // Mostrar oraciones de la categoría seleccionada
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { selectedCategoryIndex = null }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                    
                    val categories = stringArrayResource(id = R.array.prayer_categories)
                    Text(
                        text = categories[selectedCategoryIndex!!],
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    val prayers = when (selectedCategoryIndex!!) {
                        0 -> stringArrayResource(R.array.main_prayers)
                        1 -> stringArrayResource(R.array.rosary_mysteries)
                        2 -> stringArrayResource(R.array.situational_prayers)
                        3 -> stringArrayResource(R.array.sacramental_prayers)
                        4 -> stringArrayResource(R.array.popular_devotions)
                        5 -> stringArrayResource(R.array.daily_prayers)
                        else -> emptyArray()
                    }
                    
                    val prayerResourceNames = when (selectedCategoryIndex!!) {
                        0 -> listOf("prayer_padre_nuestro", "prayer_ave_maria", "prayer_gloria", "prayer_salve_regina", "prayer_fatima")
                        1 -> listOf("prayer_mysteries_joyful", "prayer_mysteries_sorrowful", "prayer_mysteries_glorious", "prayer_mysteries_luminous")
                        2 -> listOf("prayer_act_of_contrition", "prayer_psalm_51", "prayer_san_miguel", "prayer_psalm_91", "prayer_te_deum", "prayer_magnificat", "prayer_serenity", "prayer_psalm_23")
                        3 -> listOf("prayer_baptism", "prayer_communion", "prayer_confession", "prayer_marriage")
                        4 -> listOf("prayer_rosary", "prayer_divine_mercy", "prayer_via_crucis")
                        5 -> listOf("prayer_morning", "prayer_angelus", "prayer_night", "prayer_examination")
                        else -> emptyList()
                    }
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(prayers.size) { index ->
                            Surface(
                                onClick = { selectedPrayer = prayerResourceNames[index] },
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = prayers[index],
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
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
fun RosaryScreen(
    modifier: Modifier = Modifier
) {
    var selectedMysteryIndex by remember { mutableStateOf<Int?>(null) }
    // Obtener el día actual de la semana (1 = Lunes, 7 = Domingo)
    val currentDayOfWeek = remember {
        java.time.LocalDate.now().dayOfWeek.value
    }

    if (selectedMysteryIndex == null) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.rosary_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            // Calendario semanal
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.choose_day),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val weekDays = listOf(
                            Triple(stringResource(id = R.string.monday), 0, stringResource(id = R.string.joyful_mysteries)),
                            Triple(stringResource(id = R.string.tuesday), 1, stringResource(id = R.string.sorrowful_mysteries)),
                            Triple(stringResource(id = R.string.wednesday), 2, stringResource(id = R.string.glorious_mysteries)),
                            Triple(stringResource(id = R.string.thursday), 3, stringResource(id = R.string.luminous_mysteries)),
                            Triple(stringResource(id = R.string.friday), 1, stringResource(id = R.string.sorrowful_mysteries)),
                            Triple(stringResource(id = R.string.saturday), 0, stringResource(id = R.string.joyful_mysteries)),
                            Triple(stringResource(id = R.string.sunday), 2, stringResource(id = R.string.glorious_mysteries))
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                userScrollEnabled = false,
                                modifier = Modifier.height(324.dp)
                            ) {
                                items(weekDays.size - 1) { index ->
                                    val (day, mysteryIndex, mysteryName) = weekDays[index]
                                    ElevatedCard(
                                        onClick = { selectedMysteryIndex = mysteryIndex },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = day,
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            Text(
                                                text = mysteryName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                            }
                            
                            val (day, mysteryIndex, mysteryName) = weekDays.last()
                            ElevatedCard(
                                onClick = { selectedMysteryIndex = mysteryIndex },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = mysteryName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sección de misterios
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.all_mysteries),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val mysteries = listOf(
                            stringResource(id = R.string.joyful_mysteries_days) to Icons.Default.Favorite,
                            stringResource(id = R.string.sorrowful_mysteries_days) to Icons.Default.Warning,
                            stringResource(id = R.string.glorious_mysteries_days) to Icons.Default.Star,
                            stringResource(id = R.string.luminous_mysteries_days) to Icons.Default.LightMode
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            mysteries.forEachIndexed { index, (mysteryText, icon) ->
                                ElevatedCard(
                                    onClick = { selectedMysteryIndex = index },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                icon,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                            Text(
                                                text = mysteryText,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        Icon(
                                            Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // Barra superior con título y botón de retroceso
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { selectedMysteryIndex = null }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when(selectedMysteryIndex) {
                        0 -> stringResource(id = R.string.joyful_mysteries)
                        1 -> stringResource(id = R.string.sorrowful_mysteries)
                        2 -> stringResource(id = R.string.glorious_mysteries)
                        else -> stringResource(id = R.string.luminous_mysteries)
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            // Rosario interactivo
            InteractiveRosaryScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                dayOfWeek = currentDayOfWeek,
                mysteryIndex = selectedMysteryIndex ?: 0 // Valor por defecto si es null
            )
        }
    }
}

// Función auxiliar para obtener el ID del recurso por nombre
private fun getResourceIdByName(resourceName: String): Int {
    return when (resourceName) {
        "prayer_padre_nuestro" -> R.string.prayer_padre_nuestro
        "prayer_ave_maria" -> R.string.prayer_ave_maria
        "prayer_gloria" -> R.string.prayer_gloria
        "prayer_salve_regina" -> R.string.prayer_salve_regina
        "prayer_fatima" -> R.string.prayer_fatima
        "prayer_mysteries_joyful" -> R.string.prayer_mysteries_joyful
        "prayer_mysteries_sorrowful" -> R.string.prayer_mysteries_sorrowful
        "prayer_mysteries_glorious" -> R.string.prayer_mysteries_glorious
        "prayer_mysteries_luminous" -> R.string.prayer_mysteries_luminous
        "prayer_act_of_contrition" -> R.string.prayer_act_of_contrition
        "prayer_psalm_51" -> R.string.prayer_psalm_51
        "prayer_san_miguel" -> R.string.prayer_san_miguel
        "prayer_psalm_91" -> R.string.prayer_psalm_91
        "prayer_te_deum" -> R.string.prayer_te_deum
        "prayer_magnificat" -> R.string.prayer_magnificat
        "prayer_serenity" -> R.string.prayer_serenity
        "prayer_psalm_23" -> R.string.prayer_psalm_23
        "prayer_baptism" -> R.string.prayer_baptism
        "prayer_communion" -> R.string.prayer_communion
        "prayer_confession" -> R.string.prayer_confession
        "prayer_marriage" -> R.string.prayer_marriage
        "prayer_rosary" -> R.string.prayer_rosary
        "prayer_divine_mercy" -> R.string.prayer_divine_mercy
        "prayer_via_crucis" -> R.string.prayer_via_crucis
        "prayer_morning" -> R.string.prayer_morning
        "prayer_angelus" -> R.string.prayer_angelus
        "prayer_night" -> R.string.prayer_night
        "prayer_examination" -> R.string.prayer_examination
        else -> throw IllegalArgumentException("Recurso no encontrado: $resourceName")
    }
}