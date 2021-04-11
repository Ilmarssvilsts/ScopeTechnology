package com.example.maphw.data

import androidx.room.*

@Dao
interface OwnerDao {
    @Query("SELECT * FROM owners")
    fun getOwners(): List<Owner>

    @Insert
    suspend fun insertOwners(owners: Owner): Long

    @Delete
    suspend fun deleteOwners(owners: Owner)
}