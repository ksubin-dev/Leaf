# CI 커버리지 리포트 확인 방법

PR이 생성되거나 업데이트되면 `Android CI` workflow가 JVM unit test와 root `:jacocoTestReport`를 실행한다.

생성된 HTML 리포트는 GitHub Actions artifact로 업로드된다.

JaCoCo CSV 리포트를 기반으로 생성한 Markdown 요약은 GitHub Actions Summary에 표시되고, `jacoco-coverage-summary` artifact로도 업로드된다.

CI 환경에서는 보안상 실제 `app/google-services.json`을 커밋하지 않고, 테스트/리포트 생성에 필요한 placeholder 파일을 workflow 실행 중에 생성한다.

## 확인 순서

1. PR 화면에서 `Checks` 탭을 연다.
2. `Android CI / 단위 테스트 및 커버리지 리포트` 실행 결과를 선택한다.
3. Summary 화면의 `커버리지 요약`과 `커버리지가 낮은 영역`을 먼저 확인한다.
4. 세부 리포트가 필요하면 workflow run 화면의 `Artifacts` 영역에서 `jacoco-html-report`를 다운로드한다.
5. 압축을 풀고 `index.html`을 브라우저에서 열어 커버리지 리포트를 확인한다.
6. 테스트 우선순위 분석이 필요하면 `coverage-summary.md` 내용을 `docs/ai-coverage-analysis-prompt.md` 프롬프트에 붙여넣어 AI 분석을 받는다.

## 현재 범위

이번 CI는 PR 리뷰 시점에 테스트 결과, 커버리지 Markdown 요약, 커버리지 HTML 리포트를 확인하기 위한 용도다.

develop/main 머지 기준 품질 리포트, Google Play 릴리즈 전 리포트, 주간 품질 추세 리포트는 별도 이슈에서 다룬다.

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
- GitHub Actions Summary의 커버리지 요약 표시 유지
- 개선 전후 CI 실행 시간 비교

## 로컬 생성 명령

```bash
./gradlew :jacocoCoverageSummary --build-cache --parallel
```

생성 위치:

```text
build/reports/jacoco/jacocoTestReport/coverage-summary.md
```
