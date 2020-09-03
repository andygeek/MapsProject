package com.andygeek.mapsproject.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaceDao {
    @Query("SELECT * FROM Place")
    fun getAll() : List<Place>

    @Insert
    fun insertAll(vararg place : Place)

    @Delete
    fun deletePlace(place: Place)

    @Query("SELECT * FROM Place WHERE place_name == :name")
    fun getPlaceByName(name: String): List<Place>
}