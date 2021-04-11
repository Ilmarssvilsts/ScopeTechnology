package com.example.maphw.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Owner::class, Vehicle::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ownerDao(): OwnerDao
    abstract fun vehicleDao(): VehicleDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
                AppDatabase::class.java, "todo-list.db")
                .build()
    }
}