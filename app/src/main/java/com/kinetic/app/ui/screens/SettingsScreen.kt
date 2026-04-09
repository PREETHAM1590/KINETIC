package com.kinetic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.kinetic.app.ui.components.StartButton
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    onPrivacyPolicyClick: () -> Unit = {},
    onDataDeletionClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SETTINGS",
            color = TextPrimary,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "CUSTOMIZE YOUR EXPERIENCE",
            color = Lime,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Preferences Section
        Text(
            text = "PREFERENCES",
            color = TextMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(4.dp)) {
                SettingsToggle(
                    title = "Push Notifications",
                    subtitle = "Workout reminders & updates",
                    checked = uiState.notificationsEnabled,
                    onToggle = { viewModel.toggleNotifications() }
                )
                SettingsDivider()
                SettingsToggle(
                    title = "Dark Mode",
                    subtitle = "Always-on dark theme",
                    checked = uiState.darkMode,
                    onToggle = { viewModel.toggleDarkMode() }
                )
                SettingsDivider()
                SettingsToggle(
                    title = "Metric Units",
                    subtitle = "Use kg instead of lbs",
                    checked = uiState.useMetricUnits,
                    onToggle = { viewModel.toggleMetricUnits() }
                )
                SettingsDivider()
                SettingsToggle(
                    title = "Sound Effects",
                    subtitle = "Timer & completion sounds",
                    checked = uiState.soundEffects,
                    onToggle = { viewModel.toggleSoundEffects() }
                )
            }
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Lime,
                trackColor = Surface2
            )
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.error ?: "Something went wrong",
                color = Error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Account Section
        Text(
            text = "ACCOUNT",
            color = TextMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsRow(title = "Change Password", subtitle = "Last changed 45 days ago")
                SettingsDivider()
                SettingsRow(title = "Privacy Settings", subtitle = "Control your data & visibility")
                SettingsDivider()
                SettingsRow(title = "Connected Apps", subtitle = "Apple Health, Google Fit")
                SettingsDivider()
                SettingsRow(title = "Export Data", subtitle = "Download your workout history")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // About Section
        Text(
            text = "ABOUT",
            color = TextMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "App Version", color = TextPrimary, fontSize = 14.sp)
                    Text(text = "1.0.0", color = TextMuted, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Terms of Service", color = TextPrimary, fontSize = 14.sp)
                    Text(text = "→", color = Lime, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onPrivacyPolicyClick),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Privacy Policy", color = TextPrimary, fontSize = 14.sp)
                    Text(text = "→", color = Lime, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onDataDeletionClick),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Delete My Data", color = TextPrimary, fontSize = 14.sp)
                    Text(text = "→", color = Lime, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        StartButton(
            text = if (uiState.isSaving) "WORKING..." else "LOG OUT",
            onClick = { viewModel.logout(onLogout) },
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Background,
                checkedTrackColor = Lime,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Surface2
            )
        )
    }
}

@Composable
fun SettingsRow(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = subtitle,
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SettingsDivider() {
    Divider(
        color = Surface2,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
