package com.kinetic.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinetic.app.ui.components.*
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.ActiveWorkoutUiState
import com.kinetic.app.ui.viewmodels.ActiveWorkoutViewModel

@Composable
fun ActiveWorkoutScreen(
    workoutId: String = "aw1",
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workoutId) {
        viewModel.loadWorkoutById(workoutId)
    }

    when (val state = uiState) {
        is ActiveWorkoutUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Lime)
            }
        }
        is ActiveWorkoutUiState.Success -> {
            val data = state.data
            val currentExercise = data.workout.exercises[data.currentExerciseIndex]
            val upcomingExercises = data.workout.exercises.drop(data.currentExerciseIndex + 1)

            val progress by animateFloatAsState(
                targetValue = if (data.totalTime > 0f) data.timeLeft / data.totalTime else 0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                label = "timerProgress"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = currentExercise.name.uppercase(),
                    color = Lime,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                LimeBadge(text = "SET ${data.currentSet} OF ${currentExercise.sets}")

                Spacer(modifier = Modifier.height(48.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(240.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 12.dp.toPx()
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
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round),
                            size = Size(size.width, size.height)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${data.timeLeft.toInt()}",
                            color = TextPrimary,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "SECONDS",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { if (data.reps > 0) viewModel.updateReps(data.reps - 1) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Lime)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease reps", tint = Background)
                    }

                    Text(
                        text = "${data.reps} REPS",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-1).sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    IconButton(
                        onClick = { viewModel.updateReps(data.reps + 1) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Lime)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase reps", tint = Background)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${data.caloriesBurned}",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = "KCAL BURNED",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (data.isRunning) "RUNNING" else if (data.timeLeft > 0f) "PAUSED" else "STOPPED",
                            color = Lime,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = "STATUS",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StartButton(
                        text = if (data.isRunning) "PAUSE" else "START",
                        onClick = { if (data.isRunning) viewModel.pauseTimer() else viewModel.startTimer() },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    StartButton(
                        text = "STOP",
                        onClick = { viewModel.stopTimer() },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(visible = data.isResting) {
                    KineticCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "REST ${data.restTimeLeft}s",
                                color = Lime,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                letterSpacing = (-1).sp
                            )
                            if (upcomingExercises.isNotEmpty()) {
                                Text(
                                    text = "Next: ${upcomingExercises.first().name}",
                                    color = TextPrimary,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            StartButton(
                                text = "SKIP REST",
                                onClick = { viewModel.skipRest() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "UPCOMING",
                        color = TextMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        userScrollEnabled = false
                    ) {
                        items(upcomingExercises) { exercise ->
                            KineticCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = exercise.name,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                StartButton(
                    text = "COMPLETE EXERCISE",
                    onClick = {
                        viewModel.completeExercise()
                        if (upcomingExercises.isNotEmpty()) {
                            viewModel.startRest(
                                data.workout.exercises[data.currentExerciseIndex].restSeconds
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                StartButton(
                    text = "COMPLETE SET",
                    onClick = { viewModel.completeSet() },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        is ActiveWorkoutUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.message,
                    color = Error,
                    fontSize = 16.sp
                )
            }
        }
    }
}