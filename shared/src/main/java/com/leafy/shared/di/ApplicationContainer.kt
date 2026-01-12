package com.leafy.shared.di

import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.TimerUseCases
import com.subin.leafy.domain.usecase.UserUseCases

interface ApplicationContainer {
    // 기존 UseCases
    val authUseCases: AuthUseCases
    val userUseCases: UserUseCases
    val noteUseCases: NoteUseCases
    val timerUseCases: TimerUseCases
    val communityUseCases: PostUseCases

    val imageUseCases: ImageUseCases
    val settingUseCases: SettingUseCases
}