package com.example.mypfeapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mypfeapplication.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    val notifications: StateFlow<List<com.example.mypfeapplication.model.AppNotification>>
            = repository.notifications

    fun markAllAsRead() = repository.markAllAsRead()

    fun unreadCount() = repository.unreadCount()
}