package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.DamaStrings
import com.example.model.AppLanguage
import com.example.model.BoardTheme
import com.example.model.GameRules
import com.example.ui.BoardUiState
import com.example.ui.components.MoroccanPatternBackground
import com.example.ui.theme.BoardClassicDark
import com.example.ui.theme.BoardMarbleDark
import com.example.ui.theme.BoardWoodDark
import com.example.ui.theme.BoardZelligeDark
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MoroccanEmerald
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: BoardUiState,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSelectTheme: (BoardTheme) -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptic: () -> Unit,
    onUpdateRules: (GameRules) -> Unit,
    onBack: () -> Unit
) {
    val lang = state.language
    val rules = state.gameRules

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = DamaStrings.settings(lang),
                        fontWeight = FontWeight.Bold,
                        color = MoroccanGoldLight
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MoroccanPatternBackground(alpha = 0.04f)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Language Section
                SettingsSectionHeader(title = if (lang == AppLanguage.ARABIC) "اللغة / Langue" else "Langue")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.values().forEach { appLang ->
                        val isSelected = state.language == appLang
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MoroccanEmerald else CedarWoodDark.copy(alpha = 0.7f))
                                .border(
                                    1.dp,
                                    if (isSelected) MoroccanGold else Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectLanguage(appLang) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = appLang.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = appLang.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // 2. Board Themes
                SettingsSectionHeader(title = if (lang == AppLanguage.ARABIC) "مظهر الرقعة (Thème du plateau)" else "Thème du plateau")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BoardTheme.values().forEach { theme ->
                        val isSelected = state.boardTheme == theme
                        val themeColor = when (theme) {
                            BoardTheme.WOOD -> BoardWoodDark
                            BoardTheme.ZELLIGE -> BoardZelligeDark
                            BoardTheme.MARBLE -> BoardMarbleDark
                            BoardTheme.DESERT -> BoardClassicDark
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.75f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) MoroccanGold else Color.White.copy(alpha = 0.15f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectTheme(theme) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(themeColor)
                                            .border(1.dp, MoroccanGoldLight, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = DamaStrings.themeName(lang, theme.id),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MoroccanGoldLight else Color.White
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MoroccanGoldLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Audio & Haptics
                SettingsSectionHeader(title = if (lang == AppLanguage.ARABIC) "الصوت والاهتزاز" else "Audio & Vibrations")
                Card(
                    colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.75f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MoroccanGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "المؤثرات الصوتية" else "Effets sonores",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "أصوات التحريك والأكل التكتيكي" else "Sons de déplacement et captures",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                            Switch(
                                checked = state.soundEnabled,
                                onCheckedChange = { onToggleSound() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MoroccanGoldLight,
                                    checkedTrackColor = MoroccanEmerald
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "الاهتزاز اللمسي" else "Retour haptique",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "اهتزاز خفيف عند لمس ونقل القطع" else "Vibrations lors des prises",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                            Switch(
                                checked = state.hapticEnabled,
                                onCheckedChange = { onToggleHaptic() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MoroccanGoldLight,
                                    checkedTrackColor = MoroccanEmerald
                                )
                            )
                        }
                    }
                }

                // 4. Moroccan Rules Configuration
                SettingsSectionHeader(title = if (lang == AppLanguage.ARABIC) "تهيئة قواعد اللعبة (Variantes)" else "Configuration des règles")
                Card(
                    colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.75f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MoroccanGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Prise obligatoire
                        RuleToggleRow(
                            title = if (lang == AppLanguage.ARABIC) "الأكل إجباري" else "Prise obligatoire",
                            subtitle = if (lang == AppLanguage.ARABIC) "يمنع أي نقلة عادية إن وجد أكل" else "Obligation de capturer si possible",
                            checked = rules.mandatoryCapture,
                            onToggle = { onUpdateRules(rules.copy(mandatoryCapture = it)) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Prise majoritaire
                        RuleToggleRow(
                            title = if (lang == AppLanguage.ARABIC) "الأكل الأكبر (Prise majoritaire)" else "Prise majoritaire",
                            subtitle = if (lang == AppLanguage.ARABIC) "إلزامية اختيار أطول سلسلة أكل" else "Prendre le maximum de pièces",
                            checked = rules.majorityCapture,
                            onToggle = { onUpdateRules(rules.copy(majorityCapture = it)) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Prise arrière par le pion
                        RuleToggleRow(
                            title = if (lang == AppLanguage.ARABIC) "أكل البيدق للخلف" else "Prise en arrière par le pion",
                            subtitle = if (lang == AppLanguage.ARABIC) "السماح للبيدق العادي بالقفز للخلف" else "Le pion peut capturer vers l'arrière",
                            checked = rules.pawnCanCaptureBackwards,
                            onToggle = { onUpdateRules(rules.copy(pawnCanCaptureBackwards = it)) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dame volante
                        RuleToggleRow(
                            title = if (lang == AppLanguage.ARABIC) "الضامة الطائرة (Dame volante)" else "Dame volante",
                            subtitle = if (lang == AppLanguage.ARABIC) "التحرك والأكل على كامل القطر" else "Déplacement longue portée",
                            checked = rules.flyingKing,
                            onToggle = { onUpdateRules(rules.copy(flyingKing = it)) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Souffler n'est pas jouer
                        RuleToggleRow(
                            title = if (lang == AppLanguage.ARABIC) "قاعدة السفل (Souffler / كفار)" else "Règle du Souffler",
                            subtitle = if (lang == AppLanguage.ARABIC) "قاعدة المقاهي الشعبية عند نسيان الأكل" else "Avertissement ou pénalité de prise",
                            checked = rules.huffingRule,
                            onToggle = { onUpdateRules(rules.copy(huffingRule = it)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MoroccanGoldLight,
        modifier = Modifier.padding(start = 4.dp, top = 6.dp)
    )
}

@Composable
private fun RuleToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, color = Color.White)
            Text(text = subtitle, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MoroccanGoldLight,
                checkedTrackColor = MoroccanEmerald
            )
        )
    }
}
