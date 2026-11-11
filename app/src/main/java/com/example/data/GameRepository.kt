package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AIDifficulty
import com.example.model.AppLanguage
import com.example.model.BoardTheme
import com.example.model.GameMode
import com.example.model.GameRules
import com.example.model.MatchRecord
import com.example.model.PlayerColor
import com.example.model.PlayerProfile
import org.json.JSONArray
import org.json.JSONObject

class GameRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("dama_maroc_prefs", Context.MODE_PRIVATE)

    // --- Profile & Statistics ---

    fun getPlayerProfile(): PlayerProfile {
        return PlayerProfile(
            name = prefs.getString("profile_name", "Joueur Marocain") ?: "Joueur Marocain",
            matchesPlayed = prefs.getInt("profile_matches", 0),
            wins = prefs.getInt("profile_wins", 0),
            losses = prefs.getInt("profile_losses", 0),
            draws = prefs.getInt("profile_draws", 0),
            currentStreak = prefs.getInt("profile_streak", 0),
            bestStreak = prefs.getInt("profile_best_streak", 0),
            totalCaptures = prefs.getInt("profile_captures", 0),
            totalQueens = prefs.getInt("profile_queens", 0),
            favoriteDifficulty = try {
                AIDifficulty.valueOf(prefs.getString("profile_fav_diff", AIDifficulty.MEDIUM.name) ?: AIDifficulty.MEDIUM.name)
            } catch (_: Exception) {
                AIDifficulty.MEDIUM
            },
            favoriteTheme = try {
                BoardTheme.valueOf(prefs.getString("profile_fav_theme", BoardTheme.WOOD.name) ?: BoardTheme.WOOD.name)
            } catch (_: Exception) {
                BoardTheme.WOOD
            }
        )
    }

    fun savePlayerProfile(profile: PlayerProfile) {
        prefs.edit()
            .putString("profile_name", profile.name)
            .putInt("profile_matches", profile.matchesPlayed)
            .putInt("profile_wins", profile.wins)
            .putInt("profile_losses", profile.losses)
            .putInt("profile_draws", profile.draws)
            .putInt("profile_streak", profile.currentStreak)
            .putInt("profile_best_streak", profile.bestStreak)
            .putInt("profile_captures", profile.totalCaptures)
            .putInt("profile_queens", profile.totalQueens)
            .putString("profile_fav_diff", profile.favoriteDifficulty.name)
            .putString("profile_fav_theme", profile.favoriteTheme.name)
            .apply()
    }

    fun recordMatchOutcome(
        mode: GameMode,
        difficulty: AIDifficulty?,
        playerColor: PlayerColor,
        isWin: Boolean,
        isDraw: Boolean,
        moves: Int,
        durationSeconds: Int,
        captures: Int,
        queens: Int
    ) {
        val currentProfile = getPlayerProfile()
        val newMatches = currentProfile.matchesPlayed + 1
        val newWins = currentProfile.wins + if (isWin) 1 else 0
        val newLosses = currentProfile.losses + if (!isWin && !isDraw) 1 else 0
        val newDraws = currentProfile.draws + if (isDraw) 1 else 0

        val newStreak = if (isWin) currentProfile.currentStreak + 1 else 0
        val bestStreak = maxOf(currentProfile.bestStreak, newStreak)

        val updatedProfile = currentProfile.copy(
            matchesPlayed = newMatches,
            wins = newWins,
            losses = newLosses,
            draws = newDraws,
            currentStreak = newStreak,
            bestStreak = bestStreak,
            totalCaptures = currentProfile.totalCaptures + captures,
            totalQueens = currentProfile.totalQueens + queens
        )
        savePlayerProfile(updatedProfile)

        // Save match history record
        val record = MatchRecord(
            id = System.currentTimeMillis().toString(),
            timestamp = System.currentTimeMillis(),
            mode = mode,
            difficulty = difficulty,
            playerColor = playerColor,
            isWin = isWin,
            isDraw = isDraw,
            totalMoves = moves,
            durationSeconds = durationSeconds,
            capturesMade = captures,
            queensPromoted = queens
        )
        addMatchRecord(record)
    }

    fun getMatchRecords(): List<MatchRecord> {
        val jsonStr = prefs.getString("match_records", "[]") ?: "[]"
        val list = mutableListOf<MatchRecord>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    MatchRecord(
                        id = obj.getString("id"),
                        timestamp = obj.getLong("timestamp"),
                        mode = GameMode.valueOf(obj.getString("mode")),
                        difficulty = if (obj.has("diff") && !obj.isNull("diff")) AIDifficulty.valueOf(obj.getString("diff")) else null,
                        playerColor = PlayerColor.valueOf(obj.getString("color")),
                        isWin = obj.getBoolean("win"),
                        isDraw = obj.getBoolean("draw"),
                        totalMoves = obj.getInt("moves"),
                        durationSeconds = obj.getInt("duration"),
                        capturesMade = obj.getInt("captures"),
                        queensPromoted = obj.getInt("queens")
                    )
                )
            }
        } catch (_: Exception) {
            // Ignore parse errors
        }
        return list
    }

    private fun addMatchRecord(record: MatchRecord) {
        val records = getMatchRecords().toMutableList()
        records.add(0, record)
        if (records.size > 50) records.removeLast()

        val jsonArray = JSONArray()
        for (r in records) {
            val obj = JSONObject().apply {
                put("id", r.id)
                put("timestamp", r.timestamp)
                put("mode", r.mode.name)
                put("diff", r.difficulty?.name)
                put("color", r.playerColor.name)
                put("win", r.isWin)
                put("draw", r.isDraw)
                put("moves", r.totalMoves)
                put("duration", r.durationSeconds)
                put("captures", r.capturesMade)
                put("queens", r.queensPromoted)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("match_records", jsonArray.toString()).apply()
    }

    // --- Settings & Rules ---

    fun getGameRules(): GameRules {
        return GameRules(
            boardSize = prefs.getInt("rules_board_size", 8),
            mandatoryCapture = prefs.getBoolean("rules_mandatory_capture", true),
            majorityCapture = prefs.getBoolean("rules_majority_capture", true),
            pawnCanCaptureBackwards = prefs.getBoolean("rules_pawn_backwards", true),
            flyingKing = prefs.getBoolean("rules_flying_king", true),
            huffingRule = prefs.getBoolean("rules_huffing", false)
        )
    }

    fun saveGameRules(rules: GameRules) {
        prefs.edit()
            .putInt("rules_board_size", rules.boardSize)
            .putBoolean("rules_mandatory_capture", rules.mandatoryCapture)
            .putBoolean("rules_majority_capture", rules.majorityCapture)
            .putBoolean("rules_pawn_backwards", rules.pawnCanCaptureBackwards)
            .putBoolean("rules_flying_king", rules.flyingKing)
            .putBoolean("rules_huffing", rules.huffingRule)
            .apply()
    }

    fun getLanguage(): AppLanguage {
        val langStr = prefs.getString("app_language", AppLanguage.FRENCH.name) ?: AppLanguage.FRENCH.name
        return try {
            AppLanguage.valueOf(langStr)
        } catch (_: Exception) {
            AppLanguage.FRENCH
        }
    }

    fun saveLanguage(lang: AppLanguage) {
        prefs.edit().putString("app_language", lang.name).apply()
    }

    fun getTheme(): BoardTheme {
        val themeStr = prefs.getString("board_theme", BoardTheme.WOOD.name) ?: BoardTheme.WOOD.name
        return try {
            BoardTheme.valueOf(themeStr)
        } catch (_: Exception) {
            BoardTheme.WOOD
        }
    }

    fun saveTheme(theme: BoardTheme) {
        prefs.edit().putString("board_theme", theme.name).apply()
    }

    fun isSoundEnabled(): Boolean = prefs.getBoolean("sound_enabled", true)
    fun setSoundEnabled(enabled: Boolean) = prefs.edit().putBoolean("sound_enabled", enabled).apply()

    fun isHapticEnabled(): Boolean = prefs.getBoolean("haptic_enabled", true)
    fun setHapticEnabled(enabled: Boolean) = prefs.edit().putBoolean("haptic_enabled", enabled).apply()

    // --- Saved Active Game State (for game resumption) ---

    fun hasSavedGame(): Boolean = prefs.getBoolean("has_saved_game", false)

    fun clearSavedGame() {
        prefs.edit().putBoolean("has_saved_game", false).remove("saved_game_json").apply()
    }

    fun saveActiveGame(jsonString: String) {
        prefs.edit()
            .putBoolean("has_saved_game", true)
            .putString("saved_game_json", jsonString)
            .apply()
    }

    fun getSavedGameJson(): String? {
        return if (hasSavedGame()) prefs.getString("saved_game_json", null) else null
    }
}
