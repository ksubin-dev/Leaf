<img src="https://github.com/user-attachments/assets/b8c57f91-211e-4290-9bda-77133455e539" width="100%">

<br>

# 🌿 Leafy (리피) — Tea Brewing Note & Community App

> 커피/와인 테이스팅 앱은 많지만 “차”를 중심으로 **브루잉 기록과 커뮤니티 경험을 함께 제공하는 앱**은 드물다는 점에서 아이디어가 시작 되었습니다.
> 
> Leafy는 사용자가 차를 기록하고, 취향을 쌓고, 다른 사람의 티 라이프를 구경하며 자연스럽게 소통할 수 있는 **차 기록 & 커뮤니티 앱**입니다.

---

## 📌 프로젝트 요약

- **목표**: 차를 즐기는 사용자가 자신의 브루잉 경험을 기록하고, 커뮤니티에서 공유/소통하며, 개인 데이터를 기반으로 취향을 돌아볼 수 있는 앱 제작
- **핵심 방향**: 기능별로 멀티모듈로 코드를 분리하고 MVVM + Clean Architecture로 역할과 책임을 명확히 나눠 앱 규모가 커져도 유지보수가 쉬운 구조로 설계
- **데이터 전략**: Room(Local) + Firestore(Remote) 하이브리드 구성으로 저장/동기화 안정성 강화

---

## ✨ 주요 기능 (Key Features)

### 1. 브루잉 노트 기록 (Brewing Note)
- 차 종류/브랜드/원산지, 레시피(온도/시간/용량), 감각 평가(점수/메모)를 **세밀하게 기록**
- 로컬(Room)과 서버(Firestore) 간 **데이터 동기화**로 기록의 지속성과 확장성을 확보했습니다.

### 2. 커뮤니티 피드 (Community Feed)
- **트렌딩 / 팔로잉** 탭 기반 커뮤니티 피드를 제공합니다.
- 게시글 좋아요/북마크/팔로우 등 상호작용과 카운트를 반영하여 사용자 참여 흐름을 구성했습니다.

### 3. 노트 → 게시글 전환 공유 (Note-to-Post Share)
- 작성한 브루잉 노트를 **커뮤니티 게시물로 연결/전환**하여 손쉽게 공유할 수 있습니다.
- 노트 기반 공유를 통해 기록과 커뮤니티가 분리되지 않고 자연스럽게 이어지도록 설계했습니다.

### 4. 티 타이머 (Tea Timer)
- 차 종류별 프리셋을 제공하고, 사용자가 **커스텀 타이머를 저장**할 수 있습니다.
- 타이머 사용성을 높이기 위한 옵션(알림/진동 등)을 지원합니다.

### 5. 마이페이지 & 분석 (My Page & Insights)
- 프로필/찻장(보유 차 목록)/활동 기반 분석을 제공합니다.
- 사용자의 음용/기록 데이터를 **차트 기반으로 시각화**하여 직관적인 피드백을 제공합니다. (MPAndroidChart)

### 6. 인증 & 계정 (Auth & Account)
- Firebase Auth 기반 이메일 로그인/회원가입과 세션 유지 로직을 구현했습니다.
- 프로필 정보 관리 및 사용자 상태 기반 화면 흐름을 구성했습니다.

---

## 📱 주요 기능 시연

<div id="auth-start"></div>

### 1️⃣ 시작하기 & 인증 (Auth)
앱의 첫인상인 스플래시 화면과 안전한 사용자 인증 과정입니다.

| 🔐 스플래시 & 회원가입 | 👤 로그인 & 홈 진입 |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/05533a19-07b5-44ab-8a7c-735232ec1791" width="280"> | <img src="https://github.com/user-attachments/assets/abe3e01f-824e-4ef5-a174-dc6d0405de6c" width="280"> |
| 앱 실행 및 이메일 회원가입 프로세스 | 로그인 및 홈 화면 전환 |

<div id="tea-cabinet"></div>

### 2️⃣ 나만의 찻장 (Tea Cabinet)
보유한 차를 등록하고 찻장에서 바로 시음 노트를 작성할 수 있습니다.

| 🗄️ 찻장 등록 & 찻장에서 기록하기 |
| :---: |
| <img src="https://github.com/user-attachments/assets/d5b52b83-c191-41da-8f58-1b6b14d85830" width="280"> |
| 차의 상세 정보 등록 및 바로 기록 |

<div id="tea-timer"></div>

### 3️⃣ 티 타이머 & 다회 우림 (Tea Timer)
차의 맛을 위한 최적의 시간을 제공하며 여러 번 우린 기록을 한 번에 저장합니다.

| ⏱️ 타이머 설정 및 1회 우림 | 🔄 2회 우림 및 기록 저장 |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/5737f1b8-856f-4bc7-90a5-6665a2622fef" width="280"> | <img src="https://github.com/user-attachments/assets/15187a6c-2e82-405c-9aee-1260a0e32275" width="280"> |
| 온도/시간 프리셋 설정 및 타이머 작동 | 재우림(Infusion) 횟수 반영 및 노트 작성 연동 |

<div id="brewing-note"></div>

### 4️⃣ 브루잉 노트 작성 및 관리 (Brewing Note)
하단 네비게이션을 통해 언제든 기록을 시작하고 작성된 노트를 수정하여 관리할 수 있습니다.

| 📝 새 노트 작성 (네비게이션) | ✏️ 기존 노트 수정 |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/b77faf7b-ab4e-4da7-a7e3-1129c0f6d59a" width="280"> | <img src="https://github.com/user-attachments/assets/cd88b196-9fa0-4659-9978-84c404d53701" width="280"> |
| 네비게이션 '노트' 탭을 통해 즉시 기록 작성 진입 | 기존 데이터를 유지한 채 수정 모드 전환 및 업데이트 |

<div id="community-feed"></div>

### 5️⃣ 커뮤니티 피드 & 탐색 (Community Feed)
트렌드에 맞는 인기 노트를 구경하고 취향이 맞는 티 마스터를 팔로우하여 모아볼 수 있습니다.

| 🔥 이번 주 인기 노트 | 🏆 명예의 전당 |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/038edb8a-074c-4c5f-aa9d-2f58ac07eae2" width="280"> | <img src="https://github.com/user-attachments/assets/ab301172-9f15-4675-8c2d-a1db87074f4b" width="280"> |
| 주간 조회수 및 좋아요 기반 인기 게시글 랭킹 | 역대 가장 많은 사랑을 받은 명예의 전당 노트 |

| 👥 팔로우 추천 및 팔로잉 탭 | ❤️ 좋아요/북마크 & 상태 동기화 |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/1654587f-61fc-4884-b07a-bfc713408047" width="280"> | <img src="https://github.com/user-attachments/assets/3fb2aa29-ca43-4a3b-ba71-cf7727238466" width="280"> |
| 추천 유저 팔로우 및 팔로잉 피드 모아보기 | 낙관적 UI 적용 및 원본 노트-게시글 간 상태 동기화 |

<div id="social-actions"></div>

### 6️⃣ 게시글 작성 & 관리 (Social Actions)
나의 시음 노트를 커뮤니티에 공유하거나 댓글로 소통하며 작성한 글을 관리할 수 있습니다.

| 📝 원본 노트로 게시글 작성 | 💬 댓글 작성 및 소통 |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/84cc42e0-9e3e-42db-8905-1401d430ae0f" width="280"> | <img src="https://github.com/user-attachments/assets/233ea28e-c070-4a40-b1f1-a5f0bf9bbc07" width="280"> |
| 내 시음 노트를 불러와 바로 게시글로 공유 | 다른 유저의 글에 댓글을 남기며 취향 소통 |

| 🗑️ 원본 노트 연동 삭제 | ❌ 일반 게시글 삭제 |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/e99d3650-5512-4b96-9f12-000a9ed41bfc" width="280"> | <img src="https://github.com/user-attachments/assets/22a5ba2c-ba74-4d58-b302-f3206d9dbb33" width="280"> |
| 원본 노트 삭제 시 공유된 게시글도 함께 정리 | 커뮤니티에 작성한 게시글 단독 삭제 처리 |

<div id="my-page"></div>

### 7️⃣ 마이페이지 (My Page)
나의 티 라이프를 한눈에 모아보고 데이터를 통해 나의 취향을 발견합니다.

| 📊 캘린더·분석·검색·보관함 (통합 시연) | ⚙️ 프로필 수정 및 설정 |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/e8491df8-beda-40c0-83f2-f68ba50e2baa" width="280"> | <img src="https://github.com/user-attachments/assets/29a54de9-ab76-4b5f-bc0d-a3193e73c65a" width="280"> |
| 캘린더 기록 조회, 취향 분석 리포트, 내 노트 검색, 좋아요/북마크 보관함 기능을 한 번에 확인 | 닉네임, 프로필 사진 변경 및 앱 설정 관리 |

<div id="home-search"></div>

### 8️⃣ 홈 (Home)
통합 검색을 통해 원하는 정보를 찾고 내 소식을 빠르게 확인합니다.

| 🔍 통합 검색 (Search) | 🔔 알림 센터 (Notification) |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/d5cbfcac-7e7f-4af0-8f30-3dd16fc3d32a" width="280"> | <img src="https://github.com/user-attachments/assets/4c42df04-e3c8-4873-993c-3398907a5060" width="280"> |
| 게시글, 사용자 닉네임 검색 | 내 활동에 대한 반응(좋아요, 댓글 ) 알림 수신 |


---


## 🛠 기술 스택 (Tech Stack)

| Category | Tech | Usage |
| :--- | :--- | :--- |
| Language | Kotlin | 프로젝트 전체 |
| UI | Jetpack Compose (Material3) | 선언형 UI 및 상태 기반 렌더링 |
| Architecture | MVVM + Clean Architecture | 계층 분리 및 확장성 확보 |
| Async | Coroutines, Flow | 반응형 상태 스트림, `combine`, `stateIn` |
| Data | Room (Local), Firestore (Remote) | 하이브리드 저장/동기화 |
| Auth | Firebase Auth | 로그인/세션 관리 |
| DI | Hilt | 의존성 주입 및 결합도 감소 |
| Background | WorkManager | 백그라운드 작업 보장 |
| Image | Coil | 이미지 로딩 및 캐싱 최적화 |
| Chart | MPAndroidChart | 음용/활동 데이터 시각화 |

---

## 🧱 아키텍처 & 모듈 구조 (Architecture & Modules)

### MVVM + Clean Architecture
- **구조**: `features(UI)` → `domain(UseCase/Model)` ← `data(Repository/DTO/DataSource)`
- Domain은 **순수 Kotlin 모듈**로 구성하여 비즈니스 로직의 독립성을 보장했습니다.
- Data는 DTO/Mapper/Repository를 통해 도메인 계층과의 결합도를 낮췄습니다.

### Multi-module 구성
- `app`: 엔트리 포인트 및 전체 조립
- `features`: 화면/뷰모델/상태(UiState) 및 UI 로직
- `domain`: Model, UseCase 등 비즈니스 규칙
- `data`: Firestore/Room 연동, DTO/Mapper, Repository
- `shared`: 공통 UI 및 유틸/네비게이션 등 재사용 모듈

### Shared 모듈 설계 의도
Shared 모듈은 여러 feature에서 공통으로 사용하는 요소를 모아 **중복 제거 및 재사용성**을 높였습니다.
- `ui`: 공통 컴포넌트/테마/UiText 등 UI 기반 공통 자원
- `navigation`: 라우트 정의 및 네비게이션 엔트리
- `utils`: 이미지 압축, 시간/포맷, 화면 유지 등 공통 유틸리티
- `common`: 자주 쓰는 확장/헬퍼

---

## 🔥 핵심 구현 포인트 (Key Contributions)

### 1) 반응형 상태 파이프라인 구축 (Flow 기반 UiState)
- **문제**: 여러 데이터 소스(트렌딩/팔로잉/북마크/유저 상태 등)를 각각 구독하면서 로딩/상태 불일치 및 Race Condition 발생
- **해결**: `combine`으로 데이터 스트림을 하나의 `UiState`로 병합하고 `stateIn`을 통해 Hot Stream으로 관리
- **효과**: 데이터 로드 시점에 상관없이 동기화된 UI 상태 유지 + 불필요한 리렌더 감소

### 2) Kotlin Flow `combine` 인자 제한(5개) 해결
- **문제**: 화면 데이터 소스가 8개 이상으로 증가하며 기본 `combine` 오버로드 제한으로 컴파일 오류 발생
- **해결**: Flow 리스트를 묶어 처리하는 **커스텀 combine 확장 함수(Arity-8)** 구현
- **효과**: 구조 확장 시 라이브러리 제약에 묶이지 않는 상태 관리 기반 확보

### 3) 낙관적 업데이트(Optimistic Update)로 UX 개선
- **문제**: 좋아요/북마크 클릭 시 서버 응답 대기 동안 UI 반응이 늦어지는 문제
- **해결**: 서버 응답을 기다리지 않고 로컬 상태를 즉시 갱신, 실패 시 롤백
- **효과**: 네트워크 지연이 있어도 체감 반응이 빠른 인터랙션 제공

### 4) WorkManager 도입 — 작성 흐름 안정화(백그라운드 저장/동기화)
- **문제**: 게시글/기록 작성 도중 홈키 전환 등으로 앱이 백그라운드로 내려갈 때 저장이 끊기거나 유실되는 사용자 경험 이슈
- **해결**: WorkManager로 저장/동기화 작업을 백그라운드로 분리하여 앱 상태 변화에도 작업을 보장
- **효과**: 작성 흐름이 안정적으로 유지되어 사용자 편의성이 향상됨

### 5) Firestore 데이터 매핑 안정화
- **문제**: Firestore 필드명과 DTO 간 매핑 충돌 가능성 및 데이터 통신 오류
- **해결**: `@PropertyName`을 활용해 필드명을 명시적으로 매핑하고 통신 안정성을 확보
- **효과**: 데이터 정합성 개선 및 런타임 오류 리스크 감소

---


## 🧯 트러블 슈팅 (Troubleshooting)

<details>
<summary><b>1. 앱을 닫아도 끊김 없는 업로드 (WorkManager)</b></summary>
<br>

* **문제:** 기존에는 업로드 작업이 UI 생명주기(`viewModelScope`)에 종속되어 있어 사용자가 업로드 중 **앱을 이탈하거나(홈키) 화면을 닫으면 작업이 강제 취소되어 데이터가 유실**되는 치명적인 문제가 있었습니다.
* **해결:** `WorkManager`를 도입하여, 업로드 작업을 백그라운드로 옮겼습니다. 이제 화면이 꺼지거나 다른 앱을 켜도 작업이 멈추지 않습니다.
* **결과:** 사용자는 로딩 화면에서 기다릴 필요 없이 자유롭게 다른 일을 할 수 있고 업로드 성공률도 보장됩니다.

| ❌ 개선 전 (앱 이탈 시 업로드 실패) | ✅ 개선 후 (백그라운드 업로드 보장) |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/ffa7e58e-709f-41af-be19-3834e3485442" width="300"> | <img src="https://github.com/user-attachments/assets/9d583a15-5ebc-4ce7-ade9-1efc5728a768" width="300"> |

</details>

<details>
<summary><b>2. 삭제 피드백 UX 개선 (Snackbar → Toast)</b></summary>
<br>

* **문제:** 게시글 삭제 시 `Snackbar`를 사용하면 메시지가 떠 있는 동안 삭제된 게시글 화면이 계속 유지되어, 사용자에게 "정말 삭제된 것이 맞나?" 하는 혼란을 줄 수 있었습니다.
* **해결:** 화면 종속적인 `Snackbar` 대신 `Toast` 메시지를 사용하여, 삭제 즉시 화면을 닫고 목록으로 이동시켜 삭제가 완료되었음을 직관적으로 인지하도록 개선했습니다.

| ❌ 개선 전 (삭제 후에도 화면 유지) | ✅ 개선 후 (즉시 이동 및 피드백) |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/5cddd323-4d9f-470b-b2ab-f2b4bee74448" width="300"> | <img src="https://github.com/user-attachments/assets/1774c592-d19b-4e38-b7ca-20d778bc99af" width="300"> |

</details>

---

## 🌱 향후 도입
- FCM 도입
- 신고하기 기능
- 뱃지 시스템 제공
- 테스트 코드 작성
