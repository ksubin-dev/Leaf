# Leafy 테스트 커버리지 기준선

## 측정 배경

- 측정일: 2026-07-21
- 브랜치: `docs/93-test-coverage-baseline`
- 기준 커밋: `3e1227e`
- 실행 명령: `.\gradlew.bat jacocoTestReport`
- HTML 리포트: `build/reports/jacoco/jacocoTestReport/html/index.html`
- CSV 리포트: `build/reports/jacoco/jacocoTestReport/jacocoTestReport.csv`
- XML 리포트: `build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

이번 기준선은 저장/업로드 안정성 테스트를 추가하기 전에 현재 테스트 상태를 수치로 남기기 위해 측정했다. 단순히 테스트가 부족하다는 감각이 아니라, 어느 영역의 방어가 얇은지와 이후 개선 폭을 비교할 기준을 확보하는 것이 목적이다.

## 테스트 현황

| 항목 | 수치 |
| --- | ---: |
| `src/test` 단위 테스트 파일 | 16 |
| `src/androidTest` 계측 테스트 파일 | 12 |
| `src/test` 단위 테스트 메서드 | 21 |
| `src/androidTest` 계측 테스트 메서드 | 12 |
| 템플릿이 아닌 단위 테스트 파일 | 4 |
| 템플릿이 아닌 단위 테스트 메서드 | 9 |
| Repository 단위 테스트 파일 | 1 |
| ViewModel 단위 테스트 파일 | 2 |
| Worker/WorkManager 단위 테스트 파일 | 0 |
| 템플릿 `ExampleUnitTest` 파일 | 12 |
| 템플릿 `ExampleInstrumentedTest` 파일 | 12 |

현재 의미 있는 단위 테스트가 존재하는 파일은 다음과 같다.

- `data/src/test/java/com/subin/leafy/data/repository/NoteRepositoryImplTest.kt`
- `domain/src/test/java/com/subin/leafy/domain/usecase/note/NoteUseCaseImageValidationTest.kt`
- `features/note/src/test/java/com/leafy/features/note/viewmodel/NoteUiStateTest.kt`
- `features/note/src/test/java/com/leafy/features/note/viewmodel/NoteViewModelTest.kt`

## 커버리지 기준선

집계 JaCoCo 리포트는 JVM 테스트와 Android debug 단위 테스트를 기준으로 생성했다. Android 생성 클래스, Hilt/Dagger 생성 클래스, `BuildConfig`, manifest, preview, test 클래스는 리포트에서 제외했다.

| 지표 | 커버된 수 | 전체 수 | 커버리지 |
| --- | ---: | ---: | ---: |
| Line | 236 | 17,530 | 1.35% |
| Branch | 38 | 9,340 | 0.41% |
| Instruction | 1,825 | 156,019 | 1.17% |
| Method | 79 | 3,674 | 2.15% |

debug/JVM 테스트 결과 기준으로 21개 테스트가 실행되었고, 실패 0개, 에러 0개, 스킵 0개를 확인했다.

## 해석

현재 커버리지가 낮은 가장 큰 이유는 테스트 파일 수에 비해 실제 기능을 검증하는 테스트가 적기 때문이다. 특히 `ExampleUnitTest`와 `ExampleInstrumentedTest` 같은 템플릿 테스트가 많아, 단순 파일 수만으로는 테스트 품질을 판단하기 어렵다.

현재 방어가 확인되는 영역은 도메인 모델 일부, 노트 이미지 검증, 노트 ViewModel 상태, 노트 Repository 일부 흐름에 집중되어 있다. 반대로 최근 정리한 저장/업로드 안정성 정책과 직접 연결되는 WorkManager retry/failure, UniqueWork, idempotency, Storage URL 변환 흐름은 아직 테스트가 부족하다.

따라서 이후 개선은 총 테스트 파일 수보다 의미 있는 테스트 수, 핵심 실패 시나리오 수, 라인/브랜치 커버리지 변화량을 함께 비교하는 방식이 적합하다.

## 다음 보강 대상

- WorkManager retry/failure 분기 테스트
- 최대 재시도 횟수 초과 시 failure 종료 테스트
- `enqueueUniqueWork` 이름과 `ExistingWorkPolicy` 정책 테스트
- Community 게시글 이미지 경로 idempotency 테스트
- Note/Community 이미지가 Firebase Storage URL로 저장되는 흐름 테스트
- Tea/Profile Repository 저장 흐름 테스트
- 템플릿 테스트를 기능 단위 테스트로 대체 또는 보강

## 검증 메모

AI는 테스트 파일 구조 파악, 커버리지 공백 요약, 기준선 문서 초안 작성에 활용했다. 위 수치는 저장소의 테스트 파일 목록과 생성된 JaCoCo CSV 리포트를 직접 확인해 검증했다.

HTML 리포트는 `build/reports/jacoco/jacocoTestReport/html/index.html`에 생성된다. `build/`는 생성 산출물이므로 리포트 파일 자체는 커밋하지 않는다.
