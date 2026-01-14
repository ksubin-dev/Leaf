package com.subin.leafy

import android.app.Application
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.di.ApplicationContainerProvider
import com.subin.leafy.di.ApplicationContainerImpl

class LeafyApplication : Application(), ApplicationContainerProvider {

    private lateinit var appContainer: ApplicationContainer

    override fun onCreate() {
        super.onCreate()
        leafyApplication = this
        appContainer = ApplicationContainerImpl()
    }

    override fun provideAppContainer(): ApplicationContainer {
        return appContainer
    }

    companion object {
        private lateinit var leafyApplication: LeafyApplication
        fun getAppContext() = leafyApplication
    }
}