package com.kinetic.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinetic.app.data.models.FoodScanResult
import com.kinetic.app.ui.components.*
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.AICalorieScannerViewModel
import com.kinetic.app.ui.viewmodels.ScanMode
import com.kinetic.app.ui.viewmodels.TreadmillScanResult

@Composable
fun AICalorieScannerScreen(
    viewModel: AICalorieScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "AI CALORIE SCANNER",
            color = Lime,
            fontSize = 32.sp,
            fontFamily = Lexend,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ScanModeTab(
                label = "🍎 FOOD SCAN",
                isSelected = uiState.scanMode == ScanMode.FOOD,
                onClick = { viewModel.setScanMode(ScanMode.FOOD) },
                modifier = Modifier.weight(1f)
            )
            ScanModeTab(
                label = "🏃 TREADMILL SCAN",
                isSelected = uiState.scanMode == ScanMode.TREADMILL,
                onClick = { viewModel.setScanMode(ScanMode.TREADMILL) },
                modifier = Modifier.weight(1f)
            )
        }

        // Mode-specific content
        if (uiState.scanMode == ScanMode.FOOD) {
            // Camera Preview Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Surface1),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    ScanningLineAnimation()
                }
                Text(
                    text = when {
                        uiState.isLoading -> "CAMERA FEED ACTIVE"
                        uiState.foodResult != null -> "SCAN COMPLETE"
                        else -> "POINT CAMERA AT MEAL"
                    },
                    color = TextMuted,
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = when {
                    uiState.isLoading -> "ANALYZING FOOD..."
                    uiState.error != null -> "SCAN FAILED"
                    uiState.foodResult != null -> "MEAL IDENTIFIED"
                    else -> "READY TO SCAN"
                },
                color = if (uiState.error != null) Error else Lime,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Lime)
                    }
                }
                uiState.foodResult == null -> {
                    StartButton(
                        text = "START SCAN",
                        onClick = { viewModel.startScan() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    StartButton(
                        text = "SCAN AGAIN",
                        onClick = { viewModel.reset() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            uiState.error?.let { errorMsg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMsg,
                    color = Error,
                    fontSize = 14.sp,
                    fontFamily = Inter,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = uiState.foodResult != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.foodResult?.let { result ->
                    FoodResultCard(
                        result = result,
                        isLogged = uiState.isLogged,
                        onLog = { viewModel.logMeal() }
                    )
                }
            }

        } else {
            // Treadmill mode
            Text(
                text = "📸 PHOTOGRAPH YOUR TREADMILL DISPLAY",
                color = TextMuted,
                fontFamily = Inter,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Lime)
                    }
                }
                uiState.treadmillResult == null -> {
                    StartButton(
                        text = "SCAN TREADMILL",
                        onClick = { viewModel.scanTreadmill() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    StartButton(
                        text = "SCAN AGAIN",
                        onClick = { viewModel.reset() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            uiState.error?.let { errorMsg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMsg,
                    color = Error,
                    fontSize = 14.sp,
                    fontFamily = Inter,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = uiState.treadmillResult != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.treadmillResult?.let { result ->
                    TreadmillResultCard(
                        result = result,
                        isLogged = uiState.isLogged,
                        onLog = { viewModel.logTreadmill() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ScanModeTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Surface2 else Surface1)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Lime else Surface2,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Lime else TextMuted,
            fontFamily = Lexend,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FoodResultCard(
    result: FoodScanResult,
    isLogged: Boolean,
    onLog: () -> Unit
) {
    val totalCalories = result.foods.sumOf { it.calories }
    val totalProtein = result.foods.sumOf { it.protein.toInt() }
    val totalCarbs = result.foods.sumOf { it.carbs.toInt() }
    val totalFat = result.foods.sumOf { it.fat.toInt() }

    KineticCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "FOOD DETECTED",
                color = Lime,
                fontFamily = Lexend,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            result.foods.forEach { food ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = food.name,
                            color = TextPrimary,
                            fontFamily = Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${food.portionGrams.toInt()}g",
                            color = TextMuted,
                            fontFamily = Inter,
                            fontSize = 12.sp
                        )
                    }
                    LimeBadge(text = "${food.calories} kcal")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface2)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroItem("Total", "$totalCalories kcal")
                MacroItem("Protein", "${totalProtein}g")
                MacroItem("Carbs", "${totalCarbs}g")
                MacroItem("Fat", "${totalFat}g")
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLogged) {
                Text(
                    text = "✅ Logged to Diet",
                    color = Lime,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                StartButton(
                    text = "LOG MEAL",
                    onClick = onLog,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TreadmillResultCard(
    result: TreadmillScanResult,
    isLogged: Boolean,
    onLog: () -> Unit
) {
    KineticCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "TREADMILL DETECTED",
                color = Lime,
                fontFamily = Lexend,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LimeBadge(text = "${result.speedKmh} km/h")
                LimeBadge(text = "${result.inclinePct}% incline")
                LimeBadge(text = "${result.durationMinutes} min")
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface2)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔥 ${result.estimatedCalories} cal",
                    color = Lime,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontSize = 72.sp,
                    letterSpacing = (-2).sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "~${result.estimatedSteps} steps",
                    color = TextMuted,
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLogged) {
                Text(
                    text = "✅ Workout logged!",
                    color = Lime,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                StartButton(
                    text = "LOG WORKOUT",
                    onClick = onLog,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MacroItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 11.sp, fontFamily = Inter)
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter
        )
    }
}

@Composable
fun ScanningLineAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "linePosition"
    )

    Canvas(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        val y = size.height * translateY
        drawLine(
            color = Lime,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 2.dp.toPx()
        )
    }
}
