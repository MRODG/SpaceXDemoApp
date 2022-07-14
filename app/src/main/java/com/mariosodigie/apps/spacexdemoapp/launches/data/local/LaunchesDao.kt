package com.mariosodigie.apps.spacexdemoapp.launches.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LaunchesDao {

    @Query("SELECT * FROM launchDetailsEntity")
    suspend fun getLaunchDetails(): List<LaunchDetailsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunchDetails(
        companyListingEntities: List<LaunchDetailsEntity>
    )

    @Query("DELETE FROM launchDetailsEntity")
    suspend fun clearLaunchDetails()
}