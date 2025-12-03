package com.leafy.shared.ui.theme

import androidx.compose.ui.graphics.Color

// 메인 컬러
//val LeafyGreen = Color(0xFF7A8C6F)//leafyMainGreen
//
//// 서브 컬러
//val LeafyBrown = Color(0xFF8E735B)
//val LeafyAccentRed = Color(0xFFC06E52)
//
//// 공통 베이스 컬러
//val LeafyWhite = Color(0xFFFFFFFF)
//val LeafyGray = Color(0xFFB0B6BF)
//
//// 바텀바 등 배경용 (화이트 재활용)
//val LeafyBottomBarBackground = LeafyWhite

// Material 3 Light Color Palette

// ============ Light Theme ============

// Primary: 앱의 주요 브랜드 색상 (버튼, 활성 아이콘, 주요 강조점)
val primaryLight = Color(0xFF7A8C6F)        // 메인 Primary (#7A8C6F) - 강조되는 전경색
val onPrimaryLight = Color(0xFFFFFFFF)        // primaryLight 위에 올라가는 텍스트/아이콘 색상 (대비를 위해 검은색 계열 고려 필요)
val primaryContainerLight = Color(0xFFEEF2EC)  // 살짝 더 밝은 초록 톤 - Primary와 관련된 연한 배경 (활성 탭 배경, 칩 컨테이너)
val onPrimaryContainerLight = Color(0xFF8E735B) // primaryContainerLight 위에 올라가는 텍스트/아이콘 색상

// Secondary: 보조 색상 (필터 칩, 보조 버튼, 덜 중요한 강조점)
val secondaryLight = Color(0xFF8E735B)        // 메인 Secondary (#8E735B)
val onSecondaryLight = Color(0xFFFFFFFF)       // secondaryLight 위에 올라가는 텍스트/아이콘 색상
val secondaryContainerLight = Color(0xFFE0CBB8) // 밝은 브라운 컨테이너 - Secondary와 관련된 연한 배경
val onSecondaryContainerLight = Color(0xFF2F1E10) // secondaryContainerLight 위에 올라가는 텍스트/아이콘 색상

// Tertiary: 3차 색상 (옵션, 대안, 대비를 위한 포인트 색상)
val tertiaryLight = Color(0xFFADAEBC)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFB8BCC2)
val onTertiaryContainerLight = Color(0xFF374151)

// Error: 오류/경고 상태를 나타내는 색상
val errorLight = Color(0xFFC06E52)            // 메인 에러/강조색 (#C06E52)
val onErrorLight = Color(0xFFFFFFFF)           // errorLight 위에 올라가는 텍스트/아이콘 색상
val errorContainerLight = Color(0xFFFFDAD1)    // 오류 관련 연한 컨테이너 배경
val onErrorContainerLight = Color(0xFF5C170C)  // errorContainerLight 위에 올라가는 텍스트/아이콘 색상

// Background & Surface: 배경 및 UI 요소의 표면
val backgroundLight = Color(0xFFFFFFFF)       // 앱의 가장 밑 배경색
val onBackgroundLight = Color(0xFF111827)      // backgroundLight 위에 올라가는 텍스트/아이콘 색상
val surfaceLight = Color(0xFFFFFFFF)           // UI 컴포넌트의 기본 표면색 (카드, 다이얼로그)
val onSurfaceLight = Color(0xFF4B5563)         // surfaceLight 위에 올라가는 텍스트/아이콘 색상
val surfaceVariantLight = Color(0xFFF3F4F6) // 회색카드/칩 배경용 - Primary/Secondary와 관련 없는 중립적인 표면 변형색
val onSurfaceVariantLight = Color(0xFF6B7280) // surfaceVariantLight 위에 올라가는 텍스트/아이콘 색상

// Outline: 경계선/구분선
val outlineLight = Color(0xFF9CA3AF)// 버튼, 필드 등의 경계선
val outlineVariantLight = Color(0xFFE5E7EB)    // 연한 경계선

// 기타
val scrimLight = Color(0xFF000000)             // 모달이나 팝업 뒤를 어둡게 덮는 레이어 색상 (불투명도 조절)
val inverseSurfaceLight = Color(0xFF303437)    // Light Theme에서 Dark Theme의 Surface를 일시적으로 표시할 때 (예: 툴팁)
val inverseOnSurfaceLight = Color(0xFFEEF3ED)  // inverseSurfaceLight 위에 올라가는 텍스트/아이콘 색상
val inversePrimaryLight = Color(0xFF9EB291)   // Primary의 밝은 톤 - InverseSurface 위에서 Primary 역할을 수행

// Surface Container 계열: 깊이(Elevation)에 따른 미묘한 표면 밝기 변화 (모두 FFFFFF로 통일 가능)
val surfaceDimLight = Color(0xFFD7DAD4)          // 가장 어두운 표면
val surfaceBrightLight = Color(0xFFF7F9F5)       // 가장 밝은 표면
val surfaceContainerLowestLight = Color(0xFFFFFFFF) // 깊이가 가장 낮은 표면 (앱 배경과 동일)
val surfaceContainerLowLight = Color(0xFFF3F4F6)   // 낮은 깊이의 컨테이너
val surfaceContainerLight = Color(0xFFF3F4F6)     // 중간 깊이의 컨테이너
val surfaceContainerHighLight = Color(0xFFE3E7E0)  // 높은 깊이의 컨테이너 (툴바, 큰 카드)
val surfaceContainerHighestLight = Color(0xFFDDE1DA) // 가장 높은 깊이의 컨테이너 (다이얼로그, 시트)


// ============ Dark Theme ============

// Primary (어두운 배경 위에서 살짝 더 밝게)
val primaryDark = Color(0xFFB6C6AE)          // 연한 그린 – 다크 모드에서의 강조색
val onPrimaryDark = Color(0xFF1F2A18)          // primaryDark 위에 올라가는 텍스트/아이콘 색상
val primaryContainerDark = Color(0xFF5F7156) // base(#7A8C6F)보다 약간 어두운 컨테이너
val onPrimaryContainerDark = Color(0xFFE2F0D9) // primaryContainerDark 위에 올라가는 텍스트/아이콘 색상

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
val backgroundDark = Color(0xFF11140F)       // 앱의 가장 밑 배경색 (어두운 색)
val onBackgroundDark = Color(0xFFDEE2DA)      // backgroundDark 위에 올라가는 텍스트/아이콘 색상
val surfaceDark = Color(0xFF11140F)            // UI 컴포넌트의 기본 표면색 (카드, 다이얼로그)
val onSurfaceDark = Color(0xFFDEE2DA)          // surfaceDark 위에 올라가는 텍스트/아이콘 색상
val surfaceVariantDark = Color(0xFF42483F)    // Primary/Secondary와 관련 없는 중립적인 표면 변형색
val onSurfaceVariantDark = Color(0xFFC3CBC0)  // surfaceVariantDark 위에 올라가는 텍스트/아이콘 색상

// Outline
val outlineDark = Color(0xFF8B9288)
val outlineVariantDark = Color(0xFF42483F)

// 기타
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFDEE2DA)
val inverseOnSurfaceDark = Color(0xFF2D312A)
val inversePrimaryDark = Color(0xFF7A8C6F)    // Primary의 어두운 톤 - InverseSurface 위에서 Primary 역할을 수행

// Surface Container 계열: 깊이(Elevation)에 따른 표면 밝기 변화 (다크 모드에서는 밝기가 올라갈수록 톤도 밝아짐)
val surfaceDimDark = Color(0xFF101410)           // 가장 어두운 표면
val surfaceBrightDark = Color(0xFF343833)        // 가장 밝은 표면
val surfaceContainerLowestDark = Color(0xFF090D09)  // 깊이가 가장 낮은 표면
val surfaceContainerLowDark = Color(0xFF171B16)    // 낮은 깊이의 컨테이너
val surfaceContainerDark = Color(0xFF1B211B)      // 중간 깊이의 컨테이너
val surfaceContainerHighDark = Color(0xFF252B25)   // 높은 깊이의 컨테이너 (툴바, 큰 카드)
val surfaceContainerHighestDark = Color(0xFF303630) // 가장 높은 깊이의 컨테이너 (다이얼로그, 시트)



