# CI 커버리지 리포트 확인 방법

PR이 생성되거나 업데이트되면 `Android CI` workflow가 JVM unit test와 root `:jacocoTestReport`를 실행한다.

생성된 HTML 리포트는 GitHub Actions artifact로 업로드된다.

CI 환경에서는 보안상 실제 `app/google-services.json`을 커밋하지 않고, 테스트/리포트 생성에 필요한 placeholder 파일을 workflow 실행 중에 생성한다.

## 확인 순서

1. PR 화면에서 `Checks` 탭을 연다.
2. `Android CI / Unit tests and coverage report` 실행 결과를 선택한다.
3. workflow run 화면의 `Artifacts` 영역에서 `jacoco-html-report`를 다운로드한다.
4. 압축을 풀고 `index.html`을 브라우저에서 열어 커버리지 리포트를 확인한다.

## 현재 범위

이번 CI는 PR 리뷰 시점에 테스트 결과와 커버리지 HTML 리포트를 확인하기 위한 용도다.

develop/main 머지 기준 품질 리포트, Google Play 릴리즈 전 리포트, 주간 품질 추세 리포트는 별도 이슈에서 다룬다.
