package com.mckl.satiation1.reminders

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mckl.satiation1.R
import com.mckl.satiation1.database.AppSettings

object ReminderScheduler {
    fun ensureNotificationChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channels = ReminderType.entries.map { type ->
            NotificationChannel(
                type.channelId,
                when (type) {
                    ReminderType.MEAL_LOGGING -> "Meal logging reminders"
                    ReminderType.WEIGHT_LOGGING -> "Weight logging reminders"
                    ReminderType.MACRO_CHECK_IN -> "Macro check-in reminders"
                },
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = type.body
            }
        }
        channels.forEach(manager::createNotificationChannel)
    }

    fun syncAllReminders(context: Context, settings: AppSettings?) {
        ensureNotificationChannels(context)
        scheduleOrCancel(context, ReminderType.MEAL_LOGGING, settings?.mealReminderEnabled == true, settings?.mealReminderHour ?: 12, settings?.mealReminderMinute ?: 30)
        scheduleOrCancel(context, ReminderType.WEIGHT_LOGGING, settings?.weightReminderEnabled == true, settings?.weightReminderHour ?: 8, settings?.weightReminderMinute ?: 0)
        scheduleOrCancel(context, ReminderType.MACRO_CHECK_IN, settings?.macroReminderEnabled == true, settings?.macroReminderHour ?: 20, settings?.macroReminderMinute ?: 0)
    }

    fun scheduleNextReminder(context: Context, type: ReminderType, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = pendingIntent(context, type)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextReminderTriggerAt(hour, minute),
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, type: ReminderType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context, type))
    }

    fun showReminderNotification(context: Context, type: ReminderType) {
        ensureNotificationChannels(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val contentIntent = PendingIntent.getActivity(
            context,
            type.requestCode + 100,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, type.channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(type.title)
            .setContentText(type.body)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(type.requestCode, notification)
    }

    private fun scheduleOrCancel(context: Context, type: ReminderType, enabled: Boolean, hour: Int, minute: Int) {
        if (enabled) {
            scheduleNextReminder(context, type, hour, minute)
        } else {
            cancelReminder(context, type)
        }
    }

    private fun pendingIntent(context: Context, type: ReminderType): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.mckl.satiation1.reminder.${type.name.lowercase()}"
            putExtra(ReminderReceiver.EXTRA_TYPE, type.name)
        }
        return PendingIntent.getBroadcast(
            context,
            type.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
