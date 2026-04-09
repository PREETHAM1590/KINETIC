package com.kinetic.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetic.app.data.models.CoachInsight
import com.kinetic.app.data.models.CoachInsightType
import com.kinetic.app.ui.theme.Error
import com.kinetic.app.ui.theme.Lime
import com.kinetic.app.ui.theme.Surface1
import com.kinetic.app.ui.theme.Surface2
import com.kinetic.app.ui.theme.TextPrimary

@Composable
fun CoachBanner(
    insight: CoachInsight?,
    onActionClick: (route: String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = insight != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
        modifier = modifier
    ) {
        if (insight == null) return@AnimatedVisibility

        val bannerColor = when (insight.type) {
            CoachInsightType.CELEBRATION -> Lime.copy(alpha = 0.15f)
            CoachInsightType.WARNING     -> Error.copy(alpha = 0.12f)
            CoachInsightType.NUDGE       -> Surface2
            CoachInsightType.INFO        -> Surface1
        }
        val textColor = when (insight.type) {
            CoachInsightType.CELEBRATION -> Lime
            CoachInsightType.WARNING     -> Error
            else                         -> TextPrimary
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = bannerColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = insight.message,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                if (insight.actionLabel != null && insight.actionRoute != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onActionClick(insight.actionRoute) }) {
                        Text(
                            text = insight.actionLabel,
                            color = Lime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
