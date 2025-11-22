package com.leafy.shared.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LeafyDarkColorScheme = darkColorScheme(
    primary = LeafyGreen,
    secondary = LeafyGray,
    tertiary = LeafyGreen,   // 필요에 따라 조정
)

private val LeafyLightColorScheme = lightColorScheme(
    primary = LeafyGreen,
    secondary = LeafyGray,
    tertiary = LeafyGreen,
    background = LeafyWhite,
    surface = LeafyWhite,
)

@Composable
fun LeafyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, //이거 화이트 고정 만약 다른 스크린에서 배경이 안바뀌면 여기 수정!!!
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> LeafyDarkColorScheme
        else -> LeafyLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,   // 기본 템플릿에 있던 Typography.kt 재사용
        content = content
    )
}