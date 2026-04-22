package com.example.smartmedicinereminder

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var btnAddMedicine: Button
    private lateinit var btnDashboard: Button
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: MedicineAdapter

    // ✅ Broadcast Receiver
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 LOGIN CHECK
        val pref = getSharedPreferences("user", MODE_PRIVATE)
        if (!pref.getBoolean("logged", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // 🔔 Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        btnAddMedicine = findViewById(R.id.btnAddMedicine)
        btnDashboard = findViewById(R.id.btnDashboard)
        fabAdd = findViewById(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerView)

        dbHelper = DatabaseHelper(this)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadData()

        // ✅ 🔥 FINAL FIX (important)
        val filter = IntentFilter("MEDICINE_UPDATED")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(updateReceiver, filter)
        }

        btnAddMedicine.setOnClickListener {
            startActivity(Intent(this, AddMedicineActivity::class.java))
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddMedicineActivity::class.java))
        }

        btnDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    private fun loadData() {
        val list = dbHelper.getAllMedicines()

        if (list.isEmpty()) {
            recyclerView.visibility = View.GONE
            btnAddMedicine.visibility = View.VISIBLE
            fabAdd.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            btnAddMedicine.visibility = View.GONE
            fabAdd.visibility = View.VISIBLE

            adapter = MedicineAdapter(list.toMutableList(), dbHelper)
            recyclerView.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(updateReceiver)
    }
}