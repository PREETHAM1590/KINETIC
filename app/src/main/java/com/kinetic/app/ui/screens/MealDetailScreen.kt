package com.kinetic.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.kinetic.app.ui.viewmodels.MealDetailViewModel

@Composable
fun MealDetailScreen(
    mealId: String = "m1",
    viewModel: MealDetailViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mealId) {
        viewModel.loadMeal(mealId)
    }

    LaunchedEffect(uiState.showLoggedConfirmation) {
        if (uiState.showLoggedConfirmation) {
            snackbarHostState.showSnackbar("Meal logged successfully!")
            viewModel.dismissLoggedConfirmation()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    text = uiState.error ?: "Meal not found",
                    color = Error,
                    fontSize = 16.sp
                )
            }
        } else {
            val meal = uiState.meal ?: return@Scaffold

            var proteinProgress by remember { mutableFloatStateOf(0f) }
            var carbsProgress by remember { mutableFloatStateOf(0f) }
            var fatsProgress by remember { mutableFloatStateOf(0f) }

                val animatedProtein by animateFloatAsState(
                    targetValue = proteinProgress,
                    animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    label = "proteinProgress"
                )
                val animatedCarbs by animateFloatAsState(
                    targetValue = carbsProgress,
                    animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    label = "carbsProgress"
                )
                val animatedFats by animateFloatAsState(
                    targetValue = fatsProgress,
                    animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    label = "fatsProgress"
                )

                LaunchedEffect(meal) {
                    proteinProgress = meal.proteinPercent
                    carbsProgress = meal.carbsPercent
                    fatsProgress = meal.fatsPercent
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)
                        .verticalScroll(scrollState)
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = meal.name.uppercase(),
                            color = Lime,
                            fontSize = 40.sp,
                            lineHeight = 40.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = (-1).sp,
                            modifier = Modifier.weight(1f)
                        )
                        LimeBadge(text = meal.tag)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroStat(label = "PROTEIN", value = "${meal.protein}g")
                        MacroStat(label = "CARBS", value = "${meal.carbs}g")
                        MacroStat(label = "FATS", value = "${meal.fats}g")
                        MacroStat(label = "KCAL", value = "${meal.calories}")
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MacroRing(progress = animatedProtein, label = "PRO", color = Lime)
                        MacroRing(progress = animatedCarbs, label = "CHO", color = TextPrimary)
                        MacroRing(progress = animatedFats, label = "FAT", color = TextMuted)
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "INGREDIENTS",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-1).sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    meal.ingredients.forEach { ingredient ->
                        KineticCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = ingredient,
                                color = TextPrimary,
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "PREPARATION",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-1).sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    KineticCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            meal.steps.forEachIndexed { index, step ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        text = "${index + 1}.",
                                        color = Lime,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(24.dp)
                                    )
                                    Text(
                                        text = step,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    StartButton(
                        text = if (uiState.isMealLogged) "MEAL LOGGED" else "LOG MEAL",
                        onClick = { viewModel.logMeal() },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

@Composable
fun MacroStat(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp
        )
    }
}

@Composable
fun MacroRing(progress: Float, label: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 8.dp.toPx()
                drawArc(
                    color = Surface1,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round),
                    size = Size(size.width, size.height)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round),
                    size = Size(size.width, size.height)
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}