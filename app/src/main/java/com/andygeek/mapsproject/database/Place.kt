package com.andygeek.mapsproject.database

import androidx.databinding.adapters.Converters
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class Place(
    @PrimaryKey val place_id: String,
    @ColumnInfo(name = "place_name") val place_name : String,
    @ColumnInfo(name = "address") val addess : String?,
    @ColumnInfo(name = "image") val image : String?,
    @ColumnInfo(name = "rating") val rating : Int?,
    @ColumnInfo(name = "latitude") val latitude : Double?,
    @ColumnInfo(name = "longitude") val longitude : Double?
)