package com.example.maphw

import androidx.annotation.WorkerThread
import com.example.maphw.data.Vehicle
import com.example.maphw.data.VehicleDao
import kotlinx.coroutines.flow.Flow

class VehicleRepository(private val vehicleDao: VehicleDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allVehicles: Flow<List<Vehicle>> = vehicleDao.getVehicles()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(vehicle: Vehicle) {
        vehicleDao.insertVehicles(vehicle)
    }
}