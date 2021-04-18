package com.example.maphw.data.repositories

import androidx.annotation.WorkerThread
import com.example.maphw.data.models.Vehicle
import com.example.maphw.data.models.VehicleDao
import kotlinx.coroutines.flow.Flow

class VehicleRepository(private val vehicleDao: VehicleDao) {
    val allVehicles: Flow<List<Vehicle>> = vehicleDao.getVehicles()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(vehicle: Vehicle) {
        vehicleDao.insertVehicles(vehicle)
    }
}