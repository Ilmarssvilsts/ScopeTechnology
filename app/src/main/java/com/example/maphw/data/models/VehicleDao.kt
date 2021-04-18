package com.example.maphw.data.models

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    fun getVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE user_id IS :id")
    fun getVehicleById(id: Int): Flow<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: Vehicle): Long

    @Delete
    suspend fun deleteVehicles(vehicles: Vehicle)
}