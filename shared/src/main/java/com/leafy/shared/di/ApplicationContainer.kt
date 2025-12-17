package com.leafy.shared.di

import com.subin.leafy.domain.usecase.note.NoteUseCases

interface ApplicationContainer {
    val noteUseCases: NoteUseCases
}