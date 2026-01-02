package com.leafy.features.auth.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.leafy.features.auth.ui.login.LoginScreen
import com.leafy.features.auth.ui.login.LoginViewModel
import com.leafy.features.auth.ui.login.LoginViewModelFactory
import com.leafy.features.auth.ui.signup.SignUpScreen
import com.leafy.features.auth.ui.signup.SignUpViewModel
import com.leafy.features.auth.ui.signup.SignUpViewModelFactory
import com.leafy.shared.di.ApplicationContainer

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    container: ApplicationContainer,
    onAuthSuccess: () -> Unit
) {
    navigation<AuthRouteGraph>(startDestination = AuthRoute.SignUp) {

        composable<AuthRoute.Login> {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(container.authUseCases)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToSignUp = { navController.navigate(AuthRoute.SignUp) },
                onLoginSuccess = onAuthSuccess
            )
        }

        composable<AuthRoute.SignUp> {
            val signUpViewModel: SignUpViewModel = viewModel(
                factory = SignUpViewModelFactory(container.authUseCases)
            )
            SignUpScreen(
                viewModel = signUpViewModel,
                onBackClick = { navController.navigate(AuthRoute.Login) },
                onSignUpSuccess = onAuthSuccess
            )
        }
    }
}

@kotlinx.serialization.Serializable
object AuthRouteGraph