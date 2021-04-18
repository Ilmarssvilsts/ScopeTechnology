package com.example.maphw.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.maphw.data.models.Owner
import com.example.maphw.data.models.OwnerDao
import com.example.maphw.data.models.Vehicle
import com.example.maphw.data.models.VehicleDao

@Database(entities = [Owner::class, Vehicle::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ownerDao(): OwnerDao
    abstract fun vehicleDao(): VehicleDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "map_database"
                ).build()
                this.instance = instance
                // return instance
                instance
            }
        }
    }
}
