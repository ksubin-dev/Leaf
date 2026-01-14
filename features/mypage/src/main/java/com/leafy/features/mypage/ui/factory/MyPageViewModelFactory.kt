package com.leafy.features.mypage.ui.factory
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.leafy.features.mypage.ui.MyPageViewModel
//import com.leafy.shared.di.ApplicationContainer
//
//class MyPageViewModelFactory(private val container: ApplicationContainer) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
//            return MyPageViewModel(
//                getCurrentUserIdUseCase = container.userUseCases.getCurrentUserId,
//                getUserUseCase = container.userUseCases.getUser,
//                getUserStatsUseCase = container.userUseCases.getUserStats,
//                getMonthlyRecordsUseCase = container.noteUseCases.getMonthlyRecords,
//                getBrewingInsightsUseCase = container.noteUseCases.getBrewingInsights
//            ) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}