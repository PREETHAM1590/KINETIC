package com.kinetic.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kinetic.app.ui.screens.AICalorieScannerScreen
import com.kinetic.app.ui.screens.ActiveWorkoutScreen
import com.kinetic.app.ui.screens.BookClassScreen
import com.kinetic.app.ui.screens.ConsentScreen
import com.kinetic.app.ui.screens.DataDeletionScreen
import com.kinetic.app.ui.screens.DietScreen
import com.kinetic.app.ui.screens.LoginScreen
import com.kinetic.app.ui.screens.MealDetailScreen
import com.kinetic.app.ui.screens.MembershipScreen
import com.kinetic.app.ui.screens.OnboardingScreen
import com.kinetic.app.ui.screens.PrivacyPolicyScreen
import com.kinetic.app.ui.screens.ProfileScreen
import com.kinetic.app.ui.screens.ReportsScreen
import com.kinetic.app.ui.screens.SettingsScreen
import com.kinetic.app.ui.screens.SignupScreen
import com.kinetic.app.ui.screens.SupportScreen
import com.kinetic.app.ui.screens.WorkoutDetailScreen
import com.kinetic.app.ui.screens.WorkoutsScreen
import com.kinetic.app.ui.screens.bottomNavItems
import com.kinetic.app.ui.theme.Background
import com.kinetic.app.ui.theme.Lime
import com.kinetic.app.ui.theme.Surface1
import com.kinetic.app.ui.theme.Surface2
import com.kinetic.app.ui.theme.TextMuted

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val tabRoutes = bottomNavItems.map { it.route }.toSet()
    val showBottomBar = currentDestination?.route in tabRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Surface1) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
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
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Lime,
                                selectedTextColor = Lime,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = Surface2
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onGetStartedClick = {
                        navController.navigate(Screen.Consent.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Consent.route) {
                ConsentScreen(
                    onConsentGiven = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Consent.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onSignInSuccess = {
                        navController.navigate(Screen.Workouts.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    },
                    onSignUpClick = { navController.navigate(Screen.Signup.route) }
                )
            }

            composable(Screen.Signup.route) {
                SignupScreen(
                    onSignUpSuccess = {
                        navController.navigate(Screen.Workouts.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    },
                    onSignInClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Workouts.route) {
                WorkoutsScreen(navController = navController)
            }

            composable(Screen.Diet.route) {
                DietScreen(navController = navController)
            }

            composable(Screen.Reports.route) {
                ReportsScreen()
            }

            composable(Screen.Membership.route) {
                MembershipScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }

            composable(
                route = Screen.WorkoutDetail.route,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                WorkoutDetailScreen(
                    workoutId = workoutId,
                    onNavigateToActiveWorkout = { id ->
                        navController.navigate(Screen.ActiveWorkout.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.ActiveWorkout.route,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                ActiveWorkoutScreen(workoutId = workoutId)
            }

            composable(
                route = Screen.MealDetail.route,
                arguments = listOf(navArgument("mealId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
                MealDetailScreen(mealId = mealId)
            }

            composable(Screen.AICalorieScanner.route) {
                AICalorieScannerScreen()
            }

            composable(Screen.BookClass.route) {
                BookClassScreen()
            }

            composable(Screen.Support.route) {
                SupportScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onPrivacyPolicyClick = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onDataDeletionClick = { navController.navigate(Screen.DataDeletion.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.DataDeletion.route) {
                DataDeletionScreen(
                    onBack = { navController.popBackStack() },
                    onDeleted = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
