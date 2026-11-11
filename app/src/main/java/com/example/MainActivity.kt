package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.model.AIDifficulty
import com.example.model.GameMode
import com.example.ui.DamaViewModel
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnlineLobbyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RulesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DamaMarocTheme

enum class Screen {
    HOME,
    GAME,
    RULES,
    SETTINGS,
    PROFILE,
    ONLINE
}

class MainActivity : ComponentActivity() {

    private val viewModel: DamaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DamaMarocTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DamaMarocApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun DamaMarocApp(viewModel: DamaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                state = uiState,
                onStartGame = { mode, diff ->
                    viewModel.startNewGame(mode = mode, difficulty = diff)
                    currentScreen = Screen.GAME
                },
                onNavigateToRules = { currentScreen = Screen.RULES },
                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                onNavigateToProfile = { currentScreen = Screen.PROFILE },
                onNavigateToOnline = { currentScreen = Screen.ONLINE },
                onSelectLanguage = { viewModel.setLanguage(it) }
            )
        }

        Screen.GAME -> {
            BackHandler {
                currentScreen = Screen.HOME
            }

            GameScreen(
                state = uiState,
                onSquareClicked = { viewModel.onSquareClicked(it) },
                onUndo = { viewModel.undoMove() },
                onHint = { viewModel.provideHint() },
                onPause = { viewModel.togglePause() },
                onRestart = { viewModel.startNewGame() },
                onSoundToggle = { viewModel.toggleSound() },
                onBackToHome = { currentScreen = Screen.HOME },
                onRulesClicked = { currentScreen = Screen.RULES }
            )
        }

        Screen.RULES -> {
            BackHandler {
                currentScreen = Screen.HOME
            }

            RulesScreen(
                language = uiState.language,
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.SETTINGS -> {
            BackHandler {
                currentScreen = Screen.HOME
            }

            SettingsScreen(
                state = uiState,
                onSelectLanguage = { viewModel.setLanguage(it) },
                onSelectTheme = { viewModel.setBoardTheme(it) },
                onToggleSound = { viewModel.toggleSound() },
                onToggleHaptic = { viewModel.toggleHaptic() },
                onUpdateRules = { viewModel.updateRules(it) },
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.PROFILE -> {
            BackHandler {
                currentScreen = Screen.HOME
            }

            ProfileScreen(
                profile = uiState.playerProfile,
                records = uiState.matchRecords,
                language = uiState.language,
                onUpdateName = { viewModel.updatePlayerName(it) },
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.ONLINE -> {
            BackHandler {
                currentScreen = Screen.HOME
            }

            OnlineLobbyScreen(
                state = uiState,
                onCreateRoom = { viewModel.createOnlineRoom() },
                onJoinRoom = { code ->
                    viewModel.joinOnlineRoom(code)
                    viewModel.startNewGame(mode = GameMode.ONLINE_MULTIPLAYER)
                    currentScreen = Screen.GAME
                },
                onBack = { currentScreen = Screen.HOME }
            )
        }
    }
}
