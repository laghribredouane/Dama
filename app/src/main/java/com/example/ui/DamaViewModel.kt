package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.DamaAI
import com.example.audio.DamaSoundManager
import com.example.data.GameRepository
import com.example.engine.DamaEngine
import com.example.model.AIDifficulty
import com.example.model.AppLanguage
import com.example.model.Board
import com.example.model.BoardTheme
import com.example.model.GameHistoryEntry
import com.example.model.GameMode
import com.example.model.GameRules
import com.example.model.GameStatus
import com.example.model.MatchRecord
import com.example.model.Move
import com.example.model.Piece
import com.example.model.PlayerColor
import com.example.model.PlayerProfile
import com.example.model.Position
import com.example.model.VictoryReason
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

data class BoardUiState(
    val board: Board = Board.initial(8),
    val activePlayer: PlayerColor = PlayerColor.LIGHT,
    val selectedPosition: Position? = null,
    val validMovesForSelected: List<Move> = emptyList(),
    val allLegalMoves: List<Move> = emptyList(),
    val gameStatus: GameStatus = GameStatus.Active,
    val gameMode: GameMode = GameMode.VS_AI,
    val aiDifficulty: AIDifficulty = AIDifficulty.MEDIUM,
    val isAiThinking: Boolean = false,
    val gameRules: GameRules = GameRules(),
    val history: List<GameHistoryEntry> = emptyList(),
    val boardHistory: List<Board> = emptyList(),
    val elapsedSeconds: Int = 0,
    val isPaused: Boolean = false,
    val alertMessage: String? = null,
    val lightCaptures: Int = 0,
    val darkCaptures: Int = 0,
    val lightQueens: Int = 0,
    val darkQueens: Int = 0,
    val boardTheme: BoardTheme = BoardTheme.WOOD,
    val language: AppLanguage = AppLanguage.FRENCH,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val playerProfile: PlayerProfile = PlayerProfile(),
    val matchRecords: List<MatchRecord> = emptyList(),
    // Online Multiplayer Lobby State
    val onlineRoomCode: String = "",
    val isOnlineConnected: Boolean = false,
    val onlineStatusText: String = "",
    val isHost: Boolean = true
)

class DamaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)
    val soundManager = DamaSoundManager(application)
    private var engine = DamaEngine(repository.getGameRules())
    private var ai = DamaAI(engine)

    private val _uiState = MutableStateFlow(BoardUiState())
    val uiState: StateFlow<BoardUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var aiJob: Job? = null

    init {
        val rules = repository.getGameRules()
        val lang = repository.getLanguage()
        val theme = repository.getTheme()
        val sound = repository.isSoundEnabled()
        val haptic = repository.isHapticEnabled()
        val profile = repository.getPlayerProfile()
        val records = repository.getMatchRecords()

        soundManager.soundEnabled = sound
        soundManager.hapticEnabled = haptic
        engine = DamaEngine(rules)
        ai = DamaAI(engine)

        val initialBoard = Board.initial(rules.boardSize)
        val initialLegalMoves = engine.getLegalMoves(initialBoard, PlayerColor.LIGHT)

        _uiState.update {
            it.copy(
                board = initialBoard,
                gameRules = rules,
                language = lang,
                boardTheme = theme,
                soundEnabled = sound,
                hapticEnabled = haptic,
                playerProfile = profile,
                matchRecords = records,
                allLegalMoves = initialLegalMoves
            )
        }

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_uiState.value.isPaused && _uiState.value.gameStatus == GameStatus.Active) {
                    _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    // --- Gameplay Interactions ---

    fun onSquareClicked(pos: Position) {
        val state = _uiState.value
        if (state.isPaused || state.gameStatus !is GameStatus.Active || state.isAiThinking) return

        // In VS_AI mode, ignore user clicks during AI turn
        if (state.gameMode == GameMode.VS_AI && state.activePlayer == PlayerColor.DARK) return

        val pieceAtSquare = state.board[pos]

        // 1. If clicking on own piece -> Select it
        if (pieceAtSquare != null && pieceAtSquare.player == state.activePlayer) {
            val movesForPiece = state.allLegalMoves.filter { it.from == pos }
            if (movesForPiece.isEmpty()) {
                // Check if other pieces have mandatory capture
                val hasCapturesElsewhere = state.allLegalMoves.any { it.isCapture }
                if (hasCapturesElsewhere) {
                    val alert = if (state.language == AppLanguage.ARABIC) {
                        "الأكل إجباري! يجب عليك أكل قطع الخصم"
                    } else if (state.language == AppLanguage.ENGLISH) {
                        "Capture is mandatory! You must take opponent piece"
                    } else {
                        "Prise obligatoire ! Vous devez effectuer une capture"
                    }
                    _uiState.update { it.copy(alertMessage = alert) }
                }
                return
            }

            soundManager.playMove()
            _uiState.update {
                it.copy(
                    selectedPosition = pos,
                    validMovesForSelected = movesForPiece,
                    alertMessage = null
                )
            }
            return
        }

        // 2. If clicking on a valid destination for the selected piece -> Execute Move
        val selectedPos = state.selectedPosition
        if (selectedPos != null) {
            val matchingMove = state.validMovesForSelected.firstOrNull { it.to == pos }
            if (matchingMove != null) {
                executeMove(matchingMove)
                return
            }
        }

        // 3. Otherwise deselect
        _uiState.update { it.copy(selectedPosition = null, validMovesForSelected = emptyList()) }
    }

    fun executeMove(move: Move) {
        val state = _uiState.value
        val previousBoard = state.board
        val newBoard = previousBoard.applyMove(move)

        // Sound & haptic triggers
        if (move.isPromotion) {
            soundManager.playPromotion()
        } else if (move.isCapture) {
            soundManager.playCapture()
        } else {
            soundManager.playMove()
        }

        val capturedCount = move.captureCount
        val newLightCaptures = state.lightCaptures + if (state.activePlayer == PlayerColor.LIGHT) capturedCount else 0
        val newDarkCaptures = state.darkCaptures + if (state.activePlayer == PlayerColor.DARK) capturedCount else 0
        val newLightQueens = state.lightQueens + if (state.activePlayer == PlayerColor.LIGHT && move.isPromotion) 1 else 0
        val newDarkQueens = state.darkQueens + if (state.activePlayer == PlayerColor.DARK && move.isPromotion) 1 else 0

        val notation = engine.formatMoveNotation(move, state.gameRules.boardSize)
        val historyEntry = GameHistoryEntry(
            moveNumber = state.history.size + 1,
            player = state.activePlayer,
            notation = notation,
            captureCount = capturedCount,
            isPromotion = move.isPromotion
        )

        val nextPlayer = state.activePlayer.opponent
        val status = engine.evaluateGameStatus(newBoard, nextPlayer)
        val nextLegalMoves = if (status == GameStatus.Active) engine.getLegalMoves(newBoard, nextPlayer) else emptyList()

        _uiState.update {
            it.copy(
                board = newBoard,
                boardHistory = it.boardHistory + previousBoard,
                history = it.history + historyEntry,
                activePlayer = nextPlayer,
                selectedPosition = null,
                validMovesForSelected = emptyList(),
                allLegalMoves = nextLegalMoves,
                gameStatus = status,
                lightCaptures = newLightCaptures,
                darkCaptures = newDarkCaptures,
                lightQueens = newLightQueens,
                darkQueens = newDarkQueens,
                alertMessage = null
            )
        }

        // Handle Game Over
        if (status !is GameStatus.Active) {
            handleGameOver(status)
            return
        }

        // Trigger AI if it's AI's turn
        if (state.gameMode == GameMode.VS_AI && nextPlayer == PlayerColor.DARK) {
            triggerAiTurn()
        }
    }

    private fun triggerAiTurn() {
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            _uiState.update { it.copy(isAiThinking = true) }
            // Natural thinking delay (550ms)
            delay(550)

            val currentState = _uiState.value
            if (currentState.gameStatus !is GameStatus.Active) {
                _uiState.update { it.copy(isAiThinking = false) }
                return@launch
            }

            val bestMove = ai.findBestMove(
                board = currentState.board,
                aiColor = PlayerColor.DARK,
                difficulty = currentState.aiDifficulty
            )

            _uiState.update { it.copy(isAiThinking = false) }
            if (bestMove != null) {
                executeMove(bestMove)
            }
        }
    }

    private fun handleGameOver(status: GameStatus) {
        val state = _uiState.value
        val isWin = status is GameStatus.Victory && status.winner == PlayerColor.LIGHT
        val isDraw = status is GameStatus.Draw

        if (isWin) {
            soundManager.playVictory()
        } else if (status is GameStatus.Victory) {
            soundManager.playDefeat()
        }

        // Save outcome
        repository.recordMatchOutcome(
            mode = state.gameMode,
            difficulty = if (state.gameMode == GameMode.VS_AI) state.aiDifficulty else null,
            playerColor = PlayerColor.LIGHT,
            isWin = isWin,
            isDraw = isDraw,
            moves = state.history.size,
            durationSeconds = state.elapsedSeconds,
            captures = state.lightCaptures,
            queens = state.lightQueens
        )

        // Reload updated profile & records
        _uiState.update {
            it.copy(
                playerProfile = repository.getPlayerProfile(),
                matchRecords = repository.getMatchRecords()
            )
        }
    }

    // --- Controls & Actions ---

    fun undoMove() {
        val state = _uiState.value
        if (state.isAiThinking) return
        if (state.boardHistory.isEmpty()) return

        // In VS_AI, undo 2 plies (both AI's move and player's move) if available
        val stepsToUndo = if (state.gameMode == GameMode.VS_AI && state.boardHistory.size >= 2) 2 else 1

        val targetBoardIndex = state.boardHistory.size - stepsToUndo
        val targetBoard = state.boardHistory[targetBoardIndex]
        val newBoardHistory = state.boardHistory.take(targetBoardIndex)
        val newHistory = state.history.dropLast(stepsToUndo)

        val restoredPlayer = if (stepsToUndo == 2) state.activePlayer else state.activePlayer.opponent
        val restoredLegalMoves = engine.getLegalMoves(targetBoard, restoredPlayer)

        _uiState.update {
            it.copy(
                board = targetBoard,
                boardHistory = newBoardHistory,
                history = newHistory,
                activePlayer = restoredPlayer,
                selectedPosition = null,
                validMovesForSelected = emptyList(),
                allLegalMoves = restoredLegalMoves,
                gameStatus = GameStatus.Active,
                alertMessage = null
            )
        }
        soundManager.playMove()
    }

    fun startNewGame(
        mode: GameMode = _uiState.value.gameMode,
        difficulty: AIDifficulty = _uiState.value.aiDifficulty
    ) {
        aiJob?.cancel()
        val rules = _uiState.value.gameRules
        val newBoard = Board.initial(rules.boardSize)
        val initialLegalMoves = engine.getLegalMoves(newBoard, PlayerColor.LIGHT)

        _uiState.update {
            it.copy(
                board = newBoard,
                activePlayer = PlayerColor.LIGHT,
                selectedPosition = null,
                validMovesForSelected = emptyList(),
                allLegalMoves = initialLegalMoves,
                gameStatus = GameStatus.Active,
                gameMode = mode,
                aiDifficulty = difficulty,
                isAiThinking = false,
                history = emptyList(),
                boardHistory = emptyList(),
                elapsedSeconds = 0,
                isPaused = false,
                alertMessage = null,
                lightCaptures = 0,
                darkCaptures = 0,
                lightQueens = 0,
                darkQueens = 0
            )
        }
    }

    fun togglePause() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun provideHint() {
        val state = _uiState.value
        if (state.gameStatus !is GameStatus.Active || state.isAiThinking) return

        val bestMove = ai.findBestMove(
            board = state.board,
            aiColor = state.activePlayer,
            difficulty = AIDifficulty.EXPERT
        )
        if (bestMove != null) {
            val movesForPiece = state.allLegalMoves.filter { it.from == bestMove.from }
            _uiState.update {
                it.copy(
                    selectedPosition = bestMove.from,
                    validMovesForSelected = movesForPiece,
                    alertMessage = if (state.language == AppLanguage.ARABIC) "اقتراح: تحريك القطعة المحددة" else "Conseil : Jouez ce pion"
                )
            }
        }
    }

    // --- Settings & Configuration ---

    fun setLanguage(lang: AppLanguage) {
        repository.saveLanguage(lang)
        _uiState.update { it.copy(language = lang) }
    }

    fun setBoardTheme(theme: BoardTheme) {
        repository.saveTheme(theme)
        _uiState.update { it.copy(boardTheme = theme) }
    }

    fun toggleSound() {
        val newSound = !_uiState.value.soundEnabled
        repository.setSoundEnabled(newSound)
        soundManager.soundEnabled = newSound
        _uiState.update { it.copy(soundEnabled = newSound) }
    }

    fun toggleHaptic() {
        val newHaptic = !_uiState.value.hapticEnabled
        repository.setHapticEnabled(newHaptic)
        soundManager.hapticEnabled = newHaptic
        _uiState.update { it.copy(hapticEnabled = newHaptic) }
    }

    fun updateRules(rules: GameRules) {
        repository.saveGameRules(rules)
        engine = DamaEngine(rules)
        ai = DamaAI(engine)
        _uiState.update { it.copy(gameRules = rules) }
        startNewGame(mode = _uiState.value.gameMode, difficulty = _uiState.value.aiDifficulty)
    }

    fun updatePlayerName(newName: String) {
        val updatedProfile = _uiState.value.playerProfile.copy(name = newName)
        repository.savePlayerProfile(updatedProfile)
        _uiState.update { it.copy(playerProfile = updatedProfile) }
    }

    // --- Online Multiplayer Room Simulation ---

    fun createOnlineRoom() {
        val randomCode = "DAMA" + (1000..9999).random().toString()
        _uiState.update {
            it.copy(
                onlineRoomCode = randomCode,
                isOnlineConnected = false,
                isHost = true,
                onlineStatusText = if (it.language == AppLanguage.ARABIC) "في انتظار انضمام الخصم..." else "En attente d'un adversaire..."
            )
        }
    }

    fun joinOnlineRoom(code: String) {
        if (code.isBlank()) return
        _uiState.update {
            it.copy(
                onlineRoomCode = code.uppercase(),
                isOnlineConnected = true,
                isHost = false,
                onlineStatusText = if (it.language == AppLanguage.ARABIC) "تم الاتصال بالصالة $code !" else "Connecté au salon $code !"
            )
        }
    }
}
