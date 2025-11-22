package com.subin.leafy

import android.app.Application

class LeafyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        leafyApplication = this
    }

    companion object{
        private lateinit var leafyApplication: LeafyApplication
        fun getAppContext() = leafyApplication
    }
}