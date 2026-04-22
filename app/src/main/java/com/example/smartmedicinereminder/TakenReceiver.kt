package com.example.smartmedicinereminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class TakenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getIntExtra("id", -1)

        if (id != -1) {

            val dbHelper = DatabaseHelper(context)

            // ✅ MARK AS TAKEN
            dbHelper.markAsTaken(id)

            // ✅ REMOVE NOTIFICATION
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            manager?.cancel(id)

            // ✅ REFRESH UI
            val updateIntent = Intent("MEDICINE_UPDATED")
            context.sendBroadcast(updateIntent)

            Toast.makeText(context, "Medicine Taken ✅", Toast.LENGTH_SHORT).show()
        }
    }
}