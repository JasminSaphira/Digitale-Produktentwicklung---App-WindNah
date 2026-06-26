package com.example.windnah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.windnah.navigation.ROUTE_DISCOVER
import com.example.windnah.navigation.ROUTE_FACTS
import com.example.windnah.navigation.ROUTE_MY_TURBINES
import com.example.windnah.navigation.ROUTE_PROFILE
import com.example.windnah.navigation.WindNahNavGraph
import com.example.windnah.ui.theme.WindNahNavIndicator
import com.example.windnah.ui.theme.WindNahNavIndicatorDark
import com.example.windnah.ui.theme.WindNahNavOnIndicator
import com.example.windnah.ui.theme.WindNahNavOnIndicatorDark
import com.example.windnah.ui.theme.WindNahNavOnSurfaceVariant
import com.example.windnah.ui.theme.WindNahNavOnSurfaceVariantDark
import com.example.windnah.ui.theme.WindNahNavSurface
import com.example.windnah.ui.theme.WindNahNavSurfaceDark
import com.example.windnah.ui.theme.WindNahSystemBars
import com.example.windnah.ui.theme.WindNahTheme
import com.windnah.feature.onboarding.LaunchScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = hiltViewModel()
            val startDestination by appViewModel.startDestination.collectAsStateWithLifecycle()
            val darkModeEnabled by appViewModel.darkModeEnabled.collectAsStateWithLifecycle()

            WindNahTheme(darkTheme = darkModeEnabled) {
                WindNahSystemBars()

                if (startDestination == null) {
                    LaunchScreen()
                } else {
                    WindNahApp(
                        startDestination = startDestination!!,
                        darkTheme = darkModeEnabled,
                    )
                }
            }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem(
        route = ROUTE_DISCOVER,
        label = "Entdecken",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Filled.Explore,
    ),
    BottomNavItem(
        route = ROUTE_FACTS,
        label = "Fakten",
        selectedIcon = Icons.Filled.FactCheck,
        unselectedIcon = Icons.Filled.FactCheck,
    ),
    BottomNavItem(
        route = ROUTE_MY_TURBINES,
        label = "Meine Anlagen",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Filled.Star,
    ),
    BottomNavItem(
        route = ROUTE_PROFILE,
        label = "Profil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Filled.Person,
    ),
)

private val bottomNavRoutes = bottomNavItems.map { it.route }.toSet()

@Composable
private fun WindNahApp(
    startDestination: String,
    darkTheme: Boolean,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val navColors = if (darkTheme) {
        BottomNavColors(
            containerColor = WindNahNavSurfaceDark,
            indicatorColor = WindNahNavIndicatorDark,
            selectedContentColor = WindNahNavOnIndicatorDark,
            unselectedContentColor = WindNahNavOnSurfaceVariantDark,
        )
    } else {
        BottomNavColors(
            containerColor = WindNahNavSurface,
            indicatorColor = WindNahNavIndicator,
            selectedContentColor = WindNahNavOnIndicator,
            unselectedContentColor = WindNahNavOnSurfaceVariant,
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                NavigationBar(
                    modifier = Modifier.shadow(
                        elevation = 6.dp,
                        shape = RectangleShape,
                        clip = false,
                    ),
                    containerColor = navColors.containerColor,
                    windowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = navColors.selectedContentColor,
                                selectedTextColor = navColors.selectedContentColor,
                                unselectedIconColor = navColors.unselectedContentColor,
                                unselectedTextColor = navColors.unselectedContentColor,
                                indicatorColor = navColors.indicatorColor,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        WindNahNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = startDestination,
        )
    }
}

private data class BottomNavColors(
    val containerColor: androidx.compose.ui.graphics.Color,
    val indicatorColor: androidx.compose.ui.graphics.Color,
    val selectedContentColor: androidx.compose.ui.graphics.Color,
    val unselectedContentColor: androidx.compose.ui.graphics.Color,
)
