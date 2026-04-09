package com.kinetic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.kinetic.app.data.models.MembershipTier
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.LimeBadge
import com.kinetic.app.ui.components.ScreenHeadline
import com.kinetic.app.ui.components.StartButton
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.MembershipViewModel

@Composable
fun MembershipScreen(
    modifier: Modifier = Modifier,
    viewModel: MembershipViewModel = hiltViewModel()
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
        ScreenHeadline(title = "MEMBERSHIP", subtitle = "CHOOSE YOUR PLAN")

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Lime,
                trackColor = Surface2
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.error != null) {
            Text(
                text = uiState.error ?: "Something went wrong",
                color = Error,
                fontSize = 16.sp
            )
            return@Column
        }

        uiState.tiers.forEach { tier ->
            TierCard(tier = tier)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TierCard(tier: MembershipTier) {
    val borderColor = if (tier.isCurrent) Lime else Surface2
    val cardModifier = Modifier
        .fillMaxWidth()
        .border(
            width = if (tier.isCurrent) 2.dp else 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(16.dp)
        )

    KineticCard(modifier = cardModifier) {
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
                    text = tier.name,
                    color = if (tier.isCurrent) Lime else TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )
                if (tier.isCurrent) {
                    LimeBadge(text = "CURRENT")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tier.price,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            tier.features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Lime,
                        modifier = Modifier.width(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = feature,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
            }

            if (!tier.isCurrent) {
                Spacer(modifier = Modifier.height(16.dp))
                StartButton(
                    text = "UPGRADE",
                    onClick = { /* Upgrade action */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
