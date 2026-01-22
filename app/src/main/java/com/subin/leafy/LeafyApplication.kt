package com.subin.leafy

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.di.ApplicationContainerProvider
import com.subin.leafy.di.ApplicationContainerImpl

class LeafyApplication : Application(), ApplicationContainerProvider, SingletonImageLoader.Factory {

    private lateinit var appContainer: ApplicationContainer

    override fun onCreate() {
        super.onCreate()
        leafyApplication = this

        setUpFirestoreCache()
        appContainer = ApplicationContainerImpl(this)
    }

    override fun provideAppContainer(): ApplicationContainer {
        return appContainer
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, COIL_MEMORY_CACHE_PERCENT)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve(COIL_CACHE_FOLDER_NAME))
                    .maxSizeBytes(COIL_DISK_CACHE_SIZE)
                    .build()
            }
            .crossfade(true)
            .build()
    }

    private fun setUpFirestoreCache() {
        try {
            val settings = firestoreSettings {
                setLocalCacheSettings(persistentCacheSettings {
                    setSizeBytes(FIRESTORE_CACHE_SIZE)
                })
            }
            Firebase.firestore.firestoreSettings = settings
        } catch (e: Exception) {

        }
    }

    companion object {
        private lateinit var leafyApplication: LeafyApplication
        fun getAppContext() = leafyApplication


        const val COIL_MEMORY_CACHE_PERCENT = 0.30
        const val COIL_DISK_CACHE_SIZE = 100L * 1024 * 1024
        const val COIL_CACHE_FOLDER_NAME = "image_cache"
        const val FIRESTORE_CACHE_SIZE = 100L * 1024 * 1024
    }
}