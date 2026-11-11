package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.i18n.DamaStrings
import com.example.model.AIDifficulty
import com.example.model.AppLanguage
import com.example.model.GameMode
import com.example.ui.BoardUiState
import com.example.ui.components.MoroccanPatternBackground
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.CedarWoodMedium
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MoroccanEmerald
import com.example.ui.theme.MoroccanEmeraldLight
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight
import com.example.ui.theme.MoroccanRed

@Composable
fun HomeScreen(
    state: BoardUiState,
    onStartGame: (GameMode, AIDifficulty) -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOnline: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit
) {
    val lang = state.language
    var selectedDifficulty by remember { mutableStateOf(state.aiDifficulty) }
    var languageMenuOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        MoroccanPatternBackground(alpha = 0.05f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Language & Profile Quick Access
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Selector
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CedarWoodDark.copy(alpha = 0.8f))
                            .border(1.dp, MoroccanGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { languageMenuOpen = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("language_selector"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = lang.flag, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = lang.displayName,
                            color = MoroccanGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    DropdownMenu(
                        expanded = languageMenuOpen,
                        onDismissRequest = { languageMenuOpen = false },
                        modifier = Modifier.background(CedarWoodDark)
                    ) {
                        AppLanguage.values().forEach { appLang ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${appLang.flag} ${appLang.displayName}",
                                        color = if (appLang == lang) MoroccanGoldLight else Color.White
                                    )
                                },
                                onClick = {
                                    onSelectLanguage(appLang)
                                    languageMenuOpen = false
                                }
                            )
                        }
                    }
                }

                // Settings & Profile icons
                Row {
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.testTag("home_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profil",
                            tint = MoroccanGoldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("home_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Paramètres",
                            tint = MoroccanGoldLight,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logo & Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(MoroccanEmeraldLight, MoroccanEmerald, CedarWoodDark)
                            )
                        )
                        .border(3.dp, MoroccanGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dama_maroc_icon),
                        contentDescription = "Logo DAMA MAROC",
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "DAMA MAROC 🇲🇦",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MoroccanGoldLight,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "الضامة المغربية الأصيلة",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoroccanEmeraldLight,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "Jeu de dames traditionnel marocain",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Quick Stats Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MoroccanGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatBadge(
                        title = if (lang == AppLanguage.ARABIC) "مباريات" else "Parties",
                        value = "${state.playerProfile.matchesPlayed}"
                    )
                    StatBadge(
                        title = if (lang == AppLanguage.ARABIC) "انتصارات" else "Victoires",
                        value = "${state.playerProfile.wins}",
                        highlightColor = MoroccanGoldLight
                    )
                    StatBadge(
                        title = if (lang == AppLanguage.ARABIC) "نسبة الفوز" else "Ratio",
                        value = "%.0f%%".format(state.playerProfile.winRate)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Difficulty Selector Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CedarWoodDark.copy(alpha = 0.7f))
                    .border(1.dp, MoroccanGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = if (lang == AppLanguage.ARABIC) "مستوى الذكاء الاصطناعي :" else "Niveau de difficulté de l'IA :",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MoroccanGoldLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AIDifficulty.values().forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MoroccanEmerald else Color.Black.copy(alpha = 0.4f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MoroccanGold else Color.White.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedDifficulty = diff }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = DamaStrings.difficultyName(lang, diff),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Game Mode Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Jouer contre l'IA
                HomeMenuButton(
                    title = DamaStrings.playVsAi(lang),
                    subtitle = if (lang == AppLanguage.ARABIC) "تحدَّ خوارزمية مينيماكس بالعمق التكتيكي" else "Défiez le moteur Minimax marocain",
                    icon = Icons.Default.Psychology,
                    gradient = listOf(MoroccanEmeraldLight, MoroccanEmerald),
                    tag = "play_vs_ai_button",
                    onClick = { onStartGame(GameMode.VS_AI, selectedDifficulty) }
                )

                // Button 2: Deux Joueurs sur le même appareil
                HomeMenuButton(
                    title = DamaStrings.twoPlayers(lang),
                    subtitle = if (lang == AppLanguage.ARABIC) "لعبة ودية بين شخصين وجهاً لوجه" else "Partie amicale en face-à-face",
                    icon = Icons.Default.Groups,
                    gradient = listOf(CedarWoodMedium, CedarWoodDark),
                    borderColor = MoroccanGold.copy(alpha = 0.6f),
                    tag = "play_two_players_button",
                    onClick = { onStartGame(GameMode.LOCAL_TWO_PLAYERS, selectedDifficulty) }
                )

                // Button 3: Multijoueur en Ligne (Lobby / Salons)
                HomeMenuButton(
                    title = DamaStrings.onlineMultiplayer(lang),
                    subtitle = if (lang == AppLanguage.ARABIC) "غرف اللعب ورمز الدعوة للأصدقاء" else "Salons privés & code d'invitation",
                    icon = Icons.Default.Public,
                    gradient = listOf(Color(0xFF0F3E50), Color(0xFF07212C)),
                    borderColor = Color(0xFF2EA090),
                    tag = "play_online_button",
                    onClick = onNavigateToOnline
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Buttons: Règles & Paramètres
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToRules,
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(MoroccanGold.copy(alpha = 0.5f), MoroccanGoldLight.copy(alpha = 0.5f)))
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("home_rules_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = MoroccanGoldLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = DamaStrings.rules(lang),
                        color = MoroccanGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToSettings,
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(MoroccanGold.copy(alpha = 0.5f), MoroccanGoldLight.copy(alpha = 0.5f)))
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("home_settings_nav_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MoroccanGoldLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = DamaStrings.settings(lang),
                        color = MoroccanGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun StatBadge(
    title: String,
    value: String,
    highlightColor: Color = Color.White
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = highlightColor
        )
    }
}

@Composable
private fun HomeMenuButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    borderColor: Color = MoroccanGold,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(8.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(gradient))
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MoroccanGoldLight,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MoroccanGoldLight,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
