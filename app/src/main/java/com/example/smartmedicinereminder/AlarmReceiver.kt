package com.example.smartmedicinereminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // 🧪 TEST
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_SHORT).show()

        val name = intent.getStringExtra("name")
        val dosage = intent.getStringExtra("dosage")
        val id = intent.getIntExtra("id", -1)

        if (id == -1) return

        // 👉 Taken button intent
        val takenIntent = Intent(context, TakenReceiver::class.java)
        takenIntent.putExtra("id", id)

        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (manager == null) return

        val channelId = "medicine_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Medicine Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("💊 Time to take medicine")
            .setContentText("$name - $dosage")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.checkbox_on_background,
                "Taken",
                takenPendingIntent
            )

        manager.notify(id, builder.build())
    }
}