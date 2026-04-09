package com.kinetic.app.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.kinetic.app.data.models.Achievement
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.LimeBadge
import com.kinetic.app.ui.components.ScreenHeadline
import com.kinetic.app.ui.navigation.Screen
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    val achievements = uiState.achievements

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ScreenHeadline(title = "PROFILE", subtitle = "YOUR PERFORMANCE HUB")

        Spacer(modifier = Modifier.height(12.dp))

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

        if (profile == null) {
            return@Column
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar + Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Lime),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Background,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = profile.name.uppercase(),
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LimeBadge(text = profile.currentTier)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Since ${profile.memberSince}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileStatCard(
                value = "${profile.stats.workoutsCompleted}",
                label = "WORKOUTS",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ProfileStatCard(
                value = "${profile.stats.caloriesBurned / 1000}K",
                label = "CALORIES",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileStatCard(
                value = "${profile.stats.streakDays}",
                label = "STREAK",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ProfileStatCard(
                value = "${profile.stats.totalHours.toInt()}h",
                label = "TOTAL TIME",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quick Actions
        Text(
            text = "QUICK ACTIONS",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        QuickActionCard(
            icon = Icons.Default.CalendarMonth,
            title = "BOOK A CLASS",
            subtitle = "View schedule & reserve your spot",
            onClick = { navController.navigate(Screen.BookClass.route) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        QuickActionCard(
            icon = Icons.Default.HeadsetMic,
            title = "SUPPORT",
            subtitle = "FAQs, chat, or submit a ticket",
            onClick = { navController.navigate(Screen.Support.route) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        QuickActionCard(
            icon = Icons.Default.Settings,
            title = "SETTINGS",
            subtitle = "Notifications, units, and account",
            onClick = { navController.navigate(Screen.Settings.route) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Achievements
        Text(
            text = "ACHIEVEMENTS",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            achievements.take(3).forEach { achievement ->
                AchievementBadge(achievement = achievement)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            achievements.drop(3).forEach { achievement ->
                AchievementBadge(achievement = achievement)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    KineticCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Lime,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    KineticCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface2),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Lime,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted
            )
        }
    }
}

@Composable
fun AchievementBadge(achievement: Achievement) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (achievement.isUnlocked) Surface2 else Surface1),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = achievement.icon,
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = achievement.title,
            color = if (achievement.isUnlocked) TextPrimary else TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}
