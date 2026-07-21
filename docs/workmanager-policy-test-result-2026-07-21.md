# WorkManager 업로드 정책 테스트 결과

## 측정 배경

- 측정일: 2026-07-21
- 브랜치: `test/102-workmanager-retry-idempotency`
- 기준선 문서: `docs/test-coverage-baseline-2026-07-21.md`
- 실행 명령:
  - `.\gradlew.bat :data:testDebugUnitTest`
  - `.\gradlew.bat jacocoTestReport`
- HTML 리포트: `build/reports/jacoco/jacocoTestReport/html/index.html`

이번 작업은 #93에서 측정한 기준선을 바탕으로 WorkManager 업로드 정책의 자동 검증 범위를 먼저 넓힌 결과를 기록한다.

실제 Worker `doWork()` 실행 테스트는 후속 이슈로 분리하고, 이번에는 공통 retry/failure 정책과 Repository의 `enqueueUniqueWork` 정책을 단위 테스트로 고정했다.

## 추가한 테스트 범위

- `WorkerRetryPolicy` 공통 retry/failure 정책
- 최대 재시도 3회 도달 시 failure 종료 정책
- retry 가능한 실패와 즉시 failure 처리해야 하는 실패 구분
- Note `upload_note_{noteId}` + `ExistingWorkPolicy.REPLACE`
- Tea `upload_tea_{teaId}` + `ExistingWorkPolicy.REPLACE`
- Profile `update_profile_{userId}` + `ExistingWorkPolicy.REPLACE`
- Community `upload_post_draft_{draftKey}` + `ExistingWorkPolicy.KEEP`
- Community `postId` inputData 포함 여부
- Community `upload_community`, `upload_post_{postId}` tag 포함 여부
- 동일 Community draft 요청이 같은 uniqueName으로 예약되는지 여부

## 기준선 대비 변화

| 항목 | #93 기준선 | #102 작업 후 | 변화 |
| --- | ---: | ---: | ---: |
| `src/test` 단위 테스트 파일 | 16 | 18 | +2 |
| `src/androidTest` 계측 테스트 파일 | 12 | 12 | 0 |
| `src/test` 단위 테스트 메서드 | 21 | 35 | +14 |
| `src/androidTest` 계측 테스트 메서드 | 12 | 12 | 0 |
| 템플릿이 아닌 단위 테스트 파일 | 4 | 6 | +2 |
| 템플릿이 아닌 단위 테스트 메서드 | 9 | 23 | +14 |
| Repository 단위 테스트 파일 | 1 | 2 | +1 |
| ViewModel 단위 테스트 파일 | 2 | 2 | 0 |
| Worker/WorkManager 단위 테스트 파일 | 0 | 2 | +2 |

## 커버리지 변화

| 지표 | #93 기준선 | #102 작업 후 | 변화 |
| --- | ---: | ---: | ---: |
| Line Coverage | 1.35% | 2.42% | +1.07%p |
| Branch Coverage | 0.41% | 0.60% | +0.19%p |
| Instruction Coverage | 1.17% | 1.74% | +0.57%p |
| Method Coverage | 2.15% | 2.72% | +0.57%p |

커버된 라인은 236줄에서 424줄로 증가했고, 커버된 브랜치는 38개에서 56개로 증가했다.

## 실행 결과

debug/JVM 테스트 결과 기준으로 35개 테스트가 실행되었고, 실패 0개, 에러 0개, 스킵 0개를 확인했다.

## 해석

이번 작업으로 WorkManager 관련 테스트 파일이 0개에서 2개로 늘어났고, 저장/업로드 정책 중 가장 먼저 회귀 위험이 큰 uniqueName, ExistingWorkPolicy, inputData, tag가 자동 테스트로 보호되기 시작했다.

다만 전체 커버리지 수치는 아직 낮다. 이번 작업은 실제 Worker 실행, 이미지 압축, Firebase Storage 업로드, Firestore 저장까지 검증한 것이 아니라 Repository enqueue 정책과 공통 retry 정책을 먼저 고정한 단계이기 때문이다.

따라서 이후에는 #110, #111, #112를 통해 Worker catch 정책, Worker `doWork()` 실행 테스트, 초기 Sync enqueue 테스트를 순서대로 보강하는 것이 적합하다.

## 후속 작업

- #110: Worker catch retry/failure 정책 일관화
- #111: Worker doWork retry/failure 시나리오 테스트 환경 구성
- #112: 초기 Sync UniqueWork enqueue 정책 테스트 추가
