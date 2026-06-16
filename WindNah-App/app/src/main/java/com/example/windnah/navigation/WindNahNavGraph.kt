package com.example.windnah.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.windnah.feature.discover.DiscoverScreen
import com.windnah.feature.facts.FactsScreen
import com.windnah.feature.myturbines.MyTurbinesScreen
import com.windnah.feature.onboarding.OnboardingScreen
import com.windnah.feature.profile.ProfileScreen

const val ROUTE_ONBOARDING = "onboarding"
const val ROUTE_DISCOVER = "discover"
const val ROUTE_FACTS = "facts"
const val ROUTE_MY_TURBINES = "my_turbines"
const val ROUTE_PROFILE = "profile"
const val ROUTE_WIND_FARM_DETAIL = "wind_farm_detail/{windFarmId}"

fun windFarmDetailRoute(windFarmId: String) = "wind_farm_detail/$windFarmId"

@Composable
fun WindNahNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = ROUTE_DISCOVER,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(ROUTE_ONBOARDING) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(ROUTE_DISCOVER) {
                        popUpTo(ROUTE_ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        composable(ROUTE_DISCOVER) {
            DiscoverScreen(
                onWindFarmClick = { windFarmId ->
                    navController.navigate(windFarmDetailRoute(windFarmId))
                }
            )
        }
        composable(ROUTE_FACTS) {
            FactsScreen()
        }
        composable(ROUTE_MY_TURBINES) {
            MyTurbinesScreen(
                onWindFarmClick = { windFarmId ->
                    navController.navigate(windFarmDetailRoute(windFarmId))
                }
            )
        }
        composable(ROUTE_PROFILE) {
            ProfileScreen()
        }
    }
}
