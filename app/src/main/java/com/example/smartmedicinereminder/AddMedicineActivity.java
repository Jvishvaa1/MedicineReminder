package com.example.smartmedicinereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddMedicineActivity extends AppCompatActivity {

    AutoCompleteTextView etMedicineName, etDosage;
    Button btnPickTime, btnPickDate, btnSave;
    TextView tvTime, tvDate;

    CheckBox cbEveryday, cbMon, cbTue, cbWed, cbThu, cbFri, cbSat, cbSun;

    int hour = -1, minute = -1;
    int year, month, day;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        // ✅ SAFE VIEW BINDING (prevents crash)
        try {
            etMedicineName = findViewById(R.id.etMedicineName);
            etDosage = findViewById(R.id.etDosage);
            btnPickTime = findViewById(R.id.btnPickTime);
            btnPickDate = findViewById(R.id.btnPickDate);
            btnSave = findViewById(R.id.btnSave);
            tvTime = findViewById(R.id.tvTime);
            tvDate = findViewById(R.id.tvDate);

            cbEveryday = findViewById(R.id.cbEveryday);
            cbMon = findViewById(R.id.cbMon);
            cbTue = findViewById(R.id.cbTue);
            cbWed = findViewById(R.id.cbWed);
            cbThu = findViewById(R.id.cbThu);
            cbFri = findViewById(R.id.cbFri);
            cbSat = findViewById(R.id.cbSat);
            cbSun = findViewById(R.id.cbSun);

        } catch (Exception e) {
            Toast.makeText(this, "Layout Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // ✅ CHECK IF LAYOUT LOADED
        if (cbEveryday == null) {
            Toast.makeText(this, "Wrong layout loaded!", Toast.LENGTH_LONG).show();
        }

        dbHelper = new DatabaseHelper(this);

        // Auto suggest
        String[] medicineList = {"Paracetamol","Panadol","Aspirin","Ibuprofen","Amoxicillin"};
        etMedicineName.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, medicineList));

        String[] dosageList = {"1 tablet","2 tablets","5 ml","10 ml","1 capsule"};
        etDosage.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, dosageList));

        // ⏰ Time picker
        btnPickTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, h, m) -> {
                hour = h;
                minute = m;
                tvTime.setText(String.format("%02d:%02d", hour, minute));
            }, 12, 0, true).show();
        });

        // 📅 Date picker
        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                year = y;
                month = m;
                day = d;
                tvDate.setText(d + "/" + (m + 1) + "/" + y);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 💾 Save
        btnSave.setOnClickListener(v -> {

            String name = etMedicineName.getText().toString().trim();
            String dosage = etDosage.getText().toString().trim();
            String date = tvDate.getText().toString();

            if (name.isEmpty() || dosage.isEmpty() || hour == -1 || minute == -1 || date.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            long insertedId = dbHelper.insertMedicine(name, dosage,
                    tvTime.getText().toString(), date);

            if (insertedId != -1) {

                int id = (int) insertedId;
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if (cbEveryday.isChecked()) {
                    setAllDays(alarmManager, id, name, dosage);
                } else {
                    setAlarmForDay(alarmManager, Calendar.MONDAY, cbMon.isChecked(), id, name, dosage);
                    setAlarmForDay(alarmManager, Calendar.TUESDAY, cbTue.isChecked(), id+1, name, dosage);
                    setAlarmForDay(alarmManager, Calendar.WEDNESDAY, cbWed.isChecked(), id+2, name, dosage);
                    setAlarmForDay(alarmManager, Calendar.THURSDAY, cbThu.isChecked(), id+3, name, dosage);
                    setAlarmForDay(alarmManager, Calendar.FRIDAY, cbFri.isChecked(), id+4, name, dosage);
                    setAlarmForDay(alarmManager, Calendar.SATURDAY, cbSat.isChecked(), id+5, name, dosage);
                    setAlarmForDay(alarmManager, Calendar.SUNDAY, cbSun.isChecked(), id+6, name, dosage);
                }

                Toast.makeText(this, "Reminder Set!", Toast.LENGTH_SHORT).show();
                finish();

            } else {
                Toast.makeText(this, "Error saving!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAllDays(AlarmManager alarmManager, int id, String name, String dosage) {
        setAlarmForDay(alarmManager, Calendar.MONDAY, true, id, name, dosage);
        setAlarmForDay(alarmManager, Calendar.TUESDAY, true, id+1, name, dosage);
        setAlarmForDay(alarmManager, Calendar.WEDNESDAY, true, id+2, name, dosage);
        setAlarmForDay(alarmManager, Calendar.THURSDAY, true, id+3, name, dosage);
        setAlarmForDay(alarmManager, Calendar.FRIDAY, true, id+4, name, dosage);
        setAlarmForDay(alarmManager, Calendar.SATURDAY, true, id+5, name, dosage);
        setAlarmForDay(alarmManager, Calendar.SUNDAY, true, id+6, name, dosage);
    }

    private void setAlarmForDay(AlarmManager alarmManager, int day, boolean enabled,
                                int requestCode, String name, String dosage) {

        if (!enabled) return;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            c.add(Calendar.WEEK_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("name", name);
        intent.putExtra("dosage", dosage);
        intent.putExtra("id", requestCode);

        PendingIntent pi = PendingIntent.getBroadcast(
                this, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        }
    }
}