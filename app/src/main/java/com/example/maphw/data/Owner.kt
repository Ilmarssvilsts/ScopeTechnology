package com.example.maphw.data

import androidx.room.*

@Entity(tableName = "owners")
data class Owner(
    @PrimaryKey @ColumnInfo(name = "name") var name: String
)
