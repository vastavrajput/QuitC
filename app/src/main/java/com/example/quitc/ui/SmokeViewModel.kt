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

    private val _markedDays = MutableStateFlow<Map<LocalDate, DayStatus>>(emptyMap())
    val markedDays: StateFlow<Map<LocalDate, DayStatus>> = _markedDays.asStateFlow()

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreManager.markedDaysFlow.collect {
                _markedDays.value = it
            }
        }
    }

    fun updateMonth(newMonth: YearMonth) {
        _selectedMonth.value = newMonth
    }

    fun updateDay(date: LocalDate, status: DayStatus?) {
        _markedDays.update { current ->
            val newMap = current.toMutableMap()
            if (status == null) {
                newMap.remove(date)
            } else {
                newMap[date] = status
            }
            newMap
        }

        viewModelScope.launch {
            dataStoreManager.saveMarkedDays(_markedDays.value)
        }
    }

    fun resetData() {
        _markedDays.value = emptyMap()
        viewModelScope.launch {
            dataStoreManager.clearAllData()
        }
    }

    // Reactive derived state for the UI - using Eagerly to ensure immediate updates
    val successRate: StateFlow<Int> = combine(markedDays, _selectedMonth) { days, month ->
        calculateSuccessRate(days, month)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 100)

    val tokensLeft: StateFlow<Int> = combine(markedDays, _selectedMonth) { days, month ->
        val tokensUsed = days.filter {
            YearMonth.from(it.key) == month && it.value == DayStatus.HEART
        }.size
        3 - tokensUsed
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 3)

    val currentStreak: StateFlow<Int> = markedDays.map { days ->
        calculateCurrentActiveStreak(days)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val longestStreak: StateFlow<Int> = markedDays.map { days ->
        calculateHistoricalLongestStreak(days)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private fun calculateSuccessRate(days: Map<LocalDate, DayStatus>, month: YearMonth): Int {
        val daysPassedInMonth = when {
            month == YearMonth.now() -> LocalDate.now().dayOfMonth
            month.isBefore(YearMonth.now()) -> month.lengthOfMonth()
            else -> 0
        }

        return if (daysPassedInMonth == 0) 100
        else {
            val cleanDaysInMonth = days.filter {
                YearMonth.from(it.key) == month && (it.value == DayStatus.CLEAN || it.value == DayStatus.HEART)
            }.size
            ((cleanDaysInMonth.toFloat() / daysPassedInMonth) * 100).toInt()
        }
    }

    private fun calculateCurrentActiveStreak(days: Map<LocalDate, DayStatus>): Int {
        var checkDate = LocalDate.now()
        
        // If today is HEART, streak is 0
        if (days[checkDate] == DayStatus.HEART) return 0
        
        // If today isn't marked, start checking from yesterday to see the current streak
        if (days[checkDate] == null) {
            checkDate = checkDate.minusDays(1)
        }
        
        var streak = 0
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
                if (lastDate != null && date == lastDate.plusDays(1)) {
                    current++
                } else {
                    current = 1
                }
                if (current > longest) longest = current
                lastDate = date
            } else {
                // Any status other than CLEAN (including HEART) breaks the streak
                current = 0
                lastDate = null
            }
        }
        return longest
    }
}
