package com.kinetic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.kinetic.app.data.models.HiitItem
import com.kinetic.app.data.models.WorkoutItem
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.LimeBadge
import com.kinetic.app.ui.components.ScreenHeadline
import com.kinetic.app.ui.navigation.Screen
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.WorkoutsUiState
import com.kinetic.app.ui.viewmodels.WorkoutsViewModel

@Composable
fun WorkoutsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: WorkoutsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.WorkoutDetail.createRoute("w1"))
                },
                containerColor = Lime,
                contentColor = Background,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Workout"
                )
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            ScreenHeadline(title = "WORKOUTS", subtitle = "PUSH YOUR LIMITS")

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Lime)
                }
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Something went wrong",
                    color = Error,
                    fontSize = 16.sp
                )
            } else {
                Text(
                    text = "HIIT",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                uiState.hiitWorkouts.forEach { hiit ->
                    HiitCard(hiit = hiit)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = uiState.currentFilter.uppercase(),
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                uiState.workouts.forEach { workout ->
                    WorkoutCard(workout = workout, navController = navController)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HiitCard(hiit: HiitItem) {
    KineticCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = hiit.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${hiit.durationMin} min • ${hiit.calories} kcal",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
            LimeBadge(text = "HIIT")
        }
    }
}

@Composable
fun WorkoutCard(workout: WorkoutItem, navController: NavHostController) {
    KineticCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    Screen.WorkoutDetail.createRoute(workout.id)
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workout.title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )
                LimeBadge(text = workout.difficulty.uppercase())
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = "${workout.durationMin} min",
                    color = TextMuted,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${workout.calories} kcal",
                    color = TextMuted,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = workout.category,
                    color = Lime,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
