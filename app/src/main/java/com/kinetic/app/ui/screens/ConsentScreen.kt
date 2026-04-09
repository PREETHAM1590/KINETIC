package com.kinetic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.ScreenHeadline
import com.kinetic.app.ui.components.StartButton
import com.kinetic.app.ui.theme.Background
import com.kinetic.app.ui.theme.Error
import com.kinetic.app.ui.theme.Lime
import com.kinetic.app.ui.theme.Surface2
import com.kinetic.app.ui.theme.TextMuted
import com.kinetic.app.ui.theme.TextPrimary
import com.kinetic.app.ui.viewmodels.ConsentViewModel

@Composable
fun ConsentScreen(
    onConsentGiven: () -> Unit,
    viewModel: ConsentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ScreenHeadline(title = "CONSENT", subtitle = "PRIVACY & DATA USE")

        Spacer(modifier = Modifier.height(24.dp))

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Kinetic stores your workout, nutrition, and account data to personalize your fitness plan. You can review the privacy policy and request deletion anytime from Settings.",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = uiState.accepted,
                        onCheckedChange = { viewModel.setAccepted(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Lime,
                            uncheckedColor = Surface2,
                            checkmarkColor = Background
                        )
                    )
                    Text(
                        text = "I agree to data processing as described in the Privacy Policy.",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.error ?: "",
                color = Error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        StartButton(
            text = if (uiState.isSaving) "SAVING..." else "CONTINUE",
            onClick = { viewModel.saveConsent(onConsentGiven) },
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "You can withdraw consent and delete your data at any time.",
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}
