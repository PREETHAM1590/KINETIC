package com.kinetic.app.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinetic.app.data.models.ChartDataPoint
import com.kinetic.app.data.models.PersonalRecord
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.ScreenHeadline
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.ReportsViewModel

@Composable
fun ReportsScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ScreenHeadline(title = "REPORTS", subtitle = "TRACK YOUR PROGRESS")

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Lime,
                trackColor = Surface2
            )
            Spacer(modifier = Modifier.height(24.dp))
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

        // Streak Card
        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak",
                    tint = Lime,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "23 DAY STREAK",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "Your longest: 31 days",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly Activity Bar Chart
        Text(
            text = "THIS WEEK",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                WorkoutBarChart(data = uiState.chartData)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // This Week Summary
        if (uiState.reports.isNotEmpty()) {
            val current = uiState.reports.first()
            KineticCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatBox(label = "WORKOUTS", value = "${current.workoutsCompleted}")
                        StatBox(label = "CALORIES", value = "${current.caloriesBurned}")
                        StatBox(label = "MINUTES", value = "${current.totalMinutes}")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "TOP EXERCISE: ${current.topExercise.uppercase()}",
                        color = Lime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Records
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Records",
                tint = Lime,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PERSONAL RECORDS",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-1).sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        uiState.personalRecords.forEach { record ->
            RecordCard(record = record)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun WorkoutBarChart(data: List<ChartDataPoint>) {
    if (data.isEmpty()) {
        Text(
            text = "No activity yet",
            color = TextMuted,
            fontSize = 14.sp
        )
        return
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val barWidth = size.width / (data.size * 2f)
        val maxVal = data.maxOfOrNull { it.value }?.coerceAtLeast(1f) ?: 1f

        data.forEachIndexed { index, point ->
            val barHeight = (point.value / maxVal) * (size.height - 30.dp.toPx())
            val x = (index * 2f + 0.5f) * barWidth

            // Bar
            drawRoundRect(
                color = if (point.value > 0f) Lime else Surface2,
                topLeft = Offset(x, size.height - barHeight - 20.dp.toPx()),
                size = Size(barWidth, barHeight.coerceAtLeast(4.dp.toPx())),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        data.forEach { point ->
            Text(
                text = point.label,
                color = if (point.value > 0f) TextPrimary else TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RecordCard(record: PersonalRecord) {
    KineticCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = record.exercise,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = record.date,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Text(
                text = record.value,
                color = Lime,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-1).sp
            )
        }
    }
}
