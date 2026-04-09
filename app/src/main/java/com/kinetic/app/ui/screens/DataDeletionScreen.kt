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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.ScreenHeadline
import com.kinetic.app.ui.components.StartButton
import com.kinetic.app.ui.theme.Background
import com.kinetic.app.ui.theme.Error
import com.kinetic.app.ui.theme.TextMuted
import com.kinetic.app.ui.theme.TextPrimary
import com.kinetic.app.ui.viewmodels.SettingsViewModel

@Composable
fun DataDeletionScreen(
    onBack: () -> Unit,
    onDeleted: () -> Unit,
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
        ScreenHeadline(title = "DELETE DATA", subtitle = "REMOVE ACCOUNT & HISTORY")

        Spacer(modifier = Modifier.height(24.dp))

        KineticCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "This action permanently removes your account preferences and local app data. If you are signed in, your auth account is also deleted.",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "This cannot be undone.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
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
            text = if (uiState.isSaving) "DELETING..." else "DELETE ACCOUNT & DATA",
            onClick = { viewModel.deleteAllData(onDeleted) },
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        StartButton(
            text = "CANCEL",
            onClick = onBack,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
