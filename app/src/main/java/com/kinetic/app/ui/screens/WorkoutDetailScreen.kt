package com.kinetic.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinetic.app.ui.components.*
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.WorkoutDetailViewModel
import com.kinetic.app.ui.viewmodels.WorkoutDetailNavigationEvent

@Composable
fun WorkoutDetailScreen(
    workoutId: String = "w1",
    onNavigateToActiveWorkout: (String) -> Unit = {},
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is WorkoutDetailNavigationEvent.NavigateToActiveWorkout -> {
                    onNavigateToActiveWorkout(event.workoutId)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startWorkout() },
                containerColor = Lime,
                contentColor = Background,
                shape = CircleShape
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Start Workout")
            }
        },
        containerColor = Background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Lime)
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: "Workout not found",
                    color = Error,
                    fontSize = 16.sp
                )
            }
        } else {
            val workout = uiState.workout ?: return@Scaffold

            var timerProgress by remember { mutableFloatStateOf(0f) }
            val animatedProgress by animateFloatAsState(
                targetValue = timerProgress,
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                label = "timerProgress"
            )

            LaunchedEffect(Unit) {
                timerProgress = 0.75f
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = workout.title.uppercase(),
                    color = Lime,
                    fontSize = 48.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LimeBadge(text = workout.difficulty.uppercase())
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${workout.durationMin} MIN",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${workout.calories} KCAL",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(200.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 16.dp.toPx()
                            drawArc(
                                color = Surface1,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                                size = Size(size.width, size.height)
                            )
                            drawArc(
                                color = Lime,
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                                size = Size(size.width, size.height)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${workout.durationMin}",
                                color = TextPrimary,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = "MINUTES",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "EXERCISES",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-1).sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    uiState.exercises.forEach { exercise ->
                        ExerciseItem(name = exercise.name, sets = exercise.sets, reps = exercise.reps, weight = exercise.weightLbs)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

@Composable
fun ExerciseItem(name: String, sets: Int = 4, reps: Int = 10, weight: Int = 135) {
    KineticCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = name,
                color = Lime,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InputBox(label = "SETS", value = sets.toString())
                InputBox(label = "REPS", value = reps.toString())
                InputBox(label = "LBS", value = weight.toString())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBox(label: String, value: String) {
    var text by remember { mutableStateOf(value) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.width(80.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Background,
                unfocusedContainerColor = Background,
                focusedBorderColor = Lime,
                unfocusedBorderColor = Surface1,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}