package com.example.weatherapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.screen.SearchScreen
import com.example.weatherapp.ui.vm.WeatherViewModel

object Routes {
    const val SEARCH = "search"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SEARCH) {
        composable(Routes.SEARCH) {
            val vm: WeatherViewModel = hiltViewModel()
            SearchScreen(vm)
        }
    }
}
