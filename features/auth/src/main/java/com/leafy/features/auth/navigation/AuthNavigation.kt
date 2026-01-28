package com.leafy.features.auth.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.leafy.features.auth.ui.login.SignInScreen
import com.leafy.features.auth.ui.login.SignInViewModel
import com.leafy.features.auth.ui.signup.SignUpScreen
import com.leafy.features.auth.ui.signup.SignUpViewModel
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    onAuthSuccess: () -> Unit
) {
    navigation<MainNavigationRoute.Auth>(startDestination = AuthRoute.Login) {

        composable<AuthRoute.Login> {
            val loginViewModel: SignInViewModel = hiltViewModel()

            SignInScreen(
                viewModel = loginViewModel,
                onNavigateToSignUp = { navController.navigate(AuthRoute.SignUp) },
                onLoginSuccess = onAuthSuccess
            )
        }

        composable<AuthRoute.SignUp> {
            val signUpViewModel: SignUpViewModel = hiltViewModel()

            SignUpScreen(
                viewModel = signUpViewModel,
                onBackClick = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}