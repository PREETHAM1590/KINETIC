package com.kinetic.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavHostController
import com.kinetic.app.data.models.MealItem
import com.kinetic.app.ui.components.CoachBanner
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.LimeBadge
import com.kinetic.app.ui.components.ScreenHeadline
import com.kinetic.app.ui.navigation.Screen
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.CoachViewModel
import com.kinetic.app.ui.viewmodels.DietViewModel

@Composable
fun DietScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DietViewModel = hiltViewModel(),
    coachViewModel: CoachViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topInsight by coachViewModel.topInsight.collectAsStateWithLifecycle()
    val nutrition = uiState.dailyNutrition

    // Animated calorie progress
    var targetProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "calorieProgress"
    )

    LaunchedEffect(nutrition) {
        val current = nutrition ?: return@LaunchedEffect
        val safeTargetCalories = current.targetCalories.coerceAtLeast(1)
        targetProgress = (current.totalCalories.toFloat() / safeTargetCalories.toFloat()).coerceIn(0f, 1f)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AICalorieScanner.route) },
                containerColor = Lime,
                contentColor = Background,
                shape = CircleShape
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Scan Meal")
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
            ScreenHeadline(title = "DIET", subtitle = "FUEL YOUR BODY")

            Spacer(modifier = Modifier.height(16.dp))
            
            // Show CoachBanner if there are insights
            if (topInsight != null) {
                CoachBanner(
                    insight = topInsight,
                    onActionClick = { route ->
                        navController.navigate(route)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading || nutrition == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Lime,
                        trackColor = Surface2
                    )
                }
                return@Column
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Something went wrong",
                    color = Error,
                    fontSize = 16.sp
                )
                return@Column
            }

            // Calorie Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
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
                        sweepAngle = 360f * animatedProgress.coerceIn(0f, 1f),
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${nutrition.totalCalories}",
                        color = TextPrimary,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "/ ${nutrition.targetCalories} KCAL",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Macro Bars
            KineticCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MacroBar(
                        label = "PROTEIN",
                        current = nutrition.totalProtein,
                        target = 180,
                        unit = "g"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MacroBar(
                        label = "CARBS",
                        current = nutrition.totalCarbs,
                        target = 250,
                        unit = "g"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MacroBar(
                        label = "FATS",
                        current = nutrition.totalFats,
                        target = 70,
                        unit = "g"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Today's Meals
            Text(
                text = "TODAY'S MEALS",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            uiState.meals.forEach { meal ->
                MealCard(
                    meal = meal,
                    onClick = { navController.navigate(Screen.MealDetail.createRoute(meal.id)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MacroBar(label: String, current: Int, target: Int, unit: String) {
    val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)

    var animTarget by remember { mutableFloatStateOf(0f) }
    val animProgress by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "macroBar"
    )

    LaunchedEffect(progress) {
        animTarget = progress
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "$current / $target$unit",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Lime,
            trackColor = Surface2,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun MealCard(meal: MealItem, onClick: () -> Unit) {
    KineticCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = meal.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (meal.isHealthy) {
                        Spacer(modifier = Modifier.width(8.dp))
                        LimeBadge(text = "✓")
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.mealType,
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "P: ${meal.protein}g  C: ${meal.carbs}g  F: ${meal.fats}g",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Text(
                text = "${meal.calories}",
                color = Lime,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-1).sp
            )
        }
    }
}
