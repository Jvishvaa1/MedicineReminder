package com.example.smartmedicinereminder

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MedicineDB"
        private const val DATABASE_VERSION = 3

        private const val TABLE_NAME = "medicines"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DOSAGE = "dosage"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_DOSAGE TEXT, "
                + "$COLUMN_TIME TEXT, "
                + "$COLUMN_DATE TEXT, "
                + "$COLUMN_STATUS INTEGER DEFAULT 0)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // ✅ INSERT
    fun insertMedicine(name: String, dosage: String, time: String, date: String): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put(COLUMN_NAME, name)
        values.put(COLUMN_DOSAGE, dosage)
        values.put(COLUMN_TIME, time)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_STATUS, 0)

        val id = db.insert(TABLE_NAME, null, values)
        db.close()

        return id
    }

    // ✅ GET ALL
    fun getAllMedicines(): List<Medicine> {
        val list = ArrayList<Medicine>()
        val db = readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val dosage = cursor.getString(2)
                val time = cursor.getString(3)
                val date = cursor.getString(4)
                val status = cursor.getInt(5)

                list.add(Medicine(id, name, dosage, time, date, status))

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return list
    }

    // ✅ DELETE
    fun deleteMedicine(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }

    // ✅ UPDATE STATUS
    fun updateStatus(id: Int, status: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_STATUS, status)

        db.update(TABLE_NAME, values, "id=?", arrayOf(id.toString()))
        db.close()
    }

    // ✅ MARK AS TAKEN
    fun markAsTaken(id: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_STATUS, 1)

        db.update(TABLE_NAME, values, "id=?", arrayOf(id.toString()))
        db.close()
    }

    // 📊 TAKEN COUNT
    fun getTakenCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_NAME WHERE status=1",
            null
        )

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
    }

    // ❌ MISSED COUNT
    fun getMissedCount(): Int {
        val db = readableDatabase

        val query = "SELECT COUNT(*) FROM $TABLE_NAME WHERE status=0 AND date < date('now')"

        val cursor = db.rawQuery(query, null)

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return count
    }
}