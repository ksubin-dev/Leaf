package com.leafy.shared.ui.theme

import androidx.compose.ui.graphics.Color

// 메인 컬러
val LeafyGreen = Color(0xFF7A8C6F)//leafyMainGreen

// 서브 컬러
val LeafyBrown = Color(0xFF8E735B)
val LeafyAccentRed = Color(0xFFC06E52)

// 공통 베이스 컬러
val LeafyWhite = Color(0xFFFFFFFF)
val LeafyGray = Color(0xFFB0B6BF)

// 바텀바 등 배경용 (화이트 재활용)
val LeafyBottomBarBackground = LeafyWhite

// Material 3 Light Color Palette
// ============ Light Theme ============

// Primary
val primaryLight = Color(0xFF7A8C6F)        // 메인 프라이머리 (#7A8C6F)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFC1D2B7)  // 살짝 더 밝은 초록 톤
val onPrimaryContainerLight = Color(0xFF1D2A17)

// Secondary
val secondaryLight = Color(0xFF8E735B)        // 메인 세컨더리 (#8E735B)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFE0CBB8) // 밝은 브라운 컨테이너
val onSecondaryContainerLight = Color(0xFF2F1E10)

// Tertiary (보조 포인트 – 살짝 푸른 톤)
val tertiaryLight = Color(0xFF5F748D)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFCED8E6)
val onTertiaryContainerLight = Color(0xFF102238)

// Error
val errorLight = Color(0xFFC06E52)            // 메인 에러/강조색 (#C06E52)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD1)
val onErrorContainerLight = Color(0xFF5C170C)

// Background & Surface
val backgroundLight = Color(0xFFFFFFFF)       // 배경 White
val onBackgroundLight = Color(0xFF161D16)
val surfaceLight = Color(0xFFFFFFFF)      // 또는 살짝 아이보리
val onSurfaceLight = Color(0xFF161D16)
val surfaceVariantLight = Color(0xFFF0F1F4) // 회색 카드/칩 배경용
val onSurfaceVariantLight = Color(0xFF61656A) // 칩 텍스트 회색

// Outline
val outlineLight = Color(0xFF747C71)
val outlineVariantLight = Color(0xFFC3CBC0)

// 기타
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2D312A)
val inverseOnSurfaceLight = Color(0xFFEEF3ED)
val inversePrimaryLight = Color(0xFF9EB291)   // Primary의 밝은 톤

// Surface Container 계열 (담당님 스타일 맞춰서 다 만들어둠)
val surfaceDimLight = Color(0xFFD7DAD4)
val surfaceBrightLight = Color(0xFFF7F9F5)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF0F3ED)
val surfaceContainerLight = Color(0xFFEAEFE7)
val surfaceContainerHighLight = Color(0xFFE3E7E0)
val surfaceContainerHighestLight = Color(0xFFDDE1DA)


// ============ Dark Theme ============

// Primary (어두운 배경 위에서 살짝 더 밝게)
val primaryDark = Color(0xFFB6C6AE)          // 연한 그린 – 여전히 메인 톤은 초록
val onPrimaryDark = Color(0xFF1F2A18)
val primaryContainerDark = Color(0xFF5F7156) // base(#7A8C6F)보다 약간 어두운 컨테이너
val onPrimaryContainerDark = Color(0xFFE2F0D9)

// Secondary
val secondaryDark = Color(0xFFD0BCA8)
val onSecondaryDark = Color(0xFF2A1A0E)
val secondaryContainerDark = Color(0xFF6C5743)
val onSecondaryContainerDark = Color(0xFFF2DECA)

// Tertiary
val tertiaryDark = Color(0xFFB5C7E0)
val onTertiaryDark = Color(0xFF1A2C42)
val tertiaryContainerDark = Color(0xFF465A73)
val onTertiaryContainerDark = Color(0xFFD5E4F6)

// Error
val errorDark = Color(0xFFFFB4A2)
val onErrorDark = Color(0xFF5F160A)
val errorContainerDark = Color(0xFF7E2C1A)
val onErrorContainerDark = Color(0xFFFFDAD1)

// Background & Surface
val backgroundDark = Color(0xFF11140F)
val onBackgroundDark = Color(0xFFDEE2DA)
val surfaceDark = Color(0xFF11140F)
val onSurfaceDark = Color(0xFFDEE2DA)
val surfaceVariantDark = Color(0xFF42483F)
val onSurfaceVariantDark = Color(0xFFC3CBC0)

// Outline
val outlineDark = Color(0xFF8B9288)
val outlineVariantDark = Color(0xFF42483F)

// 기타
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFDEE2DA)
val inverseOnSurfaceDark = Color(0xFF2D312A)
val inversePrimaryDark = Color(0xFF7A8C6F)    // 여기서는 base primary를 반대로 사용

// Surface Container 계열
val surfaceDimDark = Color(0xFF101410)
val surfaceBrightDark = Color(0xFF343833)
val surfaceContainerLowestDark = Color(0xFF090D09)
val surfaceContainerLowDark = Color(0xFF171B16)
val surfaceContainerDark = Color(0xFF1B211B)
val surfaceContainerHighDark = Color(0xFF252B25)
val surfaceContainerHighestDark = Color(0xFF303630)