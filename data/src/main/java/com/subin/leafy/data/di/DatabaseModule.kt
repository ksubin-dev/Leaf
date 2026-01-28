package com.subin.leafy.data.di

import android.content.Context
import androidx.room.Room
import com.subin.leafy.data.datasource.local.room.LeafyDatabase
import com.subin.leafy.data.datasource.local.room.dao.NoteDao
import com.subin.leafy.data.datasource.local.room.dao.TeaDao
import com.subin.leafy.data.datasource.local.room.dao.TimerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLeafyDatabase(@ApplicationContext context: Context): LeafyDatabase {
        return Room.databaseBuilder(
            context,
            LeafyDatabase::class.java,
            "leafy_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideNoteDao(database: LeafyDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    fun provideTimerDao(database: LeafyDatabase): TimerDao {
        return database.timerDao()
    }

    @Provides
    fun provideTeaDao(database: LeafyDatabase): TeaDao {
        return database.teaDao()
    }
}