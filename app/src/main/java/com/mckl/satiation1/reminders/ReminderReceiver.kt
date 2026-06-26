package com.mckl.satiation1.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mckl.satiation1.database.SatiationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val reminderType = intent.getStringExtra(EXTRA_TYPE)?.let(ReminderType::valueOf) ?: run {
            pendingResult.finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = SatiationDatabase.getDatabase(context).appSettingsDao().getSettingsOnce()
                ReminderScheduler.showReminderNotification(context, reminderType)
                when (reminderType) {
                    ReminderType.MEAL_LOGGING -> if (settings?.mealReminderEnabled == true) {
                        ReminderScheduler.scheduleNextReminder(context, reminderType, settings.mealReminderHour, settings.mealReminderMinute)
                    }
                    ReminderType.WEIGHT_LOGGING -> if (settings?.weightReminderEnabled == true) {
                        ReminderScheduler.scheduleNextReminder(context, reminderType, settings.weightReminderHour, settings.weightReminderMinute)
                    }
                    ReminderType.MACRO_CHECK_IN -> if (settings?.macroReminderEnabled == true) {
                        ReminderScheduler.scheduleNextReminder(context, reminderType, settings.macroReminderHour, settings.macroReminderMinute)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_TYPE = "reminder_type"
    }
}
