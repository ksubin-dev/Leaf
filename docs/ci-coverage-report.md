# CI 커버리지 리포트 확인 방법

PR이 생성되거나 업데이트되면 `Android CI` workflow가 JVM unit test와 root `:jacocoTestReport`를 실행한다.

생성된 HTML 리포트는 GitHub Actions artifact로 업로드된다.

기본 JaCoCo HTML 리포트는 클래스별 세부 커버리지를 확인하기 위한 개발자용 리포트이고, Leafy 커스텀 HTML 리포트는 회의나 PR에서 커버리지 상태를 빠르게 공유하기 위한 요약 리포트다.

JaCoCo CSV 리포트를 기반으로 생성한 Markdown 요약은 GitHub Actions Summary에 표시되고, `jacoco-coverage-summary` artifact로도 업로드된다.

커버리지 요약은 전체 앱 수치와 핵심 품질 계층 수치를 분리해서 표시한다.

- 전체 커버리지는 앱 전체 자동 검증 수준을 보는 참고 지표다.
- 핵심 품질 계층 커버리지는 Worker, Repository, DataSource, Mapper, ViewModel, UseCase처럼 저장/업로드 안정성과 직접 연결되는 영역을 따로 모은 지표다.
- UI/Compose 별도 검토 영역은 Screen, Navigation, Component처럼 단순 렌더링 가능성이 큰 코드를 분리해 보여준다.

CI 환경에서는 보안상 실제 `app/google-services.json`을 커밋하지 않고, 테스트/리포트 생성에 필요한 placeholder 파일을 workflow 실행 중에 생성한다.

## 확인 순서

1. PR 화면에서 `Checks` 탭을 연다.
2. `Android CI / 단위 테스트 및 커버리지 리포트` 실행 결과를 선택한다.
3. Summary 화면의 `커버리지 요약`과 `핵심 품질 계층 커버리지 요약`을 먼저 확인한다.
4. 공유용 요약 화면이 필요하면 `Artifacts` 영역에서 `leafy-custom-coverage-report`를 다운로드한다.
5. 압축을 풀고 `index.html`을 브라우저에서 열어 커스텀 리포트를 확인한다.
6. 클래스별 세부 리포트가 필요하면 `jacoco-html-report`를 다운로드해 기본 JaCoCo HTML을 확인한다.
7. 테스트 우선순위 분석이 필요하면 `coverage-summary.md` 내용을 `docs/ai-coverage-analysis-prompt.md` 프롬프트에 붙여넣어 AI 분석을 받는다. 이때 전체 낮은 영역보다 `핵심 품질 계층 낮은 영역`을 우선 판단 기준으로 사용한다.
8. AI 응답의 JSON은 `docs/ai-coverage-analysis-result.example.json` 구조를 참고해 `docs/ai-coverage-analysis-result.json`에 저장한다.
9. 커스텀 HTML 리포트를 다시 생성하면 `AI 분석 요약` 영역에 위험 영역, 다음 테스트 후보, 보류 영역, 분석 한계가 함께 표시된다.

## AI 분석 결과 연결 방식

커스텀 HTML 리포트는 외부 AI API를 직접 호출하지 않는다.

대신 `coverage-summary.md`와 `docs/ai-coverage-analysis-prompt.md`를 사용해 받은 AI 응답 JSON을 입력 파일로 받아 HTML에 표시한다.

파일 역할은 다음과 같다.

| 파일 | 역할 | 커밋 여부 |
| --- | --- | --- |
| `docs/ai-coverage-analysis-prompt.md` | 커버리지 요약을 분석할 때 사용하는 프롬프트 | 커밋 |
| `docs/ai-coverage-analysis-result.example.json` | HTML 삽입용 AI 분석 JSON 예시 스키마 | 커밋 |
| `docs/ai-coverage-analysis-result.json` | 실행 시점마다 달라지는 실제 AI 분석 결과 입력 파일 | 커밋하지 않음 |

`docs/ai-coverage-analysis-result.json`은 `.gitignore`에 포함되어 있다. 이 파일은 특정 실행 시점의 분석 결과이므로, 저장소에 고정하지 않고 로컬 또는 CI 실행 중 입력으로만 사용한다.

AI 분석 결과 JSON이 없으면 커스텀 HTML 리포트는 안내 placeholder를 표시한다. JSON 형식이 잘못되어도 리포트 생성은 중단되지 않고 placeholder로 돌아간다.

## 현재 범위

이번 CI는 PR 리뷰 시점에 테스트 결과, 커버리지 Markdown 요약, 커버리지 HTML 리포트를 확인하기 위한 용도다.

develop/main 머지 기준 품질 리포트, Google Play 릴리즈 전 리포트, 주간 품질 추세 리포트는 별도 이슈에서 다룬다.

## 커버리지 해석 기준

전체 Line/Branch/Method 커버리지는 UI와 화면 코드까지 포함한 앱 전체 기준이다. 이 값은 자동 검증 범위의 큰 흐름을 보는 참고 지표이며, 특정 PR의 테스트 보강 효과를 단독으로 판단하는 기준으로 사용하지 않는다.

핵심 품질 계층 커버리지는 사용자 데이터 저장, 업로드, 동기화, 재시도, 데이터 변환, 상태 전환과 연결되는 코드만 따로 모아 본다. WorkManager, Repository, DataSource, Mapper, ViewModel, UseCase 테스트를 추가했을 때는 이 섹션의 변화와 낮은 영역 목록을 먼저 확인한다.

UI/Compose 별도 검토 영역은 단순 렌더링 코드가 핵심 위험 후보를 밀어내지 않도록 분리한 영역이다. 다만 UI 코드라도 입력값 검증, 저장/삭제 트리거, 인증/내비게이션 분기, 장애 이력이 확인되면 별도 테스트 후보로 다시 검토한다.

## CI 속도 개선 기준

#119에서는 HTML/Markdown 리포트 산출물을 제거하지 않고, 기존 리포트 생성 흐름을 유지한 상태에서 PR 피드백 시간을 줄인다.

기준선:

```text
PR 커버리지 리포트 CI 실행 시간: 약 5분 15초
```

적용한 개선 방향:

- Gradle build cache 활성화
- Gradle parallel build 활성화
- CI 실행 명령에 `--build-cache --parallel` 적용
- `gradle/actions/setup-gradle` 캐시를 PR 브랜치에서도 저장 가능하도록 설정
- artifact 업로드 압축 레벨을 낮춰 리포트 업로드 단계의 CPU 부담 완화

확인할 항목:

- `jacoco-coverage-summary` artifact 생성 유지
- `jacoco-html-report` artifact 생성 유지
- `leafy-custom-coverage-report` artifact 생성 확인
- GitHub Actions Summary의 커버리지 요약 표시 유지
- 개선 전후 CI 실행 시간 비교

## 로컬 생성 명령

```bash
./gradlew :generateCustomCoverageReport --build-cache --parallel
```

생성 위치:

```text
build/reports/jacoco/jacocoTestReport/coverage-summary.md
build/reports/leafy-test-report/index.html
```

선택 입력:

```text
docs/ai-coverage-analysis-result.json
```

예시 JSON을 기준으로 실제 입력 파일을 만들고 싶다면 다음 순서로 진행한다.

```bash
cp docs/ai-coverage-analysis-result.example.json docs/ai-coverage-analysis-result.json
./gradlew :generateCustomCoverageReport --build-cache --parallel
```

Windows PowerShell에서는 다음 명령을 사용할 수 있다.

```powershell
Copy-Item docs/ai-coverage-analysis-result.example.json docs/ai-coverage-analysis-result.json
.\gradlew.bat :generateCustomCoverageReport --build-cache --parallel
```
