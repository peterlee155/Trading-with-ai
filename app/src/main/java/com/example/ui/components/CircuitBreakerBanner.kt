package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonInvalidation
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurfaceVariant

@Composable
fun CircuitBreakerBanner(
    isTriggered: Boolean,
    reason: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isTriggered,
        enter = fadeIn() + expandVertically()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CrimsonDark.copy(alpha = 0.4f))
                .border(1.dp, CrimsonInvalidation, RoundedCornerShape(12.dp))
                .padding(16.dp)
                .testTag("circuit_breaker_banner")
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Circuit Breaker Alert",
                    tint = CrimsonInvalidation,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "CIRCUIT BREAKER LOCKOUT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonInvalidation
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reason ?: "Discipline breach or emotional risk detected. Stand down to preserve capital.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cash is a valid position. Waiting is an active, disciplined decision.",
                        style = MaterialTheme.typography.labelMedium,
                        color = GoldAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun DisciplineMantraCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateSurfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("mantra_card")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Discipline Shield",
                tint = GoldAccent,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "Core Risk Axiom",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Tuition is paid. Survival is the prerequisite for compounding. Never confuse activity with progress.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
