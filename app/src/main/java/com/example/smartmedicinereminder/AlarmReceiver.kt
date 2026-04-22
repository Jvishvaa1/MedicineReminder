package com.example.smartmedicinereminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // 🧪 TEST
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_SHORT).show();

        String name = intent.getStringExtra("name");
        String dosage = intent.getStringExtra("dosage");
        int id = intent.getIntExtra("id", -1); // ✅ FIXED

        if (id == -1) return; // ✅ safety check

        // 👉 Taken button intent
        Intent takenIntent = new Intent(context, TakenReceiver.class);
        takenIntent.putExtra("id", id); // ✅ IMPORTANT (you already did correctly)

        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                takenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager == null) return; // ✅ FIXED

        String channelId = "medicine_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Medicine Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("💊 Time to take medicine")
                .setContentText(name + " - " + dosage)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(
                        android.R.drawable.checkbox_on_background,
                        "Taken",
                        takenPendingIntent
                );

        manager.notify(id, builder.build());
    }
}