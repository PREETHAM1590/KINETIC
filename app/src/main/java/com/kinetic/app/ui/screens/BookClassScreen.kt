package com.kinetic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
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
import com.kinetic.app.data.models.ClassItem
import com.kinetic.app.ui.components.KineticCard
import com.kinetic.app.ui.components.LimeBadge
import com.kinetic.app.ui.components.ScreenHeadline
import com.kinetic.app.ui.components.StartButton
import com.kinetic.app.ui.theme.*
import com.kinetic.app.ui.viewmodels.BookClassViewModel

@Composable
fun BookClassScreen(
    modifier: Modifier = Modifier,
    viewModel: BookClassViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Schedule view */ },
                containerColor = Lime,
                contentColor = Background,
                shape = CircleShape
            ) {
                Icon(Icons.Default.CalendarViewWeek, contentDescription = "Schedule")
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            ScreenHeadline(title = "BOOK A CLASS", subtitle = "FIND YOUR FLOW")

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

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.categories.forEach { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.setCategory(category) },
                        label = {
                            Text(
                                text = category,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Lime,
                            selectedLabelColor = Background,
                            containerColor = Surface1,
                            labelColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Class Cards
            uiState.classes.forEach { classItem ->
                ClassCard(
                    classItem = classItem,
                    onBook = { viewModel.bookClass(classItem.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ClassCard(classItem: ClassItem, onBook: () -> Unit) {
    KineticCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = classItem.name.uppercase(),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp
                )
                LimeBadge(text = classItem.category.uppercase())
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "with ${classItem.instructor}",
                color = Lime,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = classItem.time,
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${classItem.durationMin} min",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "${classItem.spotsAvailable}/${classItem.totalSpots} spots",
                    color = if (classItem.spotsAvailable <= 3) Error else TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            StartButton(
                text = "BOOK NOW",
                onClick = onBook,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
