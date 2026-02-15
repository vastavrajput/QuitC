package com.example.quitc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quitc.model.DayStatus
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun StatsScreen(nav: NavController, vm: SmokeViewModel) {
    val colors = MaterialTheme.colorScheme
    val heartColor = Color(0xFFE91E63)
    val markedDays by vm.markedDays.collectAsStateWithLifecycle()
    val successRate by vm.successRate.collectAsStateWithLifecycle()
    val tokensLeft by vm.tokensLeft.collectAsStateWithLifecycle()
    val longestStreak by vm.longestStreak.collectAsStateWithLifecycle()
    
    var showResetDialog by remember { mutableStateOf(false) }

    val currentMonth = YearMonth.now()
    
    val cleanDaysInMonth = markedDays.filter { 
        it.key.month == currentMonth.month && it.key.year == currentMonth.year && it.value == DayStatus.CLEAN 
    }.size

    val tokensUsedThisMonth = markedDays.filter { 
        it.key.month == currentMonth.month && it.key.year == currentMonth.year && it.value == DayStatus.HEART 
    }.size

    val daysPassedInMonth = LocalDate.now().dayOfMonth
    val fails = daysPassedInMonth - cleanDaysInMonth - tokensUsedThisMonth

    val yearlyCleanDays = markedDays.filter { it.key.year == LocalDate.now().year && it.value == DayStatus.CLEAN }.size
    val totalTokensUsed = markedDays.filter { it.value == DayStatus.HEART }.size

    Surface(color = colors.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .border(2.dp, colors.outline)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, colors.outline)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Monthly Progress",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = colors.onSurface),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { showResetDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Reset Data", modifier = Modifier.size(16.dp), tint = colors.onSurfaceVariant)
                }
            }

            // Progress Summary Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.outline)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(20.dp), tint = colors.onSurface)
                        Spacer(Modifier.width(8.dp))
                        Text("Smoke-Free Days: ", fontWeight = FontWeight.Bold, color = colors.onSurface)
                        Text("$cleanDaysInMonth", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = colors.onSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(20.dp), tint = heartColor)
                        Spacer(Modifier.width(8.dp))
                        Text("Tokens Left: ", fontWeight = FontWeight.Bold, color = colors.onSurface)
                        Text("$tokensLeft", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = colors.onSurface)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Progress Bar Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.outline)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Monthly Achievement", style = MaterialTheme.typography.labelLarge, color = colors.onSurface)
                Spacer(Modifier.height(8.dp))
                
                // Custom wireframe progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .border(1.dp, colors.outline)
                        .background(colors.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (successRate > 0) successRate.toFloat() / 100f else 0.01f)
                            .fillMaxHeight()
                            .background(colors.primary)
                    )
                }
                
                Spacer(Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp), tint = heartColor)
                        Text(" $successRate% Clean Rate", fontWeight = FontWeight.Bold, color = colors.onSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.onSurface)
                        Text(" Fails: $fails", fontWeight = FontWeight.Bold, color = colors.onSurface)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Extra Stats (Longest Streak, etc.)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.outline)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Historical Data", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = colors.onSurface))
                    HorizontalDivider(color = colors.outline)
                    Text("• Longest Streak: $longestStreak Days", color = colors.onSurface)
                    Text("• Yearly Clean Days: $yearlyCleanDays", color = colors.onSurface)
                    Text("• Total Tokens Used: $totalTokensUsed", color = colors.onSurface)
                }
            }

            Spacer(Modifier.weight(1f))

            // Close Button
            Button(
                onClick = { nav.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.outline),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = colors.surface, contentColor = colors.onSurface)
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Close Statistics")
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Data?", color = colors.onSurface) },
            text = { Text("This will permanently delete all your smoking progress and token history. This action cannot be undone.", color = colors.onSurface) },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.resetData()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = colors.onSurface)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = colors.onSurface)
                }
            },
            containerColor = colors.surface,
            shape = RectangleShape,
            modifier = Modifier.border(1.dp, colors.outline)
        )
    }
}
