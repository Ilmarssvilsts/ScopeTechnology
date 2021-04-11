package com.example.maphw.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    fun getVehicles(): List<Vehicle>

    @Insert
    suspend fun insertVehicles(vehicles: Vehicle): Long

    @Delete
    suspend fun deleteVehicles(vehicles: Vehicle)
}