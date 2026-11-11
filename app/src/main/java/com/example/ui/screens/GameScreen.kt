package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.DamaStrings
import com.example.model.AppLanguage
import com.example.model.GameMode
import com.example.model.PlayerColor
import com.example.ui.BoardUiState
import com.example.ui.components.DamaBoardView
import com.example.ui.components.GameControls
import com.example.ui.components.MoroccanPatternBackground
import com.example.ui.components.PlayerBanner
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight
import com.example.ui.theme.MoroccanRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    state: BoardUiState,
    onSquareClicked: (com.example.model.Position) -> Unit,
    onUndo: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit,
    onRestart: () -> Unit,
    onSoundToggle: () -> Unit,
    onBackToHome: () -> Unit,
    onRulesClicked: () -> Unit
) {
    val lang = state.language
    val topPlayerName = if (state.gameMode == GameMode.VS_AI) {
        "IA (${DamaStrings.difficultyName(lang, state.aiDifficulty)})"
    } else {
        if (lang == AppLanguage.ARABIC) "اللاعب 2 (الأسود)" else "Joueur 2 (Noirs)"
    }
    val bottomPlayerName = if (lang == AppLanguage.ARABIC) "اللاعب 1 (الأبيض)" else "Joueur 1 (Blancs)"

    val m = state.elapsedSeconds / 60
    val s = state.elapsedSeconds % 60
    val timerString = "%02d:%02d".format(m, s)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = DamaStrings.appTitle(lang),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoroccanGoldLight
                        )
                        Text(
                            text = when (state.gameMode) {
                                GameMode.VS_AI -> DamaStrings.playVsAi(lang)
                                GameMode.LOCAL_TWO_PLAYERS -> DamaStrings.twoPlayers(lang)
                                GameMode.ONLINE_MULTIPLAYER -> DamaStrings.onlineMultiplayer(lang)
                            },
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToHome,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MoroccanGoldLight
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onRulesClicked,
                        modifier = Modifier.testTag("game_rules_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Règles",
                            tint = MoroccanGoldLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CedarWoodDark,
                    titleContentColor = MoroccanGoldLight
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MoroccanPatternBackground(alpha = 0.04f)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Opponent Banner (Top)
                PlayerBanner(
                    playerColor = PlayerColor.DARK,
                    playerName = topPlayerName,
                    isActive = state.activePlayer == PlayerColor.DARK,
                    pieceCount = state.board.countPieces(PlayerColor.DARK),
                    capturedCount = state.darkCaptures,
                    queensCount = state.darkQueens,
                    isAi = state.gameMode == GameMode.VS_AI,
                    isAiThinking = state.isAiThinking
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Alert notification banner (e.g. mandatory capture warning)
                AnimatedVisibility(
                    visible = state.alertMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    state.alertMessage?.let { msg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MoroccanRed.copy(alpha = 0.25f))
                                .border(1.dp, MoroccanRed, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MoroccanRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = msg,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Checkers Board
                DamaBoardView(
                    board = state.board,
                    selectedPosition = state.selectedPosition,
                    validMoves = state.validMovesForSelected,
                    theme = state.boardTheme,
                    onSquareClicked = onSquareClicked,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Player Banner (Bottom)
                PlayerBanner(
                    playerColor = PlayerColor.LIGHT,
                    playerName = bottomPlayerName,
                    isActive = state.activePlayer == PlayerColor.LIGHT,
                    pieceCount = state.board.countPieces(PlayerColor.LIGHT),
                    capturedCount = state.lightCaptures,
                    queensCount = state.lightQueens,
                    isAi = false,
                    isAiThinking = false,
                    timerText = timerString
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Controls Bar
                GameControls(
                    canUndo = state.boardHistory.isNotEmpty() && !state.isAiThinking,
                    isPaused = state.isPaused,
                    soundEnabled = state.soundEnabled,
                    onUndoClicked = onUndo,
                    onHintClicked = onHint,
                    onPauseClicked = onPause,
                    onRestartClicked = onRestart,
                    onSoundToggle = onSoundToggle
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Game Over Dialog overlay
            GameOverDialog(
                status = state.gameStatus,
                totalMoves = state.history.size,
                durationSeconds = state.elapsedSeconds,
                language = lang,
                onPlayAgain = { onRestart() },
                onHome = { onBackToHome() }
            )
        }
    }
}
