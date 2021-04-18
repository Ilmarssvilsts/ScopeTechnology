package com.example.maphw.data.repositories

import androidx.annotation.WorkerThread
import com.example.maphw.data.models.Owner
import com.example.maphw.data.models.OwnerDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val ownerDao: OwnerDao) {
    val allUsers: Flow<List<Owner>> = ownerDao.getOwners()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(owner: Owner) {
        ownerDao.insertOwners(owner)
    }
}