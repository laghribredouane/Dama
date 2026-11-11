package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.MatchRecord
import com.example.model.PlayerProfile
import com.example.ui.components.MoroccanPatternBackground
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.CedarWoodMedium
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MoroccanEmerald
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight
import com.example.ui.theme.MoroccanRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: PlayerProfile,
    records: List<MatchRecord>,
    language: AppLanguage,
    onUpdateName: (String) -> Unit,
    onBack: () -> Unit
) {
    var isEditingName by remember { mutableStateOf(false) }
    var currentNameInput by remember { mutableStateOf(profile.name) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = DamaStrings.profile(language),
                        fontWeight = FontWeight.Bold,
                        color = MoroccanGoldLight
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("profile_back_button")
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Player Identity Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CedarWoodDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, MoroccanGold, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(CedarWoodDark, CedarWoodMedium, CedarWoodDark)
                                    )
                                )
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MoroccanEmerald)
                                    .border(2.dp, MoroccanGoldLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = MoroccanGoldLight,
                                    modifier = Modifier.size(54.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isEditingName) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = currentNameInput,
                                        onValueChange = { currentNameInput = it },
                                        singleLine = true,
                                        colors = TextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                                            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f),
                                            focusedIndicatorColor = MoroccanGoldLight
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            if (currentNameInput.isNotBlank()) {
                                                onUpdateName(currentNameInput)
                                            }
                                            isEditingName = false
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Enregistrer", tint = MoroccanGoldLight)
                                    }
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MoroccanGoldLight
                                    )
                                    IconButton(onClick = { isEditingName = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Modifier",
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = if (language == AppLanguage.ARABIC) "لاعب ضامة مغربية" else "Joueur de Dama Marocaine",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Statistics Grid
                item {
                    Text(
                        text = if (language == AppLanguage.ARABIC) "الإحصائيات الإجمالية" else "Statistiques globales",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoroccanGoldLight,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatTile(
                            title = if (language == AppLanguage.ARABIC) "مباريات" else "Parties",
                            value = "${profile.matchesPlayed}",
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = if (language == AppLanguage.ARABIC) "انتصارات" else "Victoires",
                            value = "${profile.wins}",
                            valueColor = MoroccanGoldLight,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = if (language == AppLanguage.ARABIC) "هزائم" else "Défaites",
                            value = "${profile.losses}",
                            valueColor = MoroccanRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = if (language == AppLanguage.ARABIC) "تعادلات" else "Nuls",
                            value = "${profile.draws}",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatTile(
                            title = if (language == AppLanguage.ARABIC) "نسبة الفوز" else "Taux de victoire",
                            value = "%.1f%%".format(profile.winRate),
                            valueColor = MoroccanEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = if (language == AppLanguage.ARABIC) "أفضل سلسلة" else "Meilleure série",
                            value = "${profile.bestStreak}",
                            valueColor = MoroccanGoldLight,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = if (language == AppLanguage.ARABIC) "القطع المأكولة" else "Captures totales",
                            value = "${profile.totalCaptures}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Cloud Sync Section (Supabase Architecture Ready)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF007A87).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF007A87).copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = Color(0xFF2EA090),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == AppLanguage.ARABIC) "المزامنة السحابية (Supabase Ready)" else "Sauvegarde locale & Cloud Ready",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (language == AppLanguage.ARABIC) "جميع بياناتك محفوظة محلياً. جاهز للربط مع Supabase" else "Toutes les données sont stockées hors-ligne. Prêt pour synchronisation Supabase.",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.65f)
                                )
                            }
                        }
                    }
                }

                // Recent Matches History
                item {
                    Text(
                        text = if (language == AppLanguage.ARABIC) "سجل المباريات الأخيرة" else "Historique des parties",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoroccanGoldLight,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                    )
                }

                if (records.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "لا توجد مباريات مسجلة بعد. ابدأ اللعب الآن!" else "Aucune partie enregistrée pour le moment. Jouez votre premier match !",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(records) { record ->
                        MatchRecordCard(record = record, language = language)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    valueColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, MoroccanGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
private fun MatchRecordCard(
    record: MatchRecord,
    language: AppLanguage
) {
    val outcomeColor = if (record.isWin) MoroccanGold else if (record.isDraw) MoroccanEmerald else MoroccanRed
    val outcomeLabel = if (record.isWin) {
        if (language == AppLanguage.ARABIC) "فوز" else "Victoire"
    } else if (record.isDraw) {
        if (language == AppLanguage.ARABIC) "تعادل" else "Match nul"
    } else {
        if (language == AppLanguage.ARABIC) "هزيمة" else "Défaite"
    }

    val modeLabel = when (record.mode) {
        GameMode.VS_AI -> "vs IA (${record.difficulty?.let { DamaStrings.difficultyName(language, it) } ?: ""})"
        GameMode.LOCAL_TWO_PLAYERS -> if (language == AppLanguage.ARABIC) "لاعبان محلياً" else "2 Joueurs local"
        GameMode.ONLINE_MULTIPLAYER -> if (language == AppLanguage.ARABIC) "أونلاين" else "En ligne"
    }

    val dateFormatted = remember(record.timestamp) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(record.timestamp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.75f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, outcomeColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
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
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(outcomeColor)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "$outcomeLabel • $modeLabel",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "$dateFormatted • ${record.totalMoves} coups • ${record.durationSeconds}s",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            if (record.capturesMade > 0) {
                Text(
                    text = "+${record.capturesMade} prises",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MoroccanGoldLight
                )
            }
        }
    }
}
