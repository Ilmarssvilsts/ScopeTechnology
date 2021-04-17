package com.example.maphw

import androidx.annotation.WorkerThread
import com.example.maphw.data.Owner
import com.example.maphw.data.OwnerDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val ownerDao: OwnerDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allUsers: List<Owner> = ownerDao.getOwners()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(owner: Owner) {
        ownerDao.insertOwners(owner)
    }
}