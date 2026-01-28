package com.leafy.features.auth.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.auth.ui.common.AuthButton
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyDialog
import com.leafy.shared.ui.component.LeafyTextField
import com.leafy.shared.ui.component.LoadingOverlay
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    var showNotImplementedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadInitialSettings()
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SignInSideEffect.NavigateToHome -> {
                    onLoginSuccess()
                }
                is SignInSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    if (showNotImplementedDialog) {
        LeafyDialog(
            onDismissRequest = { showNotImplementedDialog = false },
            title = "알림",
            text = "아이디/비밀번호 찾기 기능은 준비 중입니다.\n관리자에게 문의해주세요.",
            confirmText = "확인",
            onConfirmClick = { showNotImplementedDialog = false },
            dismissText = ""
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            SignInContent(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChanged,
                onPasswordChange = viewModel::onPasswordChanged,
                onAutoLoginChecked = viewModel::onAutoLoginChecked,
                onLoginClick = viewModel::signIn,
                onSignUpClick = onNavigateToSignUp,
                onFindAccountClick = { showNotImplementedDialog = true }
            )

            LoadingOverlay(
                isLoading = uiState.isLoading,
                message = "로그인 중입니다..."
            )
        }
    }
}

@Composable
fun SignInContent(
    uiState: SignInUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAutoLoginChecked: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onFindAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = SharedR.drawable.ic_leafy_logo),
            contentDescription = "Leafy Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        LeafyTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "아이디 (이메일)",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        LeafyTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "비밀번호",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.isAutoLogin,
                onCheckedChange = onAutoLoginChecked,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = Color.Gray
                )
            )
            Text(
                text = "로그인 상태 유지",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.clickableSingle {
                    onAutoLoginChecked(!uiState.isAutoLogin)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AuthButton(
            text = "로그인",
            onClick = singleClick { onLoginClick() },
            enabled = !uiState.isLoading && uiState.email.isNotEmpty() && uiState.password.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "아이디 찾기",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.clickableSingle { onFindAccountClick() }
            )

            VerticalDivider(
                modifier = Modifier
                    .height(12.dp)
                    .padding(horizontal = 12.dp),
                color = Color.LightGray
            )

            Text(
                text = "비밀번호 찾기",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.clickableSingle { onFindAccountClick() }
            )

            VerticalDivider(
                modifier = Modifier
                    .height(12.dp)
                    .padding(horizontal = 12.dp),
                color = Color.LightGray
            )

            Text(
                text = "회원가입",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickableSingle { onSignUpClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LeafyTheme {
        SignInContent(
            uiState = SignInUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onAutoLoginChecked = {},
            onLoginClick = {},
            onSignUpClick = {},
            onFindAccountClick = {}
        )
    }
}