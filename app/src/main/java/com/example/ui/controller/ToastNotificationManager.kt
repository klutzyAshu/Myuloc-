package com.example.ui.controller

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO,
    WARNING
}

data class ToastMessage(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val type: ToastType = ToastType.INFO,
    val durationMs: Long = 3500L,
    val actionText: String? = null,
    val onAction: (() -> Unit)? = null
)

object ToastNotificationManager {
    private val _toasts = MutableStateFlow<List<ToastMessage>>(emptyList())
    val toasts: StateFlow<List<ToastMessage>> = _toasts.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    fun showToast(
        message: String,
        type: ToastType = ToastType.INFO,
        durationMs: Long = 3500L,
        actionText: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        // Disabled entirely per user instructions
    }

    fun dismissToast(id: String) {
        _toasts.value = _toasts.value.filterNot { it.id == id }
    }
}
