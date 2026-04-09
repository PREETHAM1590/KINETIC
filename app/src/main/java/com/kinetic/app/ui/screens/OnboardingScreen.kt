package com.kinetic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinetic.app.ui.components.*
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.OnboardingViewModel

private val onboardingPages = listOf(
    OnboardingPageData(
        title = "KINETIC",
        subtitle = "YOUR ULTIMATE FITNESS COMPANION",
        description = "Log your workouts, track your diet, and analyze your progress with precision. Push your limits with Kinetic."
    ),
    OnboardingPageData(
        title = "TRAIN SMART",
        subtitle = "AI-POWERED WORKOUTS",
        description = "Personalized workout plans that adapt to your fitness level. Track every rep, set, and PR with precision."
    ),
    OnboardingPageData(
        title = "EAT RIGHT",
        subtitle = "SMART NUTRITION TRACKING",
        description = "Scan meals with AI, track macros effortlessly, and stay on top of your nutrition goals with real-time insights."
    )
)

private data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val description: String
)

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState) {
        if (uiState.onboardingCompleted) {
            onGetStartedClick()
        }
    }

    val currentPage = uiState.currentPage.coerceIn(0, onboardingPages.lastIndex)
    val pageData = onboardingPages.getOrElse(currentPage) { onboardingPages.first() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Hero Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Surface1),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Fitness Hero Image",
                tint = Lime.copy(alpha = 0.5f),
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        LimeBadge(text = "WELCOME TO")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = pageData.title,
            color = TextPrimary,
            fontSize = 64.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pageData.subtitle,
            color = Lime,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = pageData.description,
            color = TextMuted,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Page Indicator
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            onboardingPages.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(
                            width = if (index == currentPage) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (index == currentPage) Lime else Surface1)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (currentPage > 0) Arrangement.SpaceBetween else Arrangement.End
        ) {
            if (currentPage > 0) {
                StartButton(
                    text = "BACK",
                    onClick = { viewModel.previousPage() },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
            }

            StartButton(
                text = if (currentPage == onboardingPages.lastIndex) "GET STARTED" else "NEXT",
                onClick = {
                    if (currentPage == onboardingPages.lastIndex) {
                        viewModel.completeOnboarding()
                    } else {
                        viewModel.nextPage()
                    }
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
