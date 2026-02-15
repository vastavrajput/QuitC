package com.example.quitc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.YearMonth
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quitc.model.DayStatus
import java.time.LocalDate
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog

@Composable
fun CalendarGrid(
    selectedMonth: YearMonth,
    markedDays: Map<LocalDate, DayStatus>,
    onDateClick: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    val firstDayOfMonth = selectedMonth.atDay(1)
    val daysInMonth = selectedMonth.lengthOfMonth()
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7)
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(selectedMonth.minusMonths(1)) }) {
                Text("<", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
            Text(
                text = selectedMonth.month.name.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 4.sp),
                textAlign = TextAlign.Center
            )
            IconButton(onClick = { onMonthChange(selectedMonth.plusMonths(1)) }) {
                Text(">", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
        }

        HorizontalDivider(color = Color.Black, thickness = 1.dp)

        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        HorizontalDivider(color = Color.Black, thickness = 1.dp)
        Spacer(Modifier.height(8.dp))

        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        Column(modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val day = cellIndex - firstDayOfWeek + 1
                        
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp)) {
                            if (day in 1..daysInMonth) {
                                val date = selectedMonth.atDay(day)
                                val status = markedDays[date]
                                val isToday = date == today

                                val backgroundColor = when (status) {
                                    DayStatus.CLEAN -> Color(0xFF4A4A4A)
                                    DayStatus.HEART -> Color(0xFFE91E63)
                                    else -> Color.Transparent
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(
                                            width = if (isToday) 2.dp else 1.dp,
                                            color = if (isToday) Color.Black else if (status == null) Color.LightGray else Color.Black
                                        )
                                        .background(backgroundColor)
                                        .clickable { onDateClick(date) }
                                ) {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp, 
                                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Bold
                                        ),
                                        color = if (status != null) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nav: NavController,
    vm: SmokeViewModel
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val markedDays by vm.markedDays.collectAsStateWithLifecycle()
    val selectedMonth by vm.selectedMonth.collectAsStateWithLifecycle()
    val currentStreak by vm.currentStreak.collectAsStateWithLifecycle()
    val tokensLeft by vm.tokensLeft.collectAsStateWithLifecycle()
    val successRate by vm.successRate.collectAsStateWithLifecycle()

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showTokenDialog by remember { mutableStateOf(false) }
    var showOptionsSheet by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val todayStatus = markedDays[today]

    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .border(1.dp, Color.Black)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .border(1.dp, Color.Black)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Quit Smoking!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Whatshot, contentDescription = null, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "$currentStreak Day Streak",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Calendar Box
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().border(1.dp, Color.Black).padding(8.dp)) {
                CalendarGrid(
                    selectedMonth = selectedMonth,
                    markedDays = markedDays,
                    onDateClick = {
                        selectedDate = it
                        showOptionsSheet = true
                    },
                    onMonthChange = { vm.updateMonth(it) }
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        vm.updateDay(today, DayStatus.CLEAN)
                        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50), -1))
                    },
                    enabled = todayStatus != DayStatus.CLEAN,
                    modifier = Modifier.weight(1.2f).border(1.dp, if(todayStatus == DayStatus.CLEAN) Color.LightGray else Color.Black),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White, 
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFFF0F0F0),
                        disabledContentColor = Color.Gray
                    ),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (todayStatus == DayStatus.CLEAN) "Clean" else "Clean Today", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showTokenDialog = true },
                    enabled = todayStatus != DayStatus.HEART,
                    modifier = Modifier.weight(1f).border(1.dp, if(todayStatus == DayStatus.HEART) Color.LightGray else Color.Black),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White, 
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFFF0F0F0),
                        disabledContentColor = Color.Gray
                    ),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Use Token", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                HorizontalDivider(color = Color.Black, thickness = 1.dp)
                Spacer(Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tokens Left: ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.DarkGray)
                    Text(" $tokensLeft", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text(text = "Success Rate: $successRate%", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = { nav.navigate("stats") },
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black),
                shape = RectangleShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text("VIEW MONTHLY PROGRESS", fontWeight = FontWeight.ExtraBold)
            }
        }
    }

    if (showTokenDialog) {
        Dialog(onDismissRequest = { showTokenDialog = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Use a Token?", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color.Black)
                    Spacer(Modifier.height(24.dp))
                    Text("You have $tokensLeft tokens left this month.", fontSize = 16.sp)
                    Spacer(Modifier.height(32.dp))
                    
                    Button(
                        onClick = {
                            vm.updateDay(today, DayStatus.HEART)
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                            showTokenDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63), contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Confirm Use Token", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { showTokenDialog = false },
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black),
                        shape = RectangleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showOptionsSheet && selectedDate != null) {
        ModalBottomSheet(
            onDismissRequest = { showOptionsSheet = false },
            containerColor = Color.White,
            shape = RectangleShape
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Update Date: $selectedDate",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(color = Color.Black)
                Button(
                    onClick = { 
                        vm.updateDay(selectedDate!!, DayStatus.CLEAN)
                        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50), -1))
                        showOptionsSheet = false 
                    },
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) { Text("Mark Clean", fontWeight = FontWeight.Bold) }
                Button(
                    onClick = { 
                        vm.updateDay(selectedDate!!, DayStatus.HEART)
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                        showOptionsSheet = false 
                    },
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) { Text("Use Token", fontWeight = FontWeight.Bold) }
                OutlinedButton(
                    onClick = { 
                        vm.updateDay(selectedDate!!, null)
                        vibrator.vibrate(VibrationEffect.createOneShot(30, 100))
                        showOptionsSheet = false 
                    },
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black),
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) { Text("Clear Selection", fontWeight = FontWeight.Bold) }
            }
        }
    }
}
