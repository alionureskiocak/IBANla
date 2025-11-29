package com.example.ibanla.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ibanla.domain.model.BottomNavigationItem
import com.example.ibanla.presentation.iban.IbanScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            SetUpNavigation(navController)

        }
    }
}

@Composable
fun SetUpNavigation(navController : NavHostController) {

    val items = listOf<BottomNavigationItem>(
        BottomNavigationItem(
            route = "IbanScreen",
            title = "Iban'lar",
            selectedIcon = Icons.Filled.Wallet,
            unselectedIcon = Icons.Default.Wallet
        )
    )
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route)
                                }
                            },
                            label = {
                                Text(item.title)
                            },
                            icon = {
                                Icon(imageVector = if (currentRoute == item.route) item.selectedIcon
                                else item.unselectedIcon, contentDescription = null)
                            }
                        )
                    }
                }
            },

        ){ paddingValue ->
            NavHost(navController = navController, startDestination = "IbanScreen",
                modifier = Modifier.padding(paddingValue)
                ){
                composable("IbanScreen") { IbanScreen() }
            }
        }
    }
}

