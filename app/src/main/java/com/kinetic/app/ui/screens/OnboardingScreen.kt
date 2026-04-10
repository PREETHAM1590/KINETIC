package com.kinetic.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinetic.app.data.models.ExperienceLevel
import com.kinetic.app.data.models.FitnessGoal
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.LimeBadge
import com.kinetic.app.ui.components.StartButton
import com.kinetic.app.ui.theme.Background
import com.kinetic.app.ui.theme.Lexend
import com.kinetic.app.ui.theme.Lime
import com.kinetic.app.ui.theme.Surface1
import com.kinetic.app.ui.theme.Surface2
import com.kinetic.app.ui.theme.TextMuted
import com.kinetic.app.ui.theme.TextPrimary
import com.kinetic.app.ui.viewmodels.OnboardingUiState
import com.kinetic.app.ui.viewmodels.OnboardingViewModel

private const val TOTAL_PAGES = 5

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.onboardingCompleted) {
        if (uiState.onboardingCompleted) onGetStartedClick()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        when (uiState.currentPage) {
            0 -> WelcomePage()
            1 -> GoalPage(uiState = uiState, onGoalSelected = viewModel::selectGoal)
            2 -> ExperiencePage(uiState = uiState, onExperienceSelected = viewModel::selectExperience)
            3 -> BodyStatsPage(
                uiState = uiState,
                onHeightChange = viewModel::setHeight,
                onWeightChange = viewModel::setWeight
            )
            4 -> DonePage(uiState = uiState)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progress dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(TOTAL_PAGES) { index ->
                Box(
                    modifier = Modifier
                        .size(
                            width = if (index == uiState.currentPage) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (index == uiState.currentPage) Lime else Surface2)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (uiState.currentPage > 0) Arrangement.SpaceBetween else Arrangement.End
        ) {
            if (uiState.currentPage > 0) {
                StartButton(
                    text = "BACK",
                    onClick = viewModel::previousPage,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }
            if (uiState.currentPage < TOTAL_PAGES - 1) {
                StartButton(
                    text = "NEXT",
                    onClick = viewModel::nextPage,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (uiState.currentPage > 0) 8.dp else 0.dp)
                )
            } else {
                StartButton(
                    text = "START KINETIC",
                    onClick = viewModel::completeOnboarding,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -- Page 0: Welcome ----------------------------------------------------------

@Composable
private fun WelcomePage() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(Lime.copy(alpha = 0.12f))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Lime.copy(alpha = 0.25f))
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Lime)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        LimeBadge(text = "WELCOME TO")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "KINETIC",
            color = TextPrimary,
            fontSize = 72.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            fontFamily = Lexend,
            letterSpacing = (-2).sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "YOUR ULTIMATE\nFITNESS COMPANION",
            color = Lime,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            fontFamily = Lexend,
            letterSpacing = (-0.5).sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Log workouts, track nutrition, and analyze your progress. Built for people who push their limits.",
            color = TextMuted,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// -- Page 1: Goal -------------------------------------------------------------

private data class GoalOption(val goal: FitnessGoal, val icon: String, val label: String)

private val goalOptions = listOf(
    GoalOption(FitnessGoal.WEIGHT_LOSS,     "Fire",        "Weight Loss"),
    GoalOption(FitnessGoal.MUSCLE_GAIN,     "Muscle",      "Muscle Gain"),
    GoalOption(FitnessGoal.ENDURANCE,       "Run",         "Endurance"),
    GoalOption(FitnessGoal.REHABILITATION,  "Medical",     "Rehabilitation"),
    GoalOption(FitnessGoal.GENERAL_FITNESS, "Star",        "General Fitness")
)

@Composable
private fun GoalPage(
    uiState: OnboardingUiState,
    onGoalSelected: (FitnessGoal) -> Unit
) {
    val goals = listOf(
        Triple(FitnessGoal.WEIGHT_LOSS,     "\uD83D\uDD25", "Weight Loss"),
        Triple(FitnessGoal.MUSCLE_GAIN,     "\uD83D\uDCAA", "Muscle Gain"),
        Triple(FitnessGoal.ENDURANCE,       "\uD83C\uDFC3", "Endurance"),
        Triple(FitnessGoal.REHABILITATION,  "\uD83E\uDE7A", "Rehabilitation"),
        Triple(FitnessGoal.GENERAL_FITNESS, "\u2B50",       "General Fitness")
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        LimeBadge(text = "STEP 1 OF 4")
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "WHAT'S YOUR\nGOAL?",
            color = TextPrimary,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            fontFamily = Lexend,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose what drives you",
            color = TextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        goals.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { (goal, icon, label) ->
                    val isSelected = uiState.selectedGoal == goal
                    KineticCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .then(
                                if (isSelected)
                                    Modifier.border(2.dp, Lime, RoundedCornerShape(16.dp))
                                else
                                    Modifier
                            )
                            .clickable { onGoalSelected(goal) },
                        containerColor = if (isSelected) Surface2 else Surface1
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = label,
                                color = if (isSelected) Lime else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                if (rowItems.size < 2) Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// -- Page 2: Experience -------------------------------------------------------

@Composable
private fun ExperiencePage(
    uiState: OnboardingUiState,
    onExperienceSelected: (ExperienceLevel) -> Unit
) {
    val levels = listOf(
        Triple(ExperienceLevel.BEGINNER,     "\uD83C\uDF31", "Beginner"),
        Triple(ExperienceLevel.INTERMEDIATE, "\u26A1",        "Intermediate"),
        Triple(ExperienceLevel.ADVANCED,     "\uD83D\uDD25", "Advanced")
    )
    val descriptions = mapOf(
        ExperienceLevel.BEGINNER     to "Just getting started",
        ExperienceLevel.INTERMEDIATE to "Some experience under my belt",
        ExperienceLevel.ADVANCED     to "Training hard and consistently"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        LimeBadge(text = "STEP 2 OF 4")
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "YOUR FITNESS\nLEVEL?",
            color = TextPrimary,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            fontFamily = Lexend,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We'll tailor workouts to your experience",
            color = TextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        levels.forEach { (level, icon, label) ->
            val isSelected = uiState.experienceLevel == level
            KineticCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .then(
                        if (isSelected)
                            Modifier.border(2.dp, Lime, RoundedCornerShape(16.dp))
                        else
                            Modifier
                    )
                    .clickable { onExperienceSelected(level) },
                containerColor = if (isSelected) Surface2 else Surface1
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            color = if (isSelected) Lime else TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = descriptions[level] ?: "",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                    if (isSelected) LimeBadge(text = "Selected")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -- Page 3: Body Stats -------------------------------------------------------

@Composable
private fun BodyStatsPage(
    uiState: OnboardingUiState,
    onHeightChange: (Float) -> Unit,
    onWeightChange: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        LimeBadge(text = "STEP 3 OF 4")
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "YOUR BODY\nSTATS",
            color = TextPrimary,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            fontFamily = Lexend,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Used to calculate your calorie targets",
            color = TextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HEIGHT",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${uiState.heightCm.toInt()} cm",
                        color = Lime,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = Lexend,
                        fontStyle = FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = uiState.heightCm,
                    onValueChange = onHeightChange,
                    valueRange = 140f..220f,
                    colors = SliderDefaults.colors(
                        thumbColor = Lime,
                        activeTrackColor = Lime,
                        inactiveTrackColor = Surface2
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("140 cm", color = TextMuted, fontSize = 11.sp)
                    Text("220 cm", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WEIGHT",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${uiState.weightKg.toInt()} kg",
                        color = Lime,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = Lexend,
                        fontStyle = FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = uiState.weightKg,
                    onValueChange = onWeightChange,
                    valueRange = 40f..150f,
                    colors = SliderDefaults.colors(
                        thumbColor = Lime,
                        activeTrackColor = Lime,
                        inactiveTrackColor = Surface2
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("40 kg", color = TextMuted, fontSize = 11.sp)
                    Text("150 kg", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -- Page 4: Done -------------------------------------------------------------

@Composable
private fun DonePage(uiState: OnboardingUiState) {
    val goal = uiState.selectedGoal ?: FitnessGoal.GENERAL_FITNESS
    val calories = defaultCaloriesFor(goal, uiState.weightKg)
    val goalLabel = when (goal) {
        FitnessGoal.WEIGHT_LOSS     -> "Weight Loss"
        FitnessGoal.MUSCLE_GAIN     -> "Muscle Gain"
        FitnessGoal.ENDURANCE       -> "Endurance"
        FitnessGoal.REHABILITATION  -> "Rehabilitation"
        FitnessGoal.GENERAL_FITNESS -> "General Fitness"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Lime.copy(alpha = 0.15f))
        ) {
            Text(
                text = "\u2713",
                fontSize = 52.sp,
                color = Lime,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "YOU'RE\nALL SET!",
            color = TextPrimary,
            fontSize = 52.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            fontFamily = Lexend,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your personalized plan is ready",
            color = TextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "YOUR PLAN",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Goal", color = TextMuted, fontSize = 14.sp)
                    Text(
                        text = goalLabel,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Daily Calories", color = TextMuted, fontSize = 14.sp)
                    Text(
                        text = "$calories kcal",
                        color = Lime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = Lexend,
                        fontStyle = FontStyle.Italic
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Level", color = TextMuted, fontSize = 14.sp)
                    Text(
                        text = uiState.experienceLevel.name
                            .lowercase()
                            .replaceFirstChar { it.uppercase() },
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -- Shared helper ------------------------------------------------------------

private fun defaultCaloriesFor(goal: FitnessGoal, weightKg: Float): Int = when (goal) {
    FitnessGoal.WEIGHT_LOSS     -> (weightKg * 28).toInt().coerceIn(1400, 2000)
    FitnessGoal.MUSCLE_GAIN     -> (weightKg * 38).toInt().coerceIn(2200, 3500)
    FitnessGoal.ENDURANCE       -> (weightKg * 35).toInt().coerceIn(2000, 3000)
    FitnessGoal.REHABILITATION  -> (weightKg * 30).toInt().coerceIn(1600, 2200)
    FitnessGoal.GENERAL_FITNESS -> (weightKg * 32).toInt().coerceIn(1800, 2500)
}
