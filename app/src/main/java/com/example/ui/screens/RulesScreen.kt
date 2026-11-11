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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.DamaStrings
import com.example.model.AppLanguage
import com.example.ui.components.MoroccanPatternBackground
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MoroccanEmerald
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight
import com.example.ui.theme.MoroccanRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    language: AppLanguage,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = DamaStrings.rules(language),
                        fontWeight = FontWeight.Bold,
                        color = MoroccanGoldLight
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("rules_back_button")
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Intro
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.85f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MoroccanGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (language == AppLanguage.ARABIC) "أصول وتقاليد الضامة المغربية" else "Tradition de la Dama Marocaine",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoroccanGoldLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "تُعد الضامة من أعرق الألعاب الاستراتيجية الشعبية في المقاهي والأحياء المغربية. تُلعَب على لوحة من 64 خانة (8×8) أو 100 خانة، مع قواعد صارمة تميزها عن باقي النسخ العالمية."
                            } else if (language == AppLanguage.ENGLISH) {
                                "Moroccan Draughts (Dama) is a popular strategy cafe game in Morocco, played on an 8x8 or 10x10 board with distinct rules honoring speed, sharp calculation, and tactical traps."
                            } else {
                                "La Dama est l'un des jeux de stratégie les plus populaires dans les cafés et ruelles du Maroc. Joué sur un damier de 64 cases (8x8) ou 100 cases, il possède des règles authentiques qui le distinguent des dames classiques."
                            },
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                    }
                }

                // Rule 1: Déplacements ordinaires
                RuleCard(
                    icon = Icons.Default.Forward,
                    iconColor = MoroccanEmerald,
                    title = if (language == AppLanguage.ARABIC) "1. حركة البيادق العادية" else "1. Déplacement des pions",
                    content = if (language == AppLanguage.ARABIC) {
                        "تتحرك القطع العادية خطوة واحدة قطرياً نحو الأمام على الخانات الداكنة فقط."
                    } else if (language == AppLanguage.ENGLISH) {
                        "Regular men move one diagonal square forward on dark squares only."
                    } else {
                        "Les pions se déplacent d'une seule case en diagonale vers l'avant, uniquement sur les cases sombres."
                    }
                )

                // Rule 2: Prise obligatoire (L'OBLIGATION D'ACHAT/PRISE)
                RuleCard(
                    icon = Icons.Default.Warning,
                    iconColor = MoroccanRed,
                    title = if (language == AppLanguage.ARABIC) "2. الأكل إجباري (Prise obligatoire)" else "2. Prise obligatoire",
                    content = if (language == AppLanguage.ARABIC) {
                        "في الضامة المغربية، إذا أتيحت لك فرصة لأكل قطعة خصم، فالقفز والأكل إجباري تماماً ولا يمكنك لعب حركة عادية أخرى."
                    } else if (language == AppLanguage.ENGLISH) {
                        "If a capture is available, capturing is strictly mandatory! Non-capturing moves are locked."
                    } else {
                        "Si vous avez la possibilité de capturer une pièce adverse, la prise est strictement obligatoire. Vous ne pouvez pas jouer un autre coup."
                    }
                )

                // Rule 3: Prise en arrière par le pion
                RuleCard(
                    icon = Icons.Default.FlashOn,
                    iconColor = MoroccanGold,
                    title = if (language == AppLanguage.ARABIC) "3. الأكل للخلف بالبيدق" else "3. Prise en arrière par le pion",
                    content = if (language == AppLanguage.ARABIC) {
                        "خلافاً للتحرك العادي، يجوز للبيدق العادي القفز للأمام أو للخلف لأكل قطع الخصم ومتابعة الأكل المتسلسل."
                    } else if (language == AppLanguage.ENGLISH) {
                        "Unlike regular moves, ordinary pawns can capture backwards as well as forwards in jump sequences."
                    } else {
                        "Bien que le pion n'avance que vers l'avant, il peut sauter vers l'avant ET vers l'arrière pour capturer une pièce adverse."
                    }
                )

                // Rule 4: Prise majoritaire
                RuleCard(
                    icon = Icons.Default.Casino,
                    iconColor = MoroccanEmerald,
                    title = if (language == AppLanguage.ARABIC) "4. قاعدة الأكل الأكبر (Prise majoritaire)" else "4. La prise majoritaire",
                    content = if (language == AppLanguage.ARABIC) {
                        "إذا كان أمامك عدة خيارات للأكل، فأنت ملزم باختيار المسار الذي يحصد أكبر عدد ممكن من قطع الخصم."
                    } else if (language == AppLanguage.ENGLISH) {
                        "When multiple capture lines are possible, you MUST pick the one that takes the maximum number of pieces."
                    } else {
                        "Lorsque plusieurs prises sont possibles, le joueur est obligé de choisir la chaîne qui capture le plus grand nombre de pièces."
                    }
                )

                // Rule 5: Promotion & Dame volante
                RuleCard(
                    icon = Icons.Default.Star,
                    iconColor = MoroccanGoldLight,
                    title = if (language == AppLanguage.ARABIC) "5. الترقية إلى ضامة (الضامة الطائرة)" else "5. Promotion & Dame volante",
                    content = if (language == AppLanguage.ARABIC) {
                        "عند وصول البيدق لآخر صف عند الخصم يصبح 'ضامة' (ملك). تتحرك الضامة على كامل القطر بحرية وتأكل عن بُعد وتقف في أي خانة فارغة بعد القطعة المأكولة."
                    } else if (language == AppLanguage.ENGLISH) {
                        "Reaching the opponent's back rank promotes the piece into a King (Dama). The flying king traverses any empty diagonal squares and captures across distances."
                    } else {
                        "En atteignant la rangée opposée, le pion devient 'Dame'. La dame volante peut parcourir toute la diagonale libre, capturer à distance et se poser sur n'importe quelle case libre derrière."
                    }
                )

                // Rule 6: Souffler n'est pas jouer (Cafar)
                RuleCard(
                    icon = Icons.Default.Warning,
                    iconColor = MoroccanRed,
                    title = if (language == AppLanguage.ARABIC) "6. قاعدة السفل / كفار (Souffler)" else "6. Règle du 'Souffler'",
                    content = if (language == AppLanguage.ARABIC) {
                        "تقليدياً في المقاهي الشعبية، إذا نسي لاعب أكل قطعة وكان بإمكانه ذلك، يمكن للخصم 'سفل' القطعة (إزالتها كعقوبة قبل لعبه)."
                    } else if (language == AppLanguage.ENGLISH) {
                        "Historically in Moroccan cafes, neglecting an available capture allowed the opponent to 'huff' (remove) the offending piece."
                    } else {
                        "Dans la tradition des cafés marocains, si un joueur omettait une prise, l'adversaire avait le droit de 'souffler' la pièce oublieuse (la retirer du jeu)."
                    }
                )

                // Rule 7: Conditions de victoire
                RuleCard(
                    icon = Icons.Default.EmojiEvents,
                    iconColor = MoroccanGold,
                    title = if (language == AppLanguage.ARABIC) "7. نهاية المباراة والفوز" else "7. Conditions de victoire",
                    content = if (language == AppLanguage.ARABIC) {
                        "يفوز اللاعب بأسر جميع قطع الخصم، أو بحصارها تماماً بحيث لا يملك الخصم أي نقلة قانونية متاحة."
                    } else if (language == AppLanguage.ENGLISH) {
                        "Victory is achieved by capturing all enemy pieces or by completely blocking them with no legal moves."
                    } else {
                        "Vous gagnez la partie en capturant la totalité des pièces adverses ou en bloquant complètement l'adversaire (aucun coup légal disponible)."
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RuleCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    content: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.85f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MoroccanGold.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f))
                    .border(1.dp, iconColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoroccanGoldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
