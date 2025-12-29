package com.leafy.features.auth.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.features.auth.ui.common.AuthButton
import com.leafy.features.auth.ui.common.AuthTextField
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // 성공 및 메시지 Flow 처리
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) onLoginSuccess()
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            // Toast 출력 후 상태 초기화
            viewModel.messageShown()
        }
    }

    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onKeepLoginChange = viewModel::onKeepLoggedInChanged,
        onLoginClick = viewModel::login,
        onSignUpClick = onNavigateToSignUp
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onKeepLoginChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 중앙 로고
        Image(
            painter = painterResource(id = SharedR.drawable.ic_leafy_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 입력 필드들
        AuthTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "아이디"
        )
        Spacer(modifier = Modifier.height(12.dp))
        AuthTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "비밀번호",
            visualTransformation = PasswordVisualTransformation()
        )

        // 로그인 상태 유지 및 체크박스
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.isKeepLoggedIn,
                onCheckedChange = onKeepLoginChange
            )
            Text(
                text = "로그인 상태 유지",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 메인 로그인 버튼
        AuthButton(
            text = "로그인",
            onClick = onLoginClick,
            enabled = !uiState.isProcessing && uiState.email.isNotEmpty() && uiState.password.isNotEmpty()
        )

        // 하단 이동 링크 바
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("아이디 찾기", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
            VerticalDivider(
                modifier = Modifier.height(12.dp).padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.tertiary
            )
            Text("비밀번호 찾기", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
            VerticalDivider(
                modifier = Modifier.height(12.dp).padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "회원가입",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable { onSignUpClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LeafyTheme {
        LoginContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onKeepLoginChange = {},
            onLoginClick = {},
            onSignUpClick = {}
        )
    }
}