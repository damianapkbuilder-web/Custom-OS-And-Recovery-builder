package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloTheme

data class StepInfo(
    val stepIndex: Int,
    val title: String,
    val shortLabel: String
)

val WIZARD_STEPS = listOf(
    StepInfo(0, "Choose Device", "1. Device"),
    StepInfo(1, "Choose Recovery", "2. Recovery"),
    StepInfo(2, "Select OS", "3. OS"),
    StepInfo(3, "Select Version", "4. Version"),
    StepInfo(4, "Additional Features", "5. Features"),
    StepInfo(5, "Review & Build", "6. Build")
)

@Composable
fun WizardStepperBar(
    currentStep: Int,
    onStepSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HoloTheme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .testTag("wizard_stepper_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            WIZARD_STEPS.forEach { step ->
                val isCurrent = step.stepIndex == currentStep
                val isCompleted = step.stepIndex < currentStep

                val bgColor = when {
                    isCurrent -> if (colors.isDark) Color(0xFF1E2F3D) else Color(0xFFD9F0FA)
                    isCompleted -> if (colors.isDark) Color(0xFF182218) else Color(0xFFE8F5E9)
                    else -> if (colors.isDark) Color(0xFF1A1A1A) else Color(0xFFEEEEEE)
                }

                val borderColor = when {
                    isCurrent -> HoloBlueLight
                    isCompleted -> HoloGreenLight
                    else -> colors.border
                }

                val textColor = when {
                    isCurrent -> HoloBlueLight
                    isCompleted -> if (colors.isDark) Color(0xFFA5D6A7) else Color(0xFF2E7D32)
                    else -> colors.textSecondary
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(3.dp))
                        .clickable { onStepSelected(step.stepIndex) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("wizard_step_chip_${step.stepIndex}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = HoloGreenLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Text(
                        text = step.shortLabel,
                        fontSize = 11.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }

        // Subtitle indicator of current step title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (colors.isDark) Color(0xFF0F0F0F) else Color(0xFFF3F3F3))
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            val step = WIZARD_STEPS.getOrNull(currentStep) ?: WIZARD_STEPS.first()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "STEP ${step.stepIndex + 1} OF ${WIZARD_STEPS.size}:",
                    color = HoloBlueLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = step.title,
                    color = colors.textPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.divider)
        )
    }
}
