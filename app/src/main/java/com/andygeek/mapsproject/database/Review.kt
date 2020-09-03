package com.andygeek.mapsproject.database

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "review") val review : String,
    @ColumnInfo(name = "img_review") val img_review : String?
)