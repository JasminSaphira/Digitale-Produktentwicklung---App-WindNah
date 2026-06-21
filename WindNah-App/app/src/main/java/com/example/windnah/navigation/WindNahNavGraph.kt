package com.example.windnah.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.windnah.feature.auth.LoginScreen
import com.windnah.feature.auth.RegistrationScreen
import com.windnah.feature.discover.DiscoverScreen
import com.windnah.feature.facts.FactsScreen
import com.windnah.feature.myturbines.MyTurbinesScreen
import com.windnah.feature.onboarding.OnboardingScreen
import com.windnah.feature.profile.ProfileScreen
import com.windnah.feature.windparkdetail.WindFarmDetailScreen

const val ROUTE_ONBOARDING = "onboarding"
const val ROUTE_DISCOVER = "discover"
const val ROUTE_FACTS = "facts"
const val ROUTE_MY_TURBINES = "my_turbines"
const val ROUTE_PROFILE = "profile"
const val ROUTE_WIND_FARM_DETAIL = "wind_farm_detail/{windFarmId}"
const val ROUTE_LOGIN = "login"
const val ROUTE_REGISTER = "register"

fun windFarmDetailRoute(windFarmId: String) =
    "wind_farm_detail/${java.net.URLEncoder.encode(windFarmId, "UTF-8")}"

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
            ProfileScreen(
                onLoginClick = { navController.navigate(ROUTE_LOGIN) }
            )
        }
        composable(
            route = ROUTE_WIND_FARM_DETAIL,
            arguments = listOf(navArgument("windFarmId") {
                type = NavType.StringType
                nullable = false
            }),
        ) {
            WindFarmDetailScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate(ROUTE_REGISTER) },
            )
        }
        composable(ROUTE_REGISTER) {
            RegistrationScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

