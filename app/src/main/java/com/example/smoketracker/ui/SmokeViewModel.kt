package com.example.quitc.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quitc.data.DataStoreManager
import com.example.quitc.model.DayStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class SmokeViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val markedDays: StateFlow<Map<LocalDate, DayStatus>> = dataStoreManager.markedDaysFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    fun updateMonth(newMonth: YearMonth) {
        _selectedMonth.value = newMonth
    }

    fun updateDay(date: LocalDate, status: DayStatus?) {
        viewModelScope.launch {
            val currentMap = markedDays.value.toMutableMap()
            if (status == null) {
                currentMap.remove(date)
            } else {
                if (status == DayStatus.HEART) {
                    val targetMonth = YearMonth.from(date)
                    val heartsThisMonth = currentMap.filter {
                        YearMonth.from(it.key) == targetMonth && it.value == DayStatus.HEART
                    }.size
                    if (heartsThisMonth < 3) {
                        currentMap[date] = status
                    }
                } else {
                    currentMap[date] = status
                }
            }
            dataStoreManager.saveMarkedDays(currentMap)
        }
    }

    // Reactive derived state for the UI
    val successRate: StateFlow<Int> = combine(markedDays, _selectedMonth) { days, month ->
        calculateSuccessRate(days, month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    val tokensLeft: StateFlow<Int> = combine(markedDays, _selectedMonth) { days, month ->
        val tokensUsed = days.filter {
            YearMonth.from(it.key) == month && it.value == DayStatus.HEART
        }.size
        (3 - tokensUsed).coerceAtLeast(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    val currentStreak: StateFlow<Int> = markedDays.map { days ->
        calculateCurrentActiveStreak(days)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val longestStreak: StateFlow<Int> = markedDays.map { days ->
        calculateHistoricalLongestStreak(days)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun calculateSuccessRate(days: Map<LocalDate, DayStatus>, month: YearMonth): Int {
        val daysPassedInMonth = when {
            month == YearMonth.now() -> LocalDate.now().dayOfMonth
            month.isBefore(YearMonth.now()) -> month.lengthOfMonth()
            else -> 0
        }

        return if (daysPassedInMonth == 0) 100
        else {
            val cleanDaysInMonth = days.filter {
                YearMonth.from(it.key) == month && it.value == DayStatus.CLEAN
            }.size
            ((cleanDaysInMonth.toFloat() / daysPassedInMonth) * 100).toInt()
        }
    }

    private fun calculateCurrentActiveStreak(days: Map<LocalDate, DayStatus>): Int {
        var checkDate = LocalDate.now()
        var streak = 0
        
        if (days[checkDate] == null) {
            checkDate = checkDate.minusDays(1)
        }
        
        while (days[checkDate] == DayStatus.CLEAN) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    private fun calculateHistoricalLongestStreak(days: Map<LocalDate, DayStatus>): Int {
        if (days.isEmpty()) return 0
        val sortedDates = days.keys.sorted()
        var longest = 0
        var current = 0
        var lastDate: LocalDate? = null

        for (date in sortedDates) {
            val status = days[date]
            if (status == DayStatus.CLEAN) {
                if (lastDate == null || date == lastDate.plusDays(1)) {
                    current++
                } else {
                    current = 1
                }
                if (current > longest) longest = current
                lastDate = date
            } else {
                current = 0
                lastDate = null
            }
        }
        return longest
    }
}
