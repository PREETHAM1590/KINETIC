package com.kinetic.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kinetic.app.ui.components.*
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.AICalorieScannerUiState
import com.kinetic.app.ui.viewmodels.AICalorieScannerViewModel
import com.kinetic.app.ui.viewmodels.ScanState

@Composable
fun AICalorieScannerScreen(
    viewModel: AICalorieScannerViewModel = viewModel()
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

        when (val state = uiState) {
            is AICalorieScannerUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Lime)
                }
            }
            is AICalorieScannerUiState.Success -> {
                val scanState = state.scanState
                val scanResult = state.scanResult

                // Camera Preview Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Surface1),
                    contentAlignment = Alignment.Center
                ) {
                    if (scanState is ScanState.Scanning) {
                        ScanningLineAnimation()
                    }

                    Text(
                        text = when (scanState) {
                            is ScanState.Scanning -> "CAMERA FEED ACTIVE"
                            is ScanState.Result -> "MEAL CAPTURED"
                            else -> "POINT CAMERA AT MEAL"
                        },
                        color = TextMuted,
                        fontFamily = Inter,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Scanning Status
                Text(
                    text = when (scanState) {
                        is ScanState.Scanning -> "SCANNING MEAL..."
                        is ScanState.Result -> "MEAL IDENTIFIED"
                        is ScanState.Error -> "SCAN FAILED"
                        else -> "READY TO SCAN"
                    },
                    color = Lime,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (scanState is ScanState.Idle) {
                    StartButton(
                        text = "START SCAN",
                        onClick = { viewModel.startScan() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (scanState is ScanState.Result) {
                    StartButton(
                        text = "SCAN AGAIN",
                        onClick = { viewModel.resetScan() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Scanning Results Overlay
                AnimatedVisibility(
                    visible = scanState is ScanState.Result && scanResult != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    scanResult?.let { result ->
                        KineticCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = result.meal.name,
                                        color = TextPrimary,
                                        fontSize = 20.sp,
                                        fontFamily = Inter,
                                        fontWeight = FontWeight.Bold
                                    )
                                    LimeBadge(text = if (result.meal.isHealthy) "HEALTHY" else "INDULGENT")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "${result.meal.calories} kcal",
                                    color = Lime,
                                    fontSize = 24.sp,
                                    fontFamily = Lexend,
                                    fontWeight = FontWeight.Black,
                                    fontStyle = FontStyle.Italic
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    MacroItem("Protein", "${result.meal.protein}g")
                                    MacroItem("Carbs", "${result.meal.carbs}g")
                                    MacroItem("Fats", "${result.meal.fats}g")
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                StartButton(
                                    text = "SAVE TO LOG",
                                    onClick = { /* Save action */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
            is AICalorieScannerUiState.Error -> {
                Text(
                    text = state.message,
                    color = Error,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun MacroItem(label: String, value: String) {
    Column {
        Text(text = label, color = TextMuted, fontSize = 12.sp, fontFamily = Inter)
        Text(text = value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = Inter)
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

    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val y = size.height * translateY
        drawLine(
            color = Lime,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 2.dp.toPx()
        )
    }
}
