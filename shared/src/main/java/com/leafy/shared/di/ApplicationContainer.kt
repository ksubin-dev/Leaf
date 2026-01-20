package com.leafy.shared.di

import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.usecase.AnalysisUseCases
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.HomeUseCases
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.TimerUseCases
import com.subin.leafy.domain.usecase.UserUseCases

interface ApplicationContainer {
    // 기존 UseCases
    val authUseCases: AuthUseCases

    val homeUseCases: HomeUseCases
    val userUseCases: UserUseCases
    val noteUseCases: NoteUseCases
    val timerUseCases: TimerUseCases
    val postUseCases: PostUseCases

    val imageUseCases: ImageUseCases
    val settingUseCases: SettingUseCases

    val imageCompressor: ImageCompressor

    val analysisUseCases: AnalysisUseCases
}