package com.example.smartmedicinereminder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences; // ✅ NEW

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnAddMedicine, btnDashboard;
    FloatingActionButton fabAdd;
    RecyclerView recyclerView;
    DatabaseHelper dbHelper;
    MedicineAdapter adapter;

    // ✅ Broadcast Receiver
    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ 🔐 LOGIN CHECK (VERY IMPORTANT)
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        if (!pref.getBoolean("logged", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return; // 🔥 IMPORTANT
        }

        setContentView(R.layout.activity_main);

        // 🔔 Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101);
            }
        }

        btnAddMedicine = findViewById(R.id.btnAddMedicine);
        btnDashboard = findViewById(R.id.btnDashboard);
        fabAdd = findViewById(R.id.fabAdd);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DatabaseHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        // ✅ Broadcast receiver fix
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateReceiver,
                    new IntentFilter("MEDICINE_UPDATED"),
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(updateReceiver,
                    new IntentFilter("MEDICINE_UPDATED"));
        }

        btnAddMedicine.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddMedicineActivity.class)));

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddMedicineActivity.class)));

        btnDashboard.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DashboardActivity.class)));
    }

    private void loadData() {
        List<Medicine> list = dbHelper.getAllMedicines();

        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            btnAddMedicine.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            btnAddMedicine.setVisibility(View.GONE);
            fabAdd.setVisibility(View.VISIBLE);

            adapter = new MedicineAdapter(list, dbHelper);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateReceiver);
    }
}