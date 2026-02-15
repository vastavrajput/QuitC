package com.example.quitc.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quitc.model.DayStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "quitc_prefs")

class DataStoreManager(private val context: Context) {

    private val markedDaysKey = stringPreferencesKey("marked_days")

    val markedDaysFlow: Flow<Map<LocalDate, DayStatus>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[markedDaysKey] ?: ""
            if (jsonString.isEmpty()) {
                emptyMap()
            } else {
                try {
                    val rawMap = Json.decodeFromString<Map<String, DayStatus>>(jsonString)
                    rawMap.mapKeys { LocalDate.parse(it.key) }
                } catch (e: Exception) {
                    emptyMap()
                }
            }
        }

    suspend fun saveMarkedDays(markedDays: Map<LocalDate, DayStatus>) {
        val rawMap = markedDays.mapKeys { it.key.toString() }
        val jsonString = Json.encodeToString(rawMap)
        context.dataStore.edit { preferences ->
            preferences[markedDaysKey] = jsonString
        }
    }
}
