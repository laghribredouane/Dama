package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.DamaStrings
import com.example.model.AppLanguage
import com.example.ui.BoardUiState
import com.example.ui.components.MoroccanPatternBackground
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.CedarWoodMedium
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MoroccanEmerald
import com.example.ui.theme.MoroccanEmeraldLight
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineLobbyScreen(
    state: BoardUiState,
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lang = state.language
    var joinCodeInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = DamaStrings.onlineMultiplayer(lang),
                        fontWeight = FontWeight.Bold,
                        color = MoroccanGoldLight
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("online_back_button")
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F3E50))
                        .border(2.dp, Color(0xFF2EA090), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = MoroccanGoldLight,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Text(
                    text = if (lang == AppLanguage.ARABIC) "صالة اللعب عبر الإنترنت" else "Salons Multijoueur en Ligne",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoroccanGoldLight
                )

                // Server Status Notice (Transparent, honoring prompt requirements)
                Card(
                    colors = CardDefaults.cardColors(containerColor = CedarWoodDark.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF007A87).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2EA090),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "حالة خادم اللعب (Supabase Architecture)" else "Statut du serveur de jeu",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (lang == AppLanguage.ARABIC) {
                                    "الهيكل البرمجي للمزامنة وغرف اللعب جاهز للاتصال بقاعدة بيانات Supabase. للمباريات التنافسية المباشرة الآن، استخدم وضع 'لاعبان على نفس الجهاز' أو 'ضد الذكاء الاصطناعي'."
                                } else if (lang == AppLanguage.ENGLISH) {
                                    "The room synchronization architecture is pre-configured and ready for Supabase pairing. Use 'Two Players (Local)' or 'Vs AI' for live offline matches."
                                } else {
                                    "L'architecture de synchronisation des salons et des tours est prête pour être reliée à un backend Supabase. Pour jouer immédiatement, profitez du mode 'Deux Joueurs (Local)' ou 'Joueur contre IA'."
                                },
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Section 1: Create a Room
                Card(
                    colors = CardDefaults.cardColors(containerColor = CedarWoodDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MoroccanGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "إنشاء صالة خاصة للأصدقاء" else "Créer une partie privée",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoroccanGoldLight
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (lang == AppLanguage.ARABIC) "احصل على رمز دعوة وشاركه مع صديقك" else "Générez un code de salon et partagez-le",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (state.onlineRoomCode.isNotEmpty()) {
                            // Display generated room code
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .border(1.5.dp, MoroccanGold, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = state.onlineRoomCode,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MoroccanGoldLight,
                                    letterSpacing = 4.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Dama Maroc Room Code", state.onlineRoomCode)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Code copié !", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MoroccanGoldLight)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = if (lang == AppLanguage.ARABIC) "نسخ الرمز" else "Copier le code", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = state.onlineStatusText,
                                fontSize = 12.sp,
                                color = MoroccanEmeraldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Button(
                                onClick = onCreateRoom,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MoroccanEmerald,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("create_room_button")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "إنشاء صالة جديدة" else "Créer un salon",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                // Section 2: Join a Room with Code
                Card(
                    colors = CardDefaults.cardColors(containerColor = CedarWoodDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MoroccanGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "الانضمام برمز الصالة" else "Rejoindre avec un code",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoroccanGoldLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = joinCodeInput,
                            onValueChange = { joinCodeInput = it.uppercase() },
                            placeholder = { Text("Ex: DAMA8842", color = Color.White.copy(alpha = 0.4f)) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.4f),
                                focusedIndicatorColor = MoroccanGoldLight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("join_room_code_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (joinCodeInput.isNotBlank()) {
                                    onJoinRoom(joinCodeInput)
                                }
                            },
                            enabled = joinCodeInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0F3E50),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("join_room_button")
                        ) {
                            Icon(imageVector = Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "دخول الصالة" else "Rejoindre la partie",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
