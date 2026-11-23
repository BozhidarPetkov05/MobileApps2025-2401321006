package com.example.autotrack.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("EXTRA_REMINDER_ID", 0)
        val title = intent.getStringExtra("EXTRA_REMINDER_TITLE") ?: "Service Reminder"
        val message = intent.getStringExtra("EXTRA_REMINDER_MESSAGE") ?: "Your vehicle service reminder is due today!"

        ReminderNotificationHelper.showReminderNotification(context, reminderId, title, message)
    }
}
