package com.example.model

enum class PlayerColor {
    LIGHT, // Usually White / Ivory / Moroccan Sand (Starts at bottom, moves upward: row - 1)
    DARK;  // Usually Black / Moroccan Red / Cedar (Starts at top, moves downward: row + 1)

    val opponent: PlayerColor
        get() = if (this == LIGHT) DARK else LIGHT
}

enum class PieceType {
    MAN,   // Pion ordinaire
    KING   // Dame / الضامة
}

data class Position(val row: Int, val col: Int) {
    fun isValid(boardSize: Int = 8): Boolean {
        return row in 0 until boardSize && col in 0 until boardSize
    }

    // Playable squares are dark squares where (row + col) % 2 != 0
    fun isPlayableSquare(): Boolean = (row + col) % 2 != 0
}

data class Piece(
    val id: Int,
    val player: PlayerColor,
    val type: PieceType = PieceType.MAN
)

data class Move(
    val from: Position,
    val to: Position,
    val capturedPositions: List<Position> = emptyList(),
    val intermediateSteps: List<Position> = emptyList(),
    val isPromotion: Boolean = false
) {
    val isCapture: Boolean
        get() = capturedPositions.isNotEmpty()

    val captureCount: Int
        get() = capturedPositions.size
}

data class GameRules(
    val boardSize: Int = 8, // 8x8 (standard Moroccan cafe draughts) or 10x10
    val mandatoryCapture: Boolean = true, // Prise obligatoire
    val majorityCapture: Boolean = true, // Prise majoritaire (must choose move with max captures)
    val pawnCanCaptureBackwards: Boolean = true, // Prise arrière par le pion ordinaire
    val flyingKing: Boolean = true, // Dame volante (peut parcourir toute la diagonale libre)
    val huffingRule: Boolean = false // Règle traditionnelle du souffler (avertit ou capture la pièce négligente)
)

enum class AIDifficulty(val searchDepth: Int) {
    EASY(1),
    MEDIUM(2),
    HARD(3),
    EXPERT(4)
}

enum class GameMode {
    VS_AI,
    LOCAL_TWO_PLAYERS,
    ONLINE_MULTIPLAYER
}

sealed class GameStatus {
    object Active : GameStatus()
    data class Victory(val winner: PlayerColor, val reason: VictoryReason) : GameStatus()
    data class Draw(val reason: String) : GameStatus()
}

enum class VictoryReason {
    ALL_PIECES_CAPTURED,
    NO_LEGAL_MOVES,
    RESIGNATION,
    TIMEOUT
}

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    ARABIC("ar", "العربية", "🇲🇦"),
    FRENCH("fr", "Français", "🇫🇷"),
    ENGLISH("en", "English", "🇬🇧")
}

enum class BoardTheme(val id: String) {
    WOOD("wood"),       // Bois de Thuya d'Essaouira
    ZELLIGE("zellige"), // Zellige Fassi émeraude
    MARBLE("marble"),   // Marbre noir et laiton doré
    DESERT("desert")    // Sable saharien et cuir rouge
}

data class GameHistoryEntry(
    val moveNumber: Int,
    val player: PlayerColor,
    val notation: String,
    val captureCount: Int,
    val isPromotion: Boolean
)

data class MatchRecord(
    val id: String,
    val timestamp: Long,
    val mode: GameMode,
    val difficulty: AIDifficulty?,
    val playerColor: PlayerColor,
    val isWin: Boolean,
    val isDraw: Boolean,
    val totalMoves: Int,
    val durationSeconds: Int,
    val capturesMade: Int,
    val queensPromoted: Int
)

data class PlayerProfile(
    val name: String = "Joueur Marocain",
    val matchesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCaptures: Int = 0,
    val totalQueens: Int = 0,
    val favoriteDifficulty: AIDifficulty = AIDifficulty.MEDIUM,
    val favoriteTheme: BoardTheme = BoardTheme.WOOD
) {
    val winRate: Float
        get() = if (matchesPlayed > 0) (wins.toFloat() / matchesPlayed) * 100f else 0f
}
