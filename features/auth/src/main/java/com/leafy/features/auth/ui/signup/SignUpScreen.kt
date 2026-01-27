package com.leafy.features.auth.ui.signup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.auth.ui.common.AuthButton
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyDialog
import com.leafy.shared.ui.component.LeafyEditableProfileImage
import com.leafy.shared.ui.component.LeafyTextField
import com.leafy.shared.ui.component.LoadingOverlay
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onProfileImageSelected(uri)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SignUpSideEffect.NavigateToHome -> {
                    onSignUpSuccess()
                }
                is SignUpSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is SignUpSideEffect.ShowErrorDialog -> {
                    errorDialogMessage = effect.message
                    showErrorDialog = true
                }
            }
        }
    }

    if (showErrorDialog) {
        LeafyDialog(
            onDismissRequest = { showErrorDialog = false },
            title = "회원가입 오류",
            text = errorDialogMessage,
            confirmText = "다시 시도",
            dismissText = "취소",
            onConfirmClick = {
                showErrorDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            SignUpContent(
                uiState = uiState,
                onUsernameChange = viewModel::onUsernameChanged,
                onEmailChange = viewModel::onEmailChanged,
                onPasswordChange = viewModel::onPasswordChanged,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChanged,
                onProfileImageClick = { imagePickerLauncher.launch("image/*") },
                onSignUpClick = viewModel::signUp,
                onBackClick = onBackClick
            )

            LoadingOverlay(
                isLoading = uiState.isLoading,
                message = "계정을 생성하고\n프로필을 설정하는 중입니다..."
            )
        }
    }
}
@Composable
fun SignUpContent(
    uiState: SignUpUiState,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onProfileImageClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        LeafyEditableProfileImage(
            imageUrl = uiState.profileImageUri,
            onImageClick = onProfileImageClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        LeafyTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            label = "이름 (닉네임)",
            placeholder = "앱에서 사용할 이름을 입력하세요",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(16.dp))

        LeafyTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = "이메일",
            placeholder = "name@example.com",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        LeafyTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = "비밀번호",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        val showPasswordMismatch = uiState.confirmPassword.isNotEmpty() && !uiState.isPasswordMatching

        LeafyTextField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "비밀번호 확인",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            errorMessage = if (showPasswordMismatch) "비밀번호가 일치하지 않습니다." else null
        )

        Spacer(modifier = Modifier.height(40.dp))

        AuthButton(
            text = "회원가입 완료",
            onClick = singleClick { onSignUpClick() },
            enabled = !uiState.isLoading &&
                    uiState.username.isNotBlank() &&
                    uiState.email.isNotBlank() &&
                    uiState.password.isNotBlank() &&
                    uiState.isPasswordMatching
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "이미 계정이 있나요? 로그인하기",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.clickableSingle { onBackClick() }
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    LeafyTheme {
        SignUpContent(
            uiState = SignUpUiState(),
            onUsernameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onProfileImageClick = {},
            onSignUpClick = {},
            onBackClick = {}
        )
    }
}