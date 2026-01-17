package com.subin.leafy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue // 👈 추가
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle // 👈 추가 (build.gradle에 의존성 확인)
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.ui.screen.EntryPointScreen

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val appContainer = (application as LeafyApplication).provideAppContainer()
                return MainViewModel(
                    noteUseCases = appContainer.noteUseCases,
                    userUseCases = appContainer.userUseCases,
                    authUseCases = appContainer.authUseCases,
                    manageLoginSettingUseCase = appContainer.settingUseCases.manageLoginSetting
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isSplashLoading.value || mainViewModel.startDestination.value == null
        }

        enableEdgeToEdge()

        val container = (application as LeafyApplication).provideAppContainer()
        setContent {
            LeafyTheme {
                val startDestination by mainViewModel.startDestination.collectAsStateWithLifecycle()

                if (startDestination != null) {
                    EntryPointScreen(
                        container = container,
                        startDestination = startDestination!!
                    )
                }
            }
        }
    }
}
