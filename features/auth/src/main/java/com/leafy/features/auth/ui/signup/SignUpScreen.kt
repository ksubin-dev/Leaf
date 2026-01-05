package com.leafy.features.auth.ui.signup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.features.auth.ui.common.AuthButton
import com.leafy.features.auth.ui.common.AuthTextField
import com.leafy.shared.common.LoadingOverlay
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // 갤러리에서 이미지 가져오기 런처
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onProfileImageSelected(uri)
    }

    // 성공 처리
    LaunchedEffect(uiState.isSignUpSuccess) {
        if (uiState.isSignUpSuccess) {
            onSignUpSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
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
            isLoading = uiState.isProcessing,
            message = "계정을 생성하고 사진을 업로드 중입니다..."
        )
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

        ProfileImagePicker(
            profileImageUri = uiState.profileImageUri,
            onImageClick = onProfileImageClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            placeholder = "이름 (닉네임)"
        )
        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "이메일 주소"
        )
        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "비밀번호",
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        AuthTextField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = "비밀번호 확인",
            visualTransformation = PasswordVisualTransformation(),
            // 비밀번호 확인란이 비어있지 않은데, 일치하지 않을 때만 에러 표시
            isError = uiState.confirmPassword.isNotEmpty() && !uiState.isPasswordMatching,
            errorMessage = "비밀번호가 일치하지 않습니다."
        )

        Spacer(modifier = Modifier.height(40.dp))
        
        AuthButton(
            text = "회원가입 완료",
            onClick = onSignUpClick,
            enabled = !uiState.isProcessing &&
                    uiState.username.isNotEmpty() &&
                    uiState.email.isNotEmpty() &&
                    uiState.password.isNotEmpty() &&
                    uiState.confirmPassword.isNotEmpty() &&
                    uiState.isPasswordMatching
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "이미 계정이 있나요? 로그인하기",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.clickable { onBackClick() }
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