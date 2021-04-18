package com.example.maphw.data

import androidx.room.*

@Entity(tableName = "owners")
data class Owner(
    @PrimaryKey @ColumnInfo(name = "id") var userId: Int,
    var name: String,
    var surname: String,
    var photo: String
)
