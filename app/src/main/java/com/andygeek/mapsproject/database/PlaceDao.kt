package com.andygeek.mapsproject.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaceDao {
    @Query("SELECT * FROM Place")
    fun getAll() : List<Place>

    @Insert
    fun insertAll(vararg place : Place)
}