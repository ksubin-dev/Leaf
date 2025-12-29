package com.leafy.shared.di

import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.CommunityUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.TimerUseCases
import com.subin.leafy.domain.usecase.UserUseCases

interface ApplicationContainer {
    val noteUseCases: NoteUseCases
    val timerUseCases: TimerUseCases
    val userUseCases: UserUseCases

    val communityUseCases: CommunityUseCases

    val authUseCases: AuthUseCases


}