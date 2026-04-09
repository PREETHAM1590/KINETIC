package com.kinetic.app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.CardMembership
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.kinetic.app.ui.navigation.NavGraph
import com.kinetic.app.ui.navigation.Screen

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Workouts", Screen.Workouts.route, Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    BottomNavItem("Diet", Screen.Diet.route, Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    BottomNavItem("Reports", Screen.Reports.route, Icons.Filled.ShowChart, Icons.Outlined.ShowChart),
    BottomNavItem("Membership", Screen.Membership.route, Icons.Filled.CardMembership, Icons.Outlined.CardMembership),
    BottomNavItem("Profile", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun MainScreen(
    navController: NavHostController,
    startDestination: String = Screen.Workouts.route
) {
    NavGraph(
        navController = navController,
        startDestination = startDestination
    )
}