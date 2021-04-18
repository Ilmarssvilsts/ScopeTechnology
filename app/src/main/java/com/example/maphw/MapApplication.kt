package com.example.maphw

import android.app.Application
import com.example.maphw.data.AppDatabase
import com.example.maphw.data.repositories.UserRepository
import com.example.maphw.data.repositories.VehicleRepository

class MapApplication : Application() {

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this) }
    val ownerRepository by lazy { UserRepository(database.ownerDao()) }

    val vehicleRepository by lazy { VehicleRepository(database.vehicleDao()) }
}
