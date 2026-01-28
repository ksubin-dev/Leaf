package com.subin.leafy.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.subin.leafy.data.datasource.local.datastore.dataStore
import com.subin.leafy.data.datasource.local.datastore.timerDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    @Named("settings")
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    @Named("timer")
    fun provideTimerDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.timerDataStore
    }
}