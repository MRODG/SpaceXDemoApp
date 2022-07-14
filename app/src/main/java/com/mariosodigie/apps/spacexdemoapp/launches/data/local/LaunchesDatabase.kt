package com.mariosodigie.apps.spacexdemoapp.launches.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LaunchDetailsEntity::class],
    version = 1
)
abstract class LaunchesDatabase: RoomDatabase() {
    abstract val dao: LaunchesDao
}