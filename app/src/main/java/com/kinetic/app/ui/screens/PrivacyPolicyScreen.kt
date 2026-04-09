package com.kinetic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.ScreenHeadline
import com.kinetic.app.ui.components.StartButton
import com.kinetic.app.ui.theme.Background
import com.kinetic.app.ui.theme.TextMuted
import com.kinetic.app.ui.theme.TextPrimary

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ScreenHeadline(title = "PRIVACY POLICY", subtitle = "HOW KINETIC USES YOUR DATA")

        Spacer(modifier = Modifier.height(24.dp))

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Kinetic collects account, workout, and nutrition information to deliver personalized coaching and progress tracking.",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "We do not sell your personal data. You can request data deletion at any time from Settings.",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Contact: privacy@kinetic.app",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        StartButton(
            text = "BACK",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
