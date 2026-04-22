package com.example.smartmedicinereminder

data class Medicine(
    val id: Int,
    val name: String,
    val dosage: String,
    val time: String,
    val date: String,
    var status: Int
)