package com.leafy.shared.di

import com.subin.leafy.domain.usecase.NoteUseCases

interface ApplicationContainerProvider {
    fun provideAppContainer(): ApplicationContainer
}