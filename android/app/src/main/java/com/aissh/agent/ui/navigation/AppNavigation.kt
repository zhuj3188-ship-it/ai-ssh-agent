package com.aissh.agent.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.aissh.agent.R
import com.aissh.agent.ui.screens.*
import com.aissh.agent.ui.theme.*
import com.aissh.agent.viewmodel.AuthViewModel

sealed class Screen(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Chat : Screen("chat", R.string.chat, Icons.Default.Chat)
    object Servers : Screen("servers", R.string.servers, Icons.Default.Dns)
    object Quota : Screen("quota", R.string.quota, Icons.Default.DataUsage)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
}

@Composable
fun AppNavigation() {
    val authVm: AuthViewModel = hiltViewModel()
    val authState by authVm.state.collectAsState()
    if (!authState.isLoggedIn) { LoginScreen(authVm); return }

    val navController = rememberNavController()
    val screens = listOf(Screen.Chat, Screen.Servers, Screen.Quota, Screen.Settings)
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    Scaffold(bottomBar = {
        NavigationBar(containerColor = SurfaceDark) {
            screens.forEach { screen ->
                NavigationBarItem(selected = currentRoute == screen.route,
                    onClick = { navController.navigate(screen.route) { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true } },
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(stringResource(screen.labelRes)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = CyanPrimary, selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary.copy(alpha = 0.15f), unselectedIconColor = MutedGray, unselectedTextColor = MutedGray))
            }
        }
    }) { padding ->
        NavHost(navController, startDestination = Screen.Chat.route, Modifier.padding(padding)) {
            composable(Screen.Chat.route) { ChatScreen() }
            composable(Screen.Servers.route) { ServersScreen() }
            composable(Screen.Quota.route) { QuotaScreen() }
            composable(Screen.Settings.route) { SettingsScreen(onLogout = { authVm.logout() }) }
        }
    }
}
