package com.example.i18n

import com.example.model.AIDifficulty
import com.example.model.AppLanguage
import com.example.model.GameMode
import com.example.model.PlayerColor
import com.example.model.VictoryReason

object DamaStrings {

    fun appTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الضامة المغربية"
        AppLanguage.FRENCH -> "Dama Maroc"
        AppLanguage.ENGLISH -> "Dama Maroc"
    }

    fun play(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "العب الآن"
        AppLanguage.FRENCH -> "Jouer"
        AppLanguage.ENGLISH -> "Play Now"
    }

    fun playVsAi(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "ضد الذكاء الاصطناعي"
        AppLanguage.FRENCH -> "Joueur contre IA"
        AppLanguage.ENGLISH -> "Player vs AI"
    }

    fun twoPlayers(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "لاعبان على نفس الجهاز"
        AppLanguage.FRENCH -> "Deux Joueurs (Local)"
        AppLanguage.ENGLISH -> "Two Players (Local)"
    }

    fun onlineMultiplayer(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "اللعب عبر الإنترنت"
        AppLanguage.FRENCH -> "Multijoueur en Ligne"
        AppLanguage.ENGLISH -> "Online Multiplayer"
    }

    fun rules(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "قواعد الضامة"
        AppLanguage.FRENCH -> "Règles du Jeu"
        AppLanguage.ENGLISH -> "Game Rules"
    }

    fun settings(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الإعدادات"
        AppLanguage.FRENCH -> "Paramètres"
        AppLanguage.ENGLISH -> "Settings"
    }

    fun profile(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الملف الشخصي"
        AppLanguage.FRENCH -> "Profil & Stats"
        AppLanguage.ENGLISH -> "Profile & Stats"
    }

    fun newGame(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "لعبة جديدة"
        AppLanguage.FRENCH -> "Nouvelle partie"
        AppLanguage.ENGLISH -> "New Game"
    }

    fun undo(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "تراجع"
        AppLanguage.FRENCH -> "Annuler"
        AppLanguage.ENGLISH -> "Undo"
    }

    fun hint(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "تلميح"
        AppLanguage.FRENCH -> "Conseil"
        AppLanguage.ENGLISH -> "Hint"
    }

    fun pause(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "إيقاف مؤقت"
        AppLanguage.FRENCH -> "Pause"
        AppLanguage.ENGLISH -> "Pause"
    }

    fun resume(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "استئناف"
        AppLanguage.FRENCH -> "Reprendre"
        AppLanguage.ENGLISH -> "Resume"
    }

    fun quit(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "خروج"
        AppLanguage.FRENCH -> "Quitter"
        AppLanguage.ENGLISH -> "Quit"
    }

    fun turnOf(lang: AppLanguage, player: PlayerColor): String = when (lang) {
        AppLanguage.ARABIC -> if (player == PlayerColor.LIGHT) "دور الأبيض / العاجي" else "دور الأسود / الأحمر"
        AppLanguage.FRENCH -> if (player == PlayerColor.LIGHT) "Tour des Blancs" else "Tour des Noirs"
        AppLanguage.ENGLISH -> if (player == PlayerColor.LIGHT) "White's Turn" else "Black's Turn"
    }

    fun aiThinking(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الذكاء الاصطناعي يفكر..."
        AppLanguage.FRENCH -> "L'IA réfléchit..."
        AppLanguage.ENGLISH -> "AI is thinking..."
    }

    fun mandatoryCaptureAlert(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الأكل إجباري في الضامة المغربية!"
        AppLanguage.FRENCH -> "La prise est obligatoire !"
        AppLanguage.ENGLISH -> "Capture is mandatory!"
    }

    fun majorityCaptureAlert(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "قاعدة الأكل الأكثر (Prise majoritaire): يجب أكل أكبر عدد ممكن من القطع!"
        AppLanguage.FRENCH -> "Prise majoritaire : vous devez choisir la prise maximale !"
        AppLanguage.ENGLISH -> "Majority capture: must take max pieces!"
    }

    fun victory(lang: AppLanguage, winner: PlayerColor): String = when (lang) {
        AppLanguage.ARABIC -> if (winner == PlayerColor.LIGHT) "مبروك! فوز الأبيض!" else "مبروك! فوز الأسود!"
        AppLanguage.FRENCH -> if (winner == PlayerColor.LIGHT) "Victoire des Blancs !" else "Victoire des Noirs !"
        AppLanguage.ENGLISH -> if (winner == PlayerColor.LIGHT) "White Victory!" else "Black Victory!"
    }

    fun defeat(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "هزيمة! حظ أوفر في المرة القادمة"
        AppLanguage.FRENCH -> "Défaite ! Bonne chance pour la prochaine"
        AppLanguage.ENGLISH -> "Defeat! Better luck next time"
    }

    fun draw(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "تعادل !"
        AppLanguage.FRENCH -> "Match nul !"
        AppLanguage.ENGLISH -> "Draw!"
    }

    fun playAgain(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "إعادة اللعب"
        AppLanguage.FRENCH -> "Rejouer"
        AppLanguage.ENGLISH -> "Play Again"
    }

    fun movesCount(lang: AppLanguage, count: Int): String = when (lang) {
        AppLanguage.ARABIC -> "$count نقلة"
        AppLanguage.FRENCH -> "$count coups"
        AppLanguage.ENGLISH -> "$count moves"
    }

    fun timeElapsed(lang: AppLanguage, seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        val timeStr = "%02d:%02d".format(m, s)
        return when (lang) {
            AppLanguage.ARABIC -> "الوقت: $timeStr"
            AppLanguage.FRENCH -> "Temps : $timeStr"
            AppLanguage.ENGLISH -> "Time: $timeStr"
        }
    }

    fun difficultyName(lang: AppLanguage, diff: AIDifficulty): String = when (diff) {
        AIDifficulty.EASY -> when (lang) {
            AppLanguage.ARABIC -> "مبتدئ"
            AppLanguage.FRENCH -> "Débutant"
            AppLanguage.ENGLISH -> "Beginner"
        }
        AIDifficulty.MEDIUM -> when (lang) {
            AppLanguage.ARABIC -> "متوسط"
            AppLanguage.FRENCH -> "Moyen"
            AppLanguage.ENGLISH -> "Medium"
        }
        AIDifficulty.HARD -> when (lang) {
            AppLanguage.ARABIC -> "متقدم"
            AppLanguage.FRENCH -> "Avancé"
            AppLanguage.ENGLISH -> "Advanced"
        }
        AIDifficulty.EXPERT -> when (lang) {
            AppLanguage.ARABIC -> "خبير (محترف)"
            AppLanguage.FRENCH -> "Expert"
            AppLanguage.ENGLISH -> "Expert"
        }
    }

    fun themeName(lang: AppLanguage, themeId: String): String = when (themeId) {
        "wood" -> when (lang) {
            AppLanguage.ARABIC -> "خشب الثويا الصويري"
            AppLanguage.FRENCH -> "Bois de Thuya d'Essaouira"
            AppLanguage.ENGLISH -> "Essaouira Thuya Wood"
        }
        "zellige" -> when (lang) {
            AppLanguage.ARABIC -> "زليج فاس الزمردي"
            AppLanguage.FRENCH -> "Zellige Fassi Émeraude"
            AppLanguage.ENGLISH -> "Fassi Emerald Zellige"
        }
        "marble" -> when (lang) {
            AppLanguage.ARABIC -> "رخام أسود ونحاس"
            AppLanguage.FRENCH -> "Marbre Noir & Laiton"
            AppLanguage.ENGLISH -> "Black Marble & Brass"
        }
        "desert" -> when (lang) {
            AppLanguage.ARABIC -> "رمل الصحراء وجلد تقليدي"
            AppLanguage.FRENCH -> "Sable Saharien & Cuir"
            AppLanguage.ENGLISH -> "Sahara Sand & Leather"
        }
        else -> themeId
    }
}
