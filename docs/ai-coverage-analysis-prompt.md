# Leafy AI 커버리지 분석 프롬프트

## 목적

아래에 제공되는 `coverage-summary.md`를 기반으로 Leafy 프로젝트의 테스트 커버리지 상태를 분석하고, 다음에 작성할 테스트의 우선순위를 제안해주세요.

이 분석의 목적은 단순히 커버리지가 낮은 클래스를 나열하는 것이 아니라, 제한된 테스트 작성 자원을 사용자 영향과 실패 위험이 큰 영역에 먼저 배분하는 것입니다.

- Prompt version: `1.0`
- Output schema version: `1.0`

## 사용 방식과 적용 범위

이 문서는 CI에서 AI API를 직접 호출하기 위한 설정 파일이 아닙니다.

현재 적용 범위는 사람이 GitHub Actions Summary 또는 `jacoco-coverage-summary` artifact에서 `coverage-summary.md` 내용을 확인한 뒤, 아래 프롬프트에 붙여넣어 AI 분석을 받는 반자동 방식입니다.

사용 흐름:

```text
PR CI 실행
-> coverage-summary.md 자동 생성
-> GitHub Actions Summary 또는 artifact에서 요약 확인
-> 이 문서의 프롬프트에 coverage-summary.md 내용 붙여넣기
-> AI가 테스트 우선순위와 위험 영역 분석
-> 다음 테스트 이슈 또는 PR 계획에 반영
```

향후 #121 커스텀 HTML 리포트에서는 이 프롬프트의 JSON 출력을 HTML 카드와 표로 렌더링할 수 있도록 연결합니다.

## 입력 데이터의 한계

입력으로 제공되는 `coverage-summary.md`에는 다음 정보가 포함됩니다.

- 전체 Instruction 커버리지
- 전체 Branch 커버리지
- 전체 Line 커버리지
- 전체 Method 커버리지
- 라인 커버리지 0% 클래스 수
- 라인 커버리지가 낮은 클래스 목록
- 클래스별 커버된 라인과 누락 라인

다음 정보는 별도로 제공되지 않을 수 있습니다.

- 테스트 통과/실패 여부
- 이전 리포트 대비 커버리지 변화
- 이번 PR에서 변경된 파일
- 클래스 내부의 실제 구현 코드
- 최근 장애나 사용자 문의
- 기존 테스트가 검증하는 상세 시나리오
- 클래스별 Branch/Method 커버리지

제공되지 않은 정보는 임의로 만들어내지 말고 `정보 없음`, `확인 필요`, 또는 JSON의 `null`로 표시해주세요.

## 분석 원칙

다음 기준을 반드시 지켜주세요.

1. 커버리지가 낮다는 이유만으로 버그가 존재한다고 단정하지 마세요.
2. 커버리지 수치와 실제 서비스 위험도를 구분하세요.
3. 리포트에서 직접 확인한 사실과 클래스명/패키지명으로 추론한 내용을 구분하세요.
4. 이전 리포트가 제공되지 않았다면 커버리지가 상승하거나 하락했다고 표현하지 마세요.
5. 테스트 결과 XML이 제공되지 않았다면 테스트가 모두 통과했다고 표현하지 마세요.
6. 클래스의 역할이 불분명하면 구체적인 기능을 만들어내지 말고 `분류 불가`로 표시하세요.
7. 소스 코드가 제공되지 않은 상태에서는 P0 우선순위를 부여하지 마세요.
8. 커버리지 목표 수치가 제공되지 않았다면 특정 수치 달성을 품질 기준으로 단정하지 마세요.
9. 동일한 0% 커버리지라도 누락 라인 수와 기능의 실패 영향을 함께 고려하세요.
10. 분석 결과에 사용한 가정과 한계를 반드시 기록하세요.

## Leafy 테스트 우선순위 기준

다음 영역을 우선 검토해주세요.

### 높은 우선 검토 영역

- 사용자 데이터 저장/수정/삭제
- Firebase 또는 외부 API 통신
- 이미지 업로드/다운로드
- 로컬 데이터베이스 저장 및 변환
- WorkManager 실행/재시도/중복 실행
- Repository와 DataSource의 성공/실패 처리
- Mapper의 null, 빈 값, 잘못된 데이터 변환
- ViewModel의 상태 전환 및 오류 처리
- 비동기 처리와 예외 복구
- 인증 및 권한 처리

### 별도 검토 영역

Compose UI 클래스는 다른 계층과 분리하여 검토해주세요.

다음 조건이 확인되지 않는 단순 UI 렌더링 클래스는 높은 우선순위로 선정하지 마세요.

- 복잡한 상태 분기
- 사용자 핵심 흐름
- 입력값 검증
- 내비게이션 분기
- 데이터 저장 또는 삭제 트리거
- 장애가 발생했던 화면

## 계층 분류

각 클래스는 가능한 경우 다음 중 하나로 분류해주세요.

- Repository
- DataSource
- Worker
- Mapper
- UseCase
- ViewModel
- StateHolder
- Compose UI
- Database
- Network
- Utility
- 분류 불가

클래스명만으로 확실하게 분류할 수 없다면 추정임을 표시해주세요.

## 우선순위 정의

| 우선순위 | 기준 |
| --- | --- |
| P0 | 데이터 유실, 중복 처리, 인증/보안 문제 또는 핵심 기능 중단 위험이 직접적인 근거로 확인된 영역 |
| P1 | 저장, 네트워크, 백그라운드 작업, 데이터 변환, 상태 관리처럼 실패 영향이 큰 영역 |
| P2 | 비즈니스 로직을 포함하지만 영향 범위가 상대적으로 제한적인 영역 |
| P3 | 단순 UI 렌더링, 정적 코드 또는 현재 정보만으로 테스트 효율이 낮다고 판단되는 영역 |

커버리지 요약과 클래스명만 제공된 경우에는 `P1`보다 높은 우선순위를 부여하지 마세요.

## 근거 수준

각 판단에는 다음 중 하나의 근거 수준을 표시해주세요.

| 근거 수준 | JSON 값 | 의미 |
| --- | --- | --- |
| 높음 | `high` | 입력 리포트에 직접적인 수치와 역할 정보가 있음 |
| 중간 | `medium` | 수치는 확인되지만 클래스 역할 일부를 이름으로 추정함 |
| 낮음 | `low` | 클래스명이나 패키지명만으로 역할과 위험을 추정함 |

JSON에서는 반드시 `high`, `medium`, `low` 중 하나를 사용해주세요.

## 출력 형식

아래 제목과 순서를 변경하지 마세요.

### 1. 비개발자용 요약

테스트 커버리지 상태를 개발 용어를 최소화하여 3문장 이내로 설명해주세요.

다음을 포함해주세요.

- 현재 무엇이 확인되었는지
- 어떤 위험을 먼저 확인해야 하는지
- 다음에 무엇을 하면 되는지

### 2. 현재 커버리지 상태

다음 표를 사용해주세요.

| 지표 | 현재 값 | 의미 | 해석 시 주의점 |
| --- | ---: | --- | --- |
| Instruction |  |  |  |
| Branch |  |  |  |
| Line |  |  |  |
| Method |  |  |  |

Branch 커버리지가 Line 커버리지보다 현저히 낮다면 조건문과 예외 흐름 검증 부족 가능성을 언급하되, 실제 원인으로 단정하지 마세요.

### 3. 위험도가 높은 미검증 영역

다음 표를 사용해주세요.

| 순위 | 우선순위 | 기능 영역 | 기술 대상 | 계층 | 커버리지 근거 | 위험 신호 | 사용자 영향 | 근거 수준 |
| -: | --- | --- | --- | --- | --- | --- | --- | --- |

최대 5개까지만 작성해주세요.

`기능 영역`은 비개발자도 이해할 수 있는 표현으로 작성하고, `기술 대상`에는 실제 클래스명을 작성해주세요.

### 4. 다음 테스트 후보

다음 표를 사용해주세요.

| 순위 | 테스트 대상 | 테스트할 상황 | 검증할 결과 | 추천 테스트 유형 | 선정 이유 |
| -: | --- | --- | --- | --- | --- |

테스트 후보는 단순히 클래스명을 반복하지 말고 구체적인 테스트 시나리오로 작성해주세요.

추천 테스트 유형은 다음 중에서 선택해주세요.

- Unit Test
- Coroutine Test
- Repository Test
- Worker Test
- Mapper Test
- ViewModel Test
- Compose UI Test
- Integration Test
- 추가 코드 확인 필요

### 5. 우선순위에서 제외하거나 보류한 영역

Compose UI, 단순 렌더링 코드 또는 정보가 부족한 영역을 정리해주세요.

| 대상 | 보류 이유 | 다시 검토할 조건 |
| --- | --- | --- |

### 6. 추가로 필요한 정보

더 정확한 분석을 위해 필요한 정보를 최대 5개까지 작성해주세요.

예:

- 이번 PR 변경 파일
- 해당 클래스의 실제 소스 코드
- 기존 테스트 목록
- 이전 커버리지 리포트
- 최근 장애 또는 사용자 문의
- 테스트 결과 XML

### 7. 분석의 한계와 가정

이번 분석에서 확인할 수 없었던 내용과 사용한 추정을 명확히 작성해주세요.

### 8. 다음 이슈 후보

다음 형식으로 3개 이내의 이슈 제목을 제안해주세요.

```text
[Test] 대상 기능 - 검증할 핵심 상황
```

각 이슈마다 완료 조건을 2~4개 작성해주세요.

### 9. 공유용 한 줄 요약

PR, 회의 또는 포트폴리오에서 사용할 수 있도록 1문장으로 작성해주세요.

과장된 품질 개선 표현은 사용하지 말고, 리포트에서 확인된 사실과 다음 액션을 중심으로 작성해주세요.

### 10. HTML 변환용 JSON

위 분석 결과와 동일한 내용을 아래 JSON 형식으로 다시 출력해주세요.

JSON은 반드시 유효한 JSON이어야 하며 주석, Markdown 문법, 후행 쉼표를 포함하지 마세요.

알 수 없는 값은 추측하지 말고 `null`을 사용해주세요.

숫자 필드는 문자열이 아니라 숫자로 출력해주세요. 예를 들어 `coveragePercent`는 `"2.48%"`가 아니라 `2.48`로 작성합니다.

```json
{
  "schemaVersion": "1.0",
  "promptVersion": "1.0",
  "overallSummary": {
    "plainLanguageSummary": "",
    "technicalSummary": "",
    "oneLineSummary": ""
  },
  "metrics": {
    "instruction": {
      "coveragePercent": null,
      "covered": null,
      "missed": null
    },
    "branch": {
      "coveragePercent": null,
      "covered": null,
      "missed": null
    },
    "line": {
      "coveragePercent": null,
      "covered": null,
      "missed": null
    },
    "method": {
      "coveragePercent": null,
      "covered": null,
      "missed": null
    },
    "zeroLineCoverageClassCount": null
  },
  "riskAreas": [
    {
      "rank": 1,
      "priority": "P1",
      "featureArea": "",
      "technicalTarget": "",
      "layer": "",
      "lineCoveragePercent": null,
      "coveredLines": null,
      "missedLines": null,
      "riskSignal": "",
      "plainLanguageImpact": "",
      "evidenceLevel": "medium",
      "assumptions": []
    }
  ],
  "testCandidates": [
    {
      "rank": 1,
      "target": "",
      "scenario": "",
      "expectedResult": "",
      "testType": "",
      "reason": ""
    }
  ],
  "deferredAreas": [
    {
      "target": "",
      "reason": "",
      "revisitCondition": ""
    }
  ],
  "requiredContext": [],
  "limitations": [],
  "suggestedIssues": [
    {
      "title": "",
      "acceptanceCriteria": []
    }
  ]
}
```

## 선택 입력 정보

아래 정보가 입력되면 분석에 함께 사용해주세요.

### 프로젝트 위험 기준

- 사용자 데이터 저장/삭제 오류를 높은 위험으로 본다.
- 이미지 업로드 실패와 중복 업로드를 우선 검증한다.
- WorkManager retry와 중복 실행 가능성을 우선 검증한다.
- Repository, DataSource, Worker, Mapper, ViewModel을 우선 검토한다.
- 단순 Compose UI는 별도 그룹으로 분리한다.

### PR 정보

- PR 번호: `[제공되지 않음]`
- Commit SHA: `[제공되지 않음]`
- 변경된 파일: `[제공되지 않음]`
- 이전 커버리지 리포트: `[제공되지 않음]`
- 최근 장애 또는 사용자 문의: `[제공되지 않음]`

## 입력

아래에 `coverage-summary.md` 내용을 붙여넣습니다.

```md
[coverage-summary.md 내용을 여기에 붙여넣기]
```
