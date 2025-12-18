package com.leafy.shared.di

import com.subin.leafy.domain.usecase.note.NoteUseCases
import com.subin.leafy.domain.usecase.timer.TimerUseCases

interface ApplicationContainer {
    val noteUseCases: NoteUseCases
    val timerUseCases: TimerUseCases
}