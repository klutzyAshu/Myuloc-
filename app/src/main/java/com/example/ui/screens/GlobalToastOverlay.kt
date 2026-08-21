package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.controller.ToastMessage
import com.example.ui.controller.ToastNotificationManager
import com.example.ui.controller.ToastType

@Composable
fun GlobalToastOverlay(
    modifier: Modifier = Modifier
) {
    val toasts by ToastNotificationManager.toasts.collectAsState()

    // Align notifications at the top center of the screen
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp)
            .windowInsetsPadding(WindowInsets.statusBars),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(toasts, key = { it.id }) { toast ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f)
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { -it }
                    ) + fadeOut()
                ) {
                    ToastCard(
                        toast = toast,
                        onDismiss = { ToastNotificationManager.dismissToast(toast.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ToastCard(
    toast: ToastMessage,
    onDismiss: () -> Unit
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    
    // Theme-compatible matching colors for types
    val (accentColor, icon, bgGradient) = when (toast.type) {
        ToastType.SUCCESS -> Triple(
            Color(0xFF10B981), // Emerald Green matching CopperGlow/AccentGreen
            Icons.Default.CheckCircle,
            if (isDark) {
                Brush.verticalGradient(listOf(Color(0xFF062217), Color(0xFF04130E)))
            } else {
                Brush.verticalGradient(listOf(Color(0xFFE6F4EA), Color(0xFFD2EBD4)))
            }
        )
        ToastType.ERROR -> Triple(
            MaterialTheme.colorScheme.error, // Syncs with M3 error color
            Icons.Default.Error,
            if (isDark) {
                Brush.verticalGradient(listOf(Color(0xFF2C0B0B), Color(0xFF1E0707)))
            } else {
                Brush.verticalGradient(listOf(Color(0xFFFCE8E6), Color(0xFFF9D5D2)))
            }
        )
        ToastType.WARNING -> Triple(
            Color(0xFFF59E0B), // Warm Amber
            Icons.Default.Warning,
            if (isDark) {
                Brush.verticalGradient(listOf(Color(0xFF291E04), Color(0xFF1A1302)))
            } else {
                Brush.verticalGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A)))
            }
        )
        ToastType.INFO -> Triple(
            MaterialTheme.colorScheme.primary, // Primary Purple - fully theme-synchronized!
            Icons.Default.Info,
            if (isDark) {
                Brush.verticalGradient(listOf(Color(0xFF0E031E), Color(0xFF06010F)))
            } else {
                Brush.verticalGradient(listOf(Color(0xFFF3E8FF), Color(0xFFE9D5FF)))
            }
        )
    }

    val textColor = if (isDark) Color.White else Color(0xFF1F2937)
    val dismissTint = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF4B5563).copy(alpha = 0.7f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .background(bgGradient)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Status Icon
            Icon(
                imageVector = icon,
                contentDescription = toast.type.name,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text message (title & detail/message)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = toast.message,
                    color = textColor,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                // Optional interactive context actions (e.g. Retry, Open, Undo)
                if (!toast.actionText.isNullOrEmpty() && toast.onAction != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = toast.actionText,
                        color = accentColor,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { toast.onAction.invoke() }
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Accessible explicit close button (minimum standard 48dp touch target)
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss Notification",
                    tint = dismissTint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
