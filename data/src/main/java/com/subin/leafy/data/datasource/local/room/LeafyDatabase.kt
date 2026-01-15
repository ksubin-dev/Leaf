package com.subin.leafy.data.datasource.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.subin.leafy.data.datasource.local.room.dao.NoteDao
import com.subin.leafy.data.datasource.local.room.dao.TeaDao
import com.subin.leafy.data.datasource.local.room.dao.TimerDao
import com.subin.leafy.data.datasource.local.room.entity.NoteEntity
import com.subin.leafy.data.datasource.local.room.entity.TeaEntity
import com.subin.leafy.data.datasource.local.room.entity.TimerPresetEntity

@Database(
    entities = [
        NoteEntity::class,
        TimerPresetEntity::class,
        TeaEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LeafyDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun timerDao(): TimerDao
    abstract fun teaDao(): TeaDao

    companion object {
        @Volatile
        private var INSTANCE: LeafyDatabase? = null

        fun getInstance(context: Context): LeafyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LeafyDatabase::class.java,
                    "leafy_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}