package com.example.smartmedicinereminder

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddMedicineActivity : AppCompatActivity() {

    private lateinit var etMedicineName: AutoCompleteTextView
    private lateinit var etDosage: AutoCompleteTextView
    private lateinit var btnPickTime: Button
    private lateinit var btnPickDate: Button
    private lateinit var btnSave: Button
    private lateinit var tvTime: TextView
    private lateinit var tvDate: TextView

    private lateinit var cbEveryday: CheckBox
    private lateinit var cbMon: CheckBox
    private lateinit var cbTue: CheckBox
    private lateinit var cbWed: CheckBox
    private lateinit var cbThu: CheckBox
    private lateinit var cbFri: CheckBox
    private lateinit var cbSat: CheckBox
    private lateinit var cbSun: CheckBox

    private var hour = -1
    private var minute = -1
    private var year = 0
    private var month = 0
    private var day = 0

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)

        try {
            etMedicineName = findViewById(R.id.etMedicineName)
            etDosage = findViewById(R.id.etDosage)
            btnPickTime = findViewById(R.id.btnPickTime)
            btnPickDate = findViewById(R.id.btnPickDate)
            btnSave = findViewById(R.id.btnSave)
            tvTime = findViewById(R.id.tvTime)
            tvDate = findViewById(R.id.tvDate)

            cbEveryday = findViewById(R.id.cbEveryday)
            cbMon = findViewById(R.id.cbMon)
            cbTue = findViewById(R.id.cbTue)
            cbWed = findViewById(R.id.cbWed)
            cbThu = findViewById(R.id.cbThu)
            cbFri = findViewById(R.id.cbFri)
            cbSat = findViewById(R.id.cbSat)
            cbSun = findViewById(R.id.cbSun)

        } catch (e: Exception) {
            Toast.makeText(this, "Layout Error: ${e.message}", Toast.LENGTH_LONG).show()
        }

        if (!::cbEveryday.isInitialized) {
            Toast.makeText(this, "Wrong layout loaded!", Toast.LENGTH_LONG).show()
        }

        dbHelper = DatabaseHelper(this)

        // Auto suggest
        val medicineList = arrayOf("Paracetamol","Panadol","Aspirin","Ibuprofen","Amoxicillin")
        etMedicineName.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, medicineList)
        )

        val dosageList = arrayOf("1 tablet","2 tablets","5 ml","10 ml","1 capsule")
        etDosage.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dosageList)
        )

        // ⏰ Time picker
        btnPickTime.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                hour = h
                minute = m
                tvTime.text = String.format("%02d:%02d", hour, minute)
            }, 12, 0, true).show()
        }

        // 📅 Date picker
        btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                year = y
                month = m
                day = d
                tvDate.text = "$d/${m + 1}/$y"
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 💾 Save
        btnSave.setOnClickListener {

            val name = etMedicineName.text.toString().trim()
            val dosage = etDosage.text.toString().trim()
            val date = tvDate.text.toString()

            if (name.isEmpty() || dosage.isEmpty() || hour == -1 || minute == -1 || date.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val insertedId = dbHelper.insertMedicine(
                name, dosage, tvTime.text.toString(), date
            )

            if (insertedId != -1L) {

                val id = insertedId.toInt()
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (cbEveryday.isChecked) {
                    setAllDays(alarmManager, id, name, dosage)
                } else {
                    setAlarmForDay(alarmManager, Calendar.MONDAY, cbMon.isChecked, id, name, dosage)
                    setAlarmForDay(alarmManager, Calendar.TUESDAY, cbTue.isChecked, id + 1, name, dosage)
                    setAlarmForDay(alarmManager, Calendar.WEDNESDAY, cbWed.isChecked, id + 2, name, dosage)
                    setAlarmForDay(alarmManager, Calendar.THURSDAY, cbThu.isChecked, id + 3, name, dosage)
                    setAlarmForDay(alarmManager, Calendar.FRIDAY, cbFri.isChecked, id + 4, name, dosage)
                    setAlarmForDay(alarmManager, Calendar.SATURDAY, cbSat.isChecked, id + 5, name, dosage)
                    setAlarmForDay(alarmManager, Calendar.SUNDAY, cbSun.isChecked, id + 6, name, dosage)
                }

                Toast.makeText(this, "Reminder Set!", Toast.LENGTH_SHORT).show()
                finish()

            } else {
                Toast.makeText(this, "Error saving!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAllDays(alarmManager: AlarmManager, id: Int, name: String, dosage: String) {
        setAlarmForDay(alarmManager, Calendar.MONDAY, true, id, name, dosage)
        setAlarmForDay(alarmManager, Calendar.TUESDAY, true, id + 1, name, dosage)
        setAlarmForDay(alarmManager, Calendar.WEDNESDAY, true, id + 2, name, dosage)
        setAlarmForDay(alarmManager, Calendar.THURSDAY, true, id + 3, name, dosage)
        setAlarmForDay(alarmManager, Calendar.FRIDAY, true, id + 4, name, dosage)
        setAlarmForDay(alarmManager, Calendar.SATURDAY, true, id + 5, name, dosage)
        setAlarmForDay(alarmManager, Calendar.SUNDAY, true, id + 6, name, dosage)
    }

    private fun setAlarmForDay(
        alarmManager: AlarmManager,
        day: Int,
        enabled: Boolean,
        requestCode: Int,
        name: String,
        dosage: String
    ) {
        if (!enabled) return

        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_WEEK, day)
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        if (c.timeInMillis <= System.currentTimeMillis()) {
            c.add(Calendar.WEEK_OF_YEAR, 1)
        }

        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("name", name)
        intent.putExtra("dosage", dosage)
        intent.putExtra("id", requestCode)

        val pi = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis,
                pi
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis,
                pi
            )
        }
    }
}