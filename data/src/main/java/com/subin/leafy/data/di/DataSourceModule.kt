package com.subin.leafy.data.di

import com.subin.leafy.data.datasource.local.impl.*
import com.subin.leafy.data.datasource.remote.auth.FirebaseAuthDataSourceImpl
import com.subin.leafy.data.datasource.remote.firestore.*
import com.subin.leafy.data.datasource.remote.storage.FirebaseStorageDataSourceImpl
import com.subin.leafy.data.datasource.local.*
import com.subin.leafy.data.datasource.remote.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    // ================= Remote DataSources =================
    @Binds
    @Singleton
    abstract fun bindAuthDataSource(impl: FirebaseAuthDataSourceImpl): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindHomeDataSource(impl: FirestoreHomeDataSourceImpl): HomeDataSource

    @Binds
    @Singleton
    abstract fun bindRemoteNoteDataSource(impl: FirestoreNoteDataSourceImpl): RemoteNoteDataSource

    @Binds
    @Singleton
    abstract fun bindPostDataSource(impl: FirestorePostDataSourceImpl): PostDataSource

    @Binds
    @Singleton
    abstract fun bindUserDataSource(impl: FirestoreUserDataSourceImpl): UserDataSource

    @Binds
    @Singleton
    abstract fun bindTeaMasterDataSource(impl: FirestoreTeaMasterDataSourceImpl): TeaMasterDataSource

    @Binds
    @Singleton
    abstract fun bindStorageDataSource(impl: FirebaseStorageDataSourceImpl): StorageDataSource

    @Binds
    @Singleton
    abstract fun bindRemoteTeaDataSource(impl: FirestoreTeaDataSourceImpl): RemoteTeaDataSource

    @Binds
    @Singleton
    abstract fun bindNotificationDataSource(impl: FirestoreNotificationDataSourceImpl): NotificationDataSource


    // ================= Local DataSources =================
    @Binds
    @Singleton
    abstract fun bindLocalNoteDataSource(impl: LocalNoteDataSourceImpl): LocalNoteDataSource

    @Binds
    @Singleton
    abstract fun bindTimerDataSource(impl: LocalTimerDataSourceImpl): TimerDataSource

    @Binds
    @Singleton
    abstract fun bindAnalysisDataSource(impl: LocalAnalysisDataSourceImpl): AnalysisDataSource

    @Binds
    @Singleton
    abstract fun bindSettingDataSource(impl: LocalSettingDataSourceImpl): LocalSettingDataSource

    @Binds
    @Singleton
    abstract fun bindLocalTeaDataSource(impl: LocalTeaDataSourceImpl): LocalTeaDataSource
}