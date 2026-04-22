package com.example.smartmedicinereminder;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TakenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("id", -1);

        if (id != -1) {

            DatabaseHelper dbHelper = new DatabaseHelper(context);

            // ✅ MARK AS TAKEN IN DATABASE
            dbHelper.markAsTaken(id);

            // ✅ REMOVE NOTIFICATION
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (manager != null) {
                manager.cancel(id);
            }

            // ✅ OPTIONAL: SEND BROADCAST TO REFRESH UI (ADVANCED)
            Intent updateIntent = new Intent("MEDICINE_UPDATED");
            context.sendBroadcast(updateIntent);

            Toast.makeText(context, "Medicine Taken ✅", Toast.LENGTH_SHORT).show();
        }
    }
}