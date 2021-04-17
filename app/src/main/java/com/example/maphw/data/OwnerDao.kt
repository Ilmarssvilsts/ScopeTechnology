package com.example.maphw.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnerDao {
    @Query("SELECT * FROM owners")
    fun getOwners(): Flow<List<Owner>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwners(owners: Owner): Long

    @Delete
    suspend fun deleteOwners(owners: Owner)
}