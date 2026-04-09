package com.kinetic.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.StartButton
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.FaqItem
import com.kinetic.app.ui.viewmodels.SupportUiState
import com.kinetic.app.ui.viewmodels.SupportViewModel

@Composable
fun SupportScreen(
    viewModel: SupportViewModel = viewModel()
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
            text = "SUPPORT",
            color = TextPrimary,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "WE'RE HERE TO HELP",
            color = Lime,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Contact Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ContactOption(icon = Icons.Default.Chat, label = "LIVE CHAT")
            ContactOption(icon = Icons.Default.Email, label = "EMAIL")
            ContactOption(icon = Icons.Default.Phone, label = "CALL")
        }

        Spacer(modifier = Modifier.height(32.dp))

        when (val state = uiState) {
            is SupportUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Lime)
                }
            }
            is SupportUiState.Success -> {
                // FAQ Section
                Text(
                    text = "FAQ",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                state.faqs.forEachIndexed { index, faq ->
                    FaqCard(
                        faq = faq,
                        onToggle = { viewModel.toggleFaq(index) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Ticket
                Text(
                    text = "SUBMIT A TICKET",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (state.ticketForm.isSubmitted) {
                    KineticCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Submitted",
                                tint = Lime,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "TICKET SUBMITTED SUCCESSFULLY",
                                color = Lime,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                } else {
                    KineticCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = state.ticketForm.subject,
                                onValueChange = { viewModel.updateSubject(it) },
                                label = { Text("Subject", color = TextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Background,
                                    unfocusedContainerColor = Background,
                                    focusedBorderColor = Lime,
                                    unfocusedBorderColor = Surface2,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                enabled = !state.ticketForm.isSubmitting
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = state.ticketForm.message,
                                onValueChange = { viewModel.updateMessage(it) },
                                label = { Text("Describe your issue", color = TextMuted) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Background,
                                    unfocusedContainerColor = Background,
                                    focusedBorderColor = Lime,
                                    unfocusedBorderColor = Surface2,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !state.ticketForm.isSubmitting
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            StartButton(
                                text = if (state.ticketForm.isSubmitting) "SUBMITTING..." else "SUBMIT",
                                onClick = { viewModel.submitTicket() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.ticketForm.isSubmitting
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
            is SupportUiState.Error -> {
                Text(
                    text = state.message,
                    color = Error,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ContactOption(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Surface1)
                .clickable { /* Contact action */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Lime,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FaqCard(faq: FaqItem, onToggle: () -> Unit) {
    KineticCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (faq.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (faq.isExpanded) "Collapse" else "Expand",
                    tint = Lime
                )
            }
            AnimatedVisibility(visible = faq.isExpanded) {
                Text(
                    text = faq.answer,
                    color = TextMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
