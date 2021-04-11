package com.example.maphw.data

import androidx.room.*

@Entity(
    tableName = "vehicles",
    foreignKeys = [
        ForeignKey(entity = Owner::class, parentColumns = ["id"], childColumns = ["user_id"])
    ],
    indices = [Index("user_id")]
)
data class Vehicle(
    @ColumnInfo(name = "user_id") val userId: Int,
    @PrimaryKey @ColumnInfo(name = "vehicleid") val vehicleId: Int,
    val make: String,
    val model: String,
    val year: String,
    val color: String,
    val vin: String,
    val foto: String
){

}