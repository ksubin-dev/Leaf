package com.leafy.features.auth.navigation

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.leafy.features.auth.ui.login.SignInScreen
import com.leafy.features.auth.ui.login.SignInViewModel
import com.leafy.features.auth.ui.login.SignInViewModelFactory
import com.leafy.features.auth.ui.signup.SignUpScreen
import com.leafy.features.auth.ui.signup.SignUpViewModel
import com.leafy.features.auth.ui.signup.SignUpViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.utils.ImageCompressor

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    container: ApplicationContainer,
    onAuthSuccess: () -> Unit
) {
    navigation<MainNavigationRoute.Auth>(startDestination = AuthRoute.Login) {

        // 1. 로그인 화면
        composable<AuthRoute.Login> {
            val loginViewModel: SignInViewModel = viewModel(
                factory = SignInViewModelFactory(
                    authUseCases = container.authUseCases,
                    noteUseCases = container.noteUseCases,
                    manageLoginSettingUseCase = container.settingUseCases.manageLoginSetting
                )
            )

            SignInScreen(
                viewModel = loginViewModel,
                onNavigateToSignUp = { navController.navigate(AuthRoute.SignUp) },
                onLoginSuccess = onAuthSuccess
            )
        }

        // 2. 회원가입 화면
        composable<AuthRoute.SignUp> {
            val context = LocalContext.current
            val imageCompressor = remember { ImageCompressor(context) }

            val signUpViewModel: SignUpViewModel = viewModel(
                factory = SignUpViewModelFactory(
                    authUseCases = container.authUseCases,
                    imageCompressor = imageCompressor
                )
            )

            SignUpScreen(
                viewModel = signUpViewModel,
                onBackClick = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.popBackStack()
//                    navController.navigate(AuthRoute.Login) {
//                        popUpTo(AuthRoute.SignUp) { inclusive = true }
//                    }
                }
            )
        }

    }
}