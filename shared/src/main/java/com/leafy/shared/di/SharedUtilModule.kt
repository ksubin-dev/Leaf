package com.leafy.shared.di

import android.content.Context
import com.leafy.shared.utils.ImageCompressor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedUtilModule {

    @Provides
    @Singleton
    fun provideImageCompressor(
        @ApplicationContext context: Context
    ): ImageCompressor {
        return ImageCompressor(context)
    }
}