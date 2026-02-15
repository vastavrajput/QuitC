package com.example.quitc.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.quitc.data.DataStoreManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Composable
fun AppNavigator() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    
    val vm: SmokeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SmokeViewModel(dataStoreManager) as T
            }
        }
    )
    
    val markedDays by vm.markedDays.collectAsState()
    
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { 
            HomeScreen(nav = nav, vm = vm) 
        }
        composable("stats") { 
            StatsScreen(nav = nav, vm = vm)
        }
    }
}
