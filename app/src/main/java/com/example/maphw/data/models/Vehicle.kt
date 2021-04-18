package com.example.maphw.data.models

import androidx.room.*

@Entity(tableName = "vehicles")
data class Vehicle(
    @ColumnInfo(name = "user_id") val userId: Int,
    @PrimaryKey @ColumnInfo(name = "vehicleid") val vehicleId: Int,
    val make: String,
    val model: String,
    val year: String,
    val color: String,
    val vin: String,
    val photo: String
)