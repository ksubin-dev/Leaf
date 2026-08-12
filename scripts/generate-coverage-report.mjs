import fs from "node:fs";
import path from "node:path";

const args = parseArgs(process.argv.slice(2));
const summaryPath = args.summary;
const csvPath = args.csv;
const outDir = args.out;

if (!summaryPath || !csvPath || !outDir) {
  console.error("Usage: node scripts/generate-coverage-report.mjs --summary <path> --csv <path> --out <dir>");
  process.exit(1);
}

const summaryMarkdown = fs.readFileSync(summaryPath, "utf8");
const csvRows = readCsv(csvPath);
const context = parseContext(summaryMarkdown);
const metrics = parseMetrics(summaryMarkdown);
const lowCoverageAreas = parseLowCoverageAreas(summaryMarkdown);
const layerSummary = summarizeLayers(lowCoverageAreas);
const coreLayerSummaries = parseLayerSummaries(summaryMarkdown, "핵심 품질 계층 커버리지 요약");
const coreLowCoverageAreas = parseCoverageAreaSection(summaryMarkdown, "핵심 품질 계층 낮은 영역");
const uiLayerSummaries = parseLayerSummaries(summaryMarkdown, "UI/Compose 별도 검토 영역");
const uiCoverageAreas = parseCoverageAreaSection(summaryMarkdown, "UI/Compose 별도 검토 영역");
const aiAnalysis = loadAiAnalysis(args["ai-json"] ?? "docs/ai-coverage-analysis-result.json");

fs.mkdirSync(outDir, { recursive: true });
fs.writeFileSync(
  path.join(outDir, "index.html"),
  renderHtml({
    context,
    metrics,
    lowCoverageAreas,
    layerSummary,
    coreLayerSummaries,
    coreLowCoverageAreas,
    uiLayerSummaries,
    uiCoverageAreas,
    aiAnalysis,
    zeroCoverageCount: parseZeroCoverageCount(summaryMarkdown),
    coreZeroCoverageCount: parseNamedZeroCoverageCount(summaryMarkdown, "핵심 품질 계층"),
    uiZeroCoverageCount: parseNamedZeroCoverageCount(summaryMarkdown, "UI/Compose 별도 검토 계층"),
    classCount: csvRows.length,
  }),
  "utf8",
);

console.log(`Custom coverage report generated: ${path.join(outDir, "index.html")}`);

function parseArgs(rawArgs) {
  const parsed = {};
  for (let index = 0; index < rawArgs.length; index += 2) {
    const key = rawArgs[index]?.replace(/^--/, "");
    const value = rawArgs[index + 1];
    if (key && value) parsed[key] = value;
  }
  return parsed;
}

function readCsv(filePath) {
  const text = fs.readFileSync(filePath, "utf8").trim();
  if (!text) return [];

  const [headerLine, ...lines] = text.split(/\r?\n/);
  const headers = headerLine.split(",");
  return lines.filter(Boolean).map((line) => {
    const values = line.split(",");
    return Object.fromEntries(headers.map((header, index) => [header, values[index] ?? ""]));
  });
}

function parseContext(markdown) {
  const section = getSection(markdown, "Report Context");
  const rows = parseMarkdownTable(section);
  return rows.map((row) => ({
    label: row["항목"] ?? "",
    value: row["값"] ?? "",
  })).filter((row) => row.label);
}

function parseMetrics(markdown) {
  const section = getSection(markdown, "커버리지 요약");
  const rows = parseMarkdownTable(section);
  return rows.map((row) => ({
    label: row["지표"] ?? "",
    coverage: row["커버리지"] ?? "",
    covered: row["커버됨"] ?? "",
    missed: row["누락"] ?? "",
    percent: Number((row["커버리지"] ?? "0").replace("%", "")),
  })).filter((row) => row.label);
}

function parseLowCoverageAreas(markdown) {
  return parseCoverageAreaSection(markdown, "커버리지가 낮은 영역");
}

function parseCoverageAreaSection(markdown, heading) {
  const section = getSection(markdown, heading);
  const rows = parseMarkdownTableByHeaders(section, ["클래스"]);
  return rows.map((row) => {
    const name = stripCode(row["클래스"] ?? "");
    const layer = row["계층"] ?? classifyLayer(name);
    return {
      name,
      layer,
      lineCoverage: row["라인 커버리지"] ?? "",
      coveredLines: row["커버된 라인"] ?? "",
      missedLines: row["누락 라인"] ?? "",
      priority: recommendPriority(name, layer),
      userImpact: describeImpact(name, layer),
    };
  }).filter((row) => row.name && row.name !== "라인 커버리지 데이터 없음");
}

function parseLayerSummaries(markdown, heading) {
  const section = getSection(markdown, heading);
  const rows = parseMarkdownTableByHeaders(section, ["계층", "0% 클래스"]);
  return rows.map((row) => ({
    layer: row["계층"] ?? "",
    lineCoverage: row["라인 커버리지"] ?? "",
    coveredLines: row["커버된 라인"] ?? "",
    missedLines: row["누락 라인"] ?? "",
    zeroCoverageClasses: row["0% 클래스"] ?? "",
  })).filter((row) => row.layer && row.layer !== "데이터 없음");
}

function parseZeroCoverageCount(markdown) {
  const match = markdown.match(/라인 커버리지 0% 클래스 수:\s*(\d+)/);
  return match ? Number(match[1]) : null;
}

function parseNamedZeroCoverageCount(markdown, label) {
  const match = markdown.match(new RegExp(`${escapeRegExp(label)} 0% 클래스 수:\\s*(\\d+)`));
  return match ? Number(match[1]) : null;
}

function getSection(markdown, heading) {
  const lines = markdown.split(/\r?\n/);
  const start = lines.findIndex((line) => line.trim() === `## ${heading}`);
  if (start < 0) return "";

  const end = lines.findIndex((line, index) => index > start && line.startsWith("## "));
  return lines.slice(start + 1, end < 0 ? undefined : end).join("\n");
}

function parseMarkdownTable(section) {
  const tableLines = section
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter((line) => line.startsWith("|") && line.endsWith("|"));

  if (tableLines.length < 2) return [];

  const headers = splitTableLine(tableLines[0]);
  return tableLines.slice(2).map((line) => {
    const values = splitTableLine(line);
    return Object.fromEntries(headers.map((header, index) => [header, values[index] ?? ""]));
  });
}

function splitTableLine(line) {
  return line.slice(1, -1).split("|").map((value) => value.trim());
}

function stripCode(value) {
  return value.replace(/^`|`$/g, "");
}

function classifyLayer(className) {
  const lower = className.toLowerCase();
  if (lower.includes("uploadqueue")) return "Sync/Upload Queue";
  if (lower.includes("worker")) return "Worker";
  if (lower.includes("repository")) return "Repository";
  if (lower.includes("datasource")) return "DataSource";
  if (lower.includes("mapper")) return "Mapper";
  if (lower.includes("viewmodel")) return "ViewModel";
  if (lower.includes("usecase")) return "UseCase";
  if (lower.includes(".dao.") || lower.includes("database") || lower.includes(".room.")) return "Database";
  if (lower.includes("sync")) return "Sync/Upload Queue";
  if (lower.includes("uistate") || lower.includes("stateholder")) return "StateHolder";
  if (lower.includes("navigation") || lower.includes("navgraph") || lower.includes("route")) return "Navigation";
  if (lower.includes("screen") || lower.includes("component") || lower.includes(".ui.") || lower.includes(".presentation.")) return "Compose UI";
  if (lower.includes("util") || lower.includes("utils") || lower.includes("policy")) return "Utility";
  return "분류 필요";
}

function parseMarkdownTableByHeaders(section, requiredHeaders) {
  const lines = section
    .split(/\r?\n/)
    .map((line) => line.trim());

  for (let index = 0; index < lines.length - 1; index += 1) {
    const line = lines[index];
    const separator = lines[index + 1];
    if (!line.startsWith("|") || !line.endsWith("|")) continue;
    if (!separator.startsWith("|") || !separator.endsWith("|")) continue;

    const headers = splitTableLine(line);
    if (!requiredHeaders.every((header) => headers.includes(header))) continue;

    const tableLines = [];
    for (let tableIndex = index + 2; tableIndex < lines.length; tableIndex += 1) {
      const tableLine = lines[tableIndex];
      if (!tableLine.startsWith("|") || !tableLine.endsWith("|")) break;
      tableLines.push(tableLine);
    }

    return tableLines.map((tableLine) => {
      const values = splitTableLine(tableLine);
      return Object.fromEntries(headers.map((header, valueIndex) => [header, values[valueIndex] ?? ""]));
    });
  }

  return [];
}

function recommendPriority(className, providedLayer) {
  const layer = providedLayer ?? classifyLayer(className);
  if (["DataSource", "Repository", "Worker", "Mapper", "ViewModel", "Sync/Upload Queue"].includes(layer)) return "P1";
  if (["UseCase", "Database", "Utility"].includes(layer)) return "P2";
  return "P3";
}

function describeImpact(className, providedLayer) {
  const layer = providedLayer ?? classifyLayer(className);
  if (layer === "DataSource") return "저장/조회 실패 시 사용자 데이터가 누락되거나 잘못 표시될 수 있어 먼저 확인이 필요합니다.";
  if (layer === "Repository") return "데이터 흐름의 성공/실패 전달이 흔들리면 여러 화면에 영향이 퍼질 수 있습니다.";
  if (layer === "Worker") return "백그라운드 저장, 재시도, 중복 실행 정책이 의도대로 동작하는지 검증이 필요합니다.";
  if (layer === "Mapper") return "데이터 변환 오류가 있으면 화면과 저장소의 값이 다르게 보일 수 있습니다.";
  if (layer === "ViewModel") return "상태 전환 오류가 있으면 사용자가 저장 결과를 잘못 인지할 수 있습니다.";
  if (layer === "UseCase") return "비즈니스 규칙의 성공/실패 분기가 의도대로 유지되는지 확인이 필요합니다.";
  if (layer === "Database") return "로컬 저장과 조회 정책이 흔들리면 오프라인 데이터와 복구 흐름에 영향을 줄 수 있습니다.";
  if (layer === "Sync/Upload Queue") return "대기, 재시도, 실패 상태 전이가 사용자 데이터 복구 흐름과 직접 연결됩니다.";
  if (layer === "Utility") return "공통 정책이나 유틸리티 오류가 여러 저장/업로드 흐름에 반복 영향을 줄 수 있습니다.";
  if (layer === "Compose UI") return "현재 정보만으로는 높은 위험으로 단정하기 어려워 핵심 사용자 흐름 중심으로 보류합니다.";
  if (layer === "Navigation") return "단순 이동 정의는 보류하되 인증/저장 결과에 따른 분기가 있으면 다시 확인합니다.";
  if (layer === "StateHolder") return "상태 모델 자체는 보류하되 오류/저장 상태 분기가 복잡하면 테스트 후보로 다시 봅니다.";
  return "클래스명만으로 역할을 확정하기 어려워 추가 코드 확인이 필요합니다.";
}

function summarizeLayers(areas) {
  const counts = new Map();
  areas.forEach((area) => counts.set(area.layer, (counts.get(area.layer) ?? 0) + 1));
  return [...counts.entries()].map(([layer, count]) => ({ layer, count }));
}

function loadAiAnalysis(filePath) {
  if (!filePath || !fs.existsSync(filePath)) return null;

  try {
    return JSON.parse(fs.readFileSync(filePath, "utf8"));
  } catch (error) {
    console.warn(`AI analysis JSON could not be parsed: ${filePath}`);
    console.warn(error.message);
    return null;
  }
}

function renderHtml({
  context,
  metrics,
  lowCoverageAreas,
  layerSummary,
  coreLayerSummaries,
  coreLowCoverageAreas,
  uiLayerSummaries,
  uiCoverageAreas,
  aiAnalysis,
  zeroCoverageCount,
  coreZeroCoverageCount,
  uiZeroCoverageCount,
  classCount,
}) {
  const generatedAt = context.find((item) => item.label === "Generated At")?.value ?? new Date().toISOString();
  const lineMetric = metrics.find((metric) => metric.label === "Line");
  const branchMetric = metrics.find((metric) => metric.label === "Branch");
  const methodMetric = metrics.find((metric) => metric.label === "Method");

  const status = Number(lineMetric?.percent ?? 0) >= 70 ? "통과" : "개선 필요";
  const statusClass = status === "통과" ? "pass" : "warn";

  return `<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Leafy 테스트 커버리지 리포트</title>
  <style>
    :root {
      --bg: #f5f6f8;
      --surface: #ffffff;
      --surface-soft: #f8fafc;
      --text: #1f2933;
      --muted: #667085;
      --line: #e4e7ec;
      --header: #f2f4f7;
      --blue: #2563eb;
      --green: #16a34a;
      --amber: #d97706;
      --red: #dc2626;
    }

    * { box-sizing: border-box; }
    body {
      margin: 0;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Pretendard", sans-serif;
      background: var(--bg);
      color: var(--text);
      line-height: 1.5;
    }

    .wrap { max-width: 1080px; margin: 0 auto; padding: 36px 24px 56px; }
    header {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 20px;
      margin-bottom: 20px;
    }
    .eyebrow { color: var(--muted); font-weight: 700; font-size: 0.82rem; text-transform: uppercase; }
    h1 { margin: 6px 0 8px; font-size: 2rem; letter-spacing: 0; }
    .lead { max-width: 760px; margin: 0; color: var(--muted); font-size: 0.98rem; }
    .status-pill {
      display: inline-flex;
      align-items: center;
      white-space: nowrap;
      border-radius: 999px;
      padding: 8px 13px;
      font-weight: 800;
      font-size: 0.86rem;
      border: 1px solid transparent;
    }
    .status-pill.pass { color: #067647; background: #ecfdf3; border-color: #abefc6; }
    .status-pill.warn { color: #b42318; background: #fef3f2; border-color: #fecdca; }

    .grid { display: grid; gap: 16px; }
    .summary-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); margin: 18px 0; }
    .two-col { grid-template-columns: 1.1fr 0.9fr; align-items: start; }

    .card, .panel {
      background: var(--surface);
      border: 1px solid var(--line);
      border-radius: 8px;
    }

    .card { padding: 18px; min-height: 126px; }
    .card h2 { margin: 0 0 8px; font-size: 0.88rem; color: var(--muted); }
    .metric { display: flex; align-items: baseline; gap: 8px; }
    .metric strong { font-size: 1.9rem; letter-spacing: 0; }
    .metric span { color: var(--muted); font-size: 0.86rem; }
    .bar { height: 8px; background: #eef2f6; border-radius: 999px; overflow: hidden; margin-top: 16px; }
    .bar span { display: block; height: 100%; min-width: 2px; background: var(--blue); border-radius: inherit; }

    .panel { padding: 22px; margin-top: 16px; }
    .panel h2 { margin: 0 0 14px; font-size: 1.3rem; }
    .note { color: var(--muted); margin: 0 0 16px; }

    table { width: 100%; border-collapse: collapse; font-size: 0.92rem; }
    th, td { padding: 12px 10px; border-bottom: 1px solid var(--line); vertical-align: top; }
    th { text-align: left; color: #344054; font-weight: 800; background: var(--header); }
    tr:last-child td { border-bottom: 0; }
    td.numeric, th.numeric { text-align: right; }
    code {
      display: inline-block;
      max-width: 100%;
      padding: 3px 6px;
      border-radius: 6px;
      background: #eef2f6;
      overflow-wrap: anywhere;
      font-size: 0.84rem;
    }

    .badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      min-width: 42px;
      padding: 4px 9px;
      border-radius: 999px;
      color: #fff;
      font-weight: 800;
      font-size: 0.78rem;
    }
    .p0 { background: #7f1d1d; }
    .p1 { background: var(--red); }
    .p2 { background: var(--amber); }
    .p3 { background: #475467; }

    .context {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 10px;
    }
    .context-item { padding: 12px; background: var(--surface-soft); border: 1px solid var(--line); border-radius: 8px; }
    .context-item b { display: block; color: var(--muted); font-size: 0.82rem; margin-bottom: 4px; }
    .context-item span { overflow-wrap: anywhere; }

    .layer-list { display: grid; gap: 10px; }
    .layer-row { display: flex; justify-content: space-between; gap: 12px; padding: 12px; background: var(--surface-soft); border: 1px solid var(--line); border-radius: 8px; }

    .ai-placeholder {
      border: 1px dashed #98a2b3;
      background: #fbfcfa;
      border-radius: 8px;
      padding: 18px;
    }
    .ai-placeholder ul { margin: 12px 0 0; padding-left: 20px; color: var(--muted); }
    .ai-summary {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 12px;
      margin: 14px 0 18px;
    }
    .ai-summary-item {
      padding: 14px;
      border: 1px solid var(--line);
      border-radius: 8px;
      background: var(--surface-soft);
    }
    .ai-summary-item b {
      display: block;
      margin-bottom: 6px;
      color: var(--muted);
      font-size: 0.82rem;
    }
    .section-subtitle {
      margin: 26px 0 10px;
      font-size: 1rem;
    }
    .simple-list {
      margin: 0;
      padding-left: 20px;
      color: var(--muted);
    }
    .simple-list li { margin: 6px 0; }
    .issue-list {
      display: grid;
      gap: 12px;
      margin-top: 10px;
    }
    .issue-item {
      padding: 14px 0;
      border-top: 1px solid var(--line);
    }
    .issue-item:first-child { border-top: 0; }
    .issue-item b { display: block; margin-bottom: 6px; }
    .empty-row { color: var(--muted); text-align: center; }

    footer { margin-top: 28px; color: var(--muted); font-size: 0.9rem; }

    @media (max-width: 900px) {
      .summary-grid, .two-col, .context, .ai-summary { grid-template-columns: 1fr; }
      .wrap { padding: 28px 16px 44px; }
      table { font-size: 0.86rem; }
    }
  </style>
</head>
<body>
  <main class="wrap">
    <header>
      <div>
        <div class="eyebrow">Leafy Quality Report</div>
        <h1>테스트 커버리지 리포트</h1>
        <p class="lead">JaCoCo 결과를 공유와 회고에 쓰기 쉽도록 요약했습니다. 낮은 커버리지는 버그를 의미하지 않으며, 다음 테스트 우선순위를 정하기 위한 신호로 해석합니다.</p>
      </div>
      <span class="status-pill ${statusClass}">${status}</span>
    </header>

    <section class="grid summary-grid" aria-label="커버리지 요약">
      ${metrics.map((metric) => metricCard(metric)).join("\n")}
    </section>

    <section class="grid two-col">
      <article class="panel">
        <h2>리포트 컨텍스트</h2>
        <div class="context">
          ${context.map((item) => `<div class="context-item"><b>${escapeHtml(item.label)}</b><span>${escapeHtml(item.value)}</span></div>`).join("\n")}
        </div>
      </article>

      <article class="panel">
        <h2>현재 상태</h2>
        <p class="note">총 ${formatNumber(classCount)}개 클래스 중 생성 코드를 제외한 0% 라인 커버리지 클래스가 ${formatNumber(zeroCoverageCount)}개입니다.</p>
        <div class="layer-list">
          ${layerSummary.map((item) => `<div class="layer-row"><span>${escapeHtml(item.layer)}</span><strong>${item.count}개</strong></div>`).join("\n")}
        </div>
      </article>
    </section>

    <section class="grid two-col">
      ${renderLayerSummaryPanel(
        "핵심 품질 계층",
        `Worker, Repository, DataSource, Mapper, ViewModel 등 사용자 데이터 흐름과 직접 맞닿은 계층입니다. 0% 클래스 ${formatNumber(coreZeroCoverageCount)}개를 우선 신호로 봅니다.`,
        coreLayerSummaries,
      )}
      ${renderLayerSummaryPanel(
        "UI/Compose 별도 검토",
        `Screen, Navigation, Component 등은 핵심 계층과 분리해 해석합니다. 0% 클래스 ${formatNumber(uiZeroCoverageCount)}개는 단순 렌더링 여부를 먼저 확인합니다.`,
        uiLayerSummaries,
      )}
    </section>

    ${renderCoverageAreaPanel(
      "핵심 품질 계층 낮은 영역",
      "AI 분석과 다음 테스트 후보 산정에서 먼저 볼 영역입니다. 저장, 업로드, 동기화, 데이터 변환, 상태 전환 실패 가능성을 중심으로 해석합니다.",
      coreLowCoverageAreas,
    )}

    <section class="panel">
      <h2>커버리지가 낮은 영역</h2>
      <p class="note">전체 앱 기준 상위 영역입니다. UI 코드가 섞일 수 있으므로 테스트 우선순위는 핵심 품질 계층 표를 먼저 확인합니다.</p>
      <table>
        <thead>
          <tr>
            <th>우선순위</th>
            <th>계층</th>
            <th>대상</th>
            <th class="numeric">라인 커버리지</th>
            <th class="numeric">누락 라인</th>
            <th>확인 관점</th>
          </tr>
        </thead>
        <tbody>
          ${lowCoverageAreas.map((area) => `
          <tr>
            <td><span class="badge ${area.priority.toLowerCase()}">${escapeHtml(area.priority)}</span></td>
            <td>${escapeHtml(area.layer)}</td>
            <td><code>${escapeHtml(area.name)}</code></td>
            <td class="numeric">${escapeHtml(area.lineCoverage)}</td>
            <td class="numeric">${escapeHtml(area.missedLines)}</td>
            <td>${escapeHtml(area.userImpact)}</td>
          </tr>`).join("\n")}
        </tbody>
      </table>
    </section>

    ${renderCoverageAreaPanel(
      "UI/Compose 별도 검토 영역",
      "단순 렌더링 코드는 보류하되 입력 검증, 저장/삭제 트리거, 복잡한 상태 분기, 장애 이력이 확인되면 다시 테스트 후보로 올립니다.",
      uiCoverageAreas,
    )}

    ${renderAiSection(aiAnalysis)}

    <footer>
      생성 시각: ${escapeHtml(generatedAt)} · Line ${escapeHtml(lineMetric?.coverage ?? "정보 없음")} · Branch ${escapeHtml(branchMetric?.coverage ?? "정보 없음")} · Method ${escapeHtml(methodMetric?.coverage ?? "정보 없음")}
    </footer>
  </main>
</body>
</html>`;
}

function renderAiSection(aiAnalysis) {
  if (!aiAnalysis) {
    return `<section class="panel">
      <h2>AI 분석 연결 영역</h2>
      <div class="ai-placeholder">
        <strong>#117 프롬프트 결과를 여기에 연결할 수 있습니다.</strong>
        <ul>
          <li><code>coverage-summary.md</code>를 <code>docs/ai-coverage-analysis-prompt.md</code>에 붙여넣어 테스트 후보를 정리합니다.</li>
          <li><code>docs/ai-coverage-analysis-result.example.json</code> 구조를 참고해 AI 응답 JSON을 <code>docs/ai-coverage-analysis-result.json</code>에 저장합니다.</li>
          <li>현재 리포트는 AI API를 직접 호출하지 않고, 커버리지 수치와 보수적 계층 분류만 표시합니다.</li>
        </ul>
      </div>
    </section>`;
  }

  const summary = aiAnalysis.overallSummary ?? {};
  const riskAreas = Array.isArray(aiAnalysis.riskAreas) ? aiAnalysis.riskAreas : [];
  const testCandidates = Array.isArray(aiAnalysis.testCandidates) ? aiAnalysis.testCandidates : [];
  const deferredAreas = Array.isArray(aiAnalysis.deferredAreas) ? aiAnalysis.deferredAreas : [];
  const requiredContext = Array.isArray(aiAnalysis.requiredContext) ? aiAnalysis.requiredContext : [];
  const limitations = Array.isArray(aiAnalysis.limitations) ? aiAnalysis.limitations : [];
  const suggestedIssues = Array.isArray(aiAnalysis.suggestedIssues) ? aiAnalysis.suggestedIssues : [];

  return `<section class="panel">
      <h2>AI 분석 요약</h2>
      <p class="note">${escapeHtml(summary.plainLanguageSummary ?? "요약 정보 없음")}</p>
      <div class="ai-summary">
        <div class="ai-summary-item">
          <b>기술 요약</b>
          <span>${escapeHtml(summary.technicalSummary ?? "정보 없음")}</span>
        </div>
        <div class="ai-summary-item">
          <b>공유용 한 줄 요약</b>
          <span>${escapeHtml(summary.oneLineSummary ?? "정보 없음")}</span>
        </div>
      </div>

      <h3 class="section-subtitle">위험 영역</h3>
      <table>
        <thead>
          <tr>
            <th>우선순위</th>
            <th>영역</th>
            <th>기술 대상</th>
            <th>근거 수준</th>
            <th>사용자 영향</th>
          </tr>
        </thead>
        <tbody>
          ${riskAreas.length ? riskAreas.slice(0, 5).map((area) => `
          <tr>
            <td><span class="badge ${String(area.priority ?? "P3").toLowerCase()}">${escapeHtml(area.priority ?? "P3")}</span></td>
            <td>${escapeHtml(area.featureArea ?? area.technicalTarget ?? "정보 없음")}</td>
            <td><code>${escapeHtml(area.technicalTarget ?? "정보 없음")}</code></td>
            <td>${escapeHtml(area.evidenceLevel ?? "정보 없음")}</td>
            <td>${escapeHtml(area.plainLanguageImpact ?? "정보 없음")}</td>
          </tr>`).join("\n") : emptyRow(5, "AI 분석 위험 영역 정보가 없습니다.")}
        </tbody>
      </table>

      <h3 class="section-subtitle">다음 테스트 후보</h3>
      <table>
        <thead>
          <tr>
            <th>순위</th>
            <th>대상</th>
            <th>테스트 시나리오</th>
            <th>기대 결과</th>
            <th>유형</th>
          </tr>
        </thead>
        <tbody>
          ${testCandidates.length ? testCandidates.slice(0, 5).map((candidate) => `
          <tr>
            <td>${escapeHtml(candidate.rank ?? "")}</td>
            <td>${escapeHtml(candidate.target ?? "정보 없음")}</td>
            <td>${escapeHtml(candidate.scenario ?? "정보 없음")}</td>
            <td>${escapeHtml(candidate.expectedResult ?? "정보 없음")}</td>
            <td>${escapeHtml(candidate.testType ?? "정보 없음")}</td>
          </tr>`).join("\n") : emptyRow(5, "AI 분석 테스트 후보 정보가 없습니다.")}
        </tbody>
      </table>

      <h3 class="section-subtitle">보류 영역</h3>
      <table>
        <thead>
          <tr>
            <th>대상</th>
            <th>보류 이유</th>
            <th>다시 검토할 조건</th>
          </tr>
        </thead>
        <tbody>
          ${deferredAreas.length ? deferredAreas.slice(0, 5).map((area) => `
          <tr>
            <td>${escapeHtml(area.target ?? "정보 없음")}</td>
            <td>${escapeHtml(area.reason ?? "정보 없음")}</td>
            <td>${escapeHtml(area.revisitCondition ?? "정보 없음")}</td>
          </tr>`).join("\n") : emptyRow(3, "보류 영역 정보가 없습니다.")}
        </tbody>
      </table>

      <h3 class="section-subtitle">분석 한계와 추가로 필요한 정보</h3>
      <div class="ai-summary">
        <div class="ai-summary-item">
          <b>분석 한계</b>
          ${simpleList(limitations, "분석 한계 정보가 없습니다.")}
        </div>
        <div class="ai-summary-item">
          <b>추가로 필요한 정보</b>
          ${simpleList(requiredContext, "추가 입력 정보가 없습니다.")}
        </div>
      </div>

      <h3 class="section-subtitle">다음 이슈 후보</h3>
      <div class="issue-list">
        ${suggestedIssues.length ? suggestedIssues.slice(0, 3).map((issue) => `
        <div class="issue-item">
          <b>${escapeHtml(issue.title ?? "제목 없음")}</b>
          ${simpleList(issue.acceptanceCriteria, "완료 조건 정보가 없습니다.")}
        </div>`).join("\n") : `<p class="note">AI 분석 기반 다음 이슈 후보가 없습니다.</p>`}
      </div>
    </section>`;
}

function renderLayerSummaryPanel(title, note, summaries) {
  return `<article class="panel">
        <h2>${escapeHtml(title)}</h2>
        <p class="note">${escapeHtml(note)}</p>
        <table>
          <thead>
            <tr>
              <th>계층</th>
              <th class="numeric">라인 커버리지</th>
              <th class="numeric">누락 라인</th>
              <th class="numeric">0% 클래스</th>
            </tr>
          </thead>
          <tbody>
            ${summaries.length ? summaries.map((summary) => `
            <tr>
              <td>${escapeHtml(summary.layer)}</td>
              <td class="numeric">${escapeHtml(summary.lineCoverage)}</td>
              <td class="numeric">${escapeHtml(summary.missedLines)}</td>
              <td class="numeric">${escapeHtml(summary.zeroCoverageClasses)}</td>
            </tr>`).join("\n") : emptyRow(4, "계층 요약 정보가 없습니다.")}
          </tbody>
        </table>
      </article>`;
}

function renderCoverageAreaPanel(title, note, areas) {
  return `<section class="panel">
      <h2>${escapeHtml(title)}</h2>
      <p class="note">${escapeHtml(note)}</p>
      <table>
        <thead>
          <tr>
            <th>우선순위</th>
            <th>계층</th>
            <th>대상</th>
            <th class="numeric">라인 커버리지</th>
            <th class="numeric">누락 라인</th>
            <th>확인 관점</th>
          </tr>
        </thead>
        <tbody>
          ${areas.length ? areas.map((area) => `
          <tr>
            <td><span class="badge ${area.priority.toLowerCase()}">${escapeHtml(area.priority)}</span></td>
            <td>${escapeHtml(area.layer)}</td>
            <td><code>${escapeHtml(area.name)}</code></td>
            <td class="numeric">${escapeHtml(area.lineCoverage)}</td>
            <td class="numeric">${escapeHtml(area.missedLines)}</td>
            <td>${escapeHtml(area.userImpact)}</td>
          </tr>`).join("\n") : emptyRow(6, "라인 커버리지 데이터가 없습니다.")}
        </tbody>
      </table>
    </section>`;
}

function emptyRow(colspan, message) {
  return `<tr><td class="empty-row" colspan="${colspan}">${escapeHtml(message)}</td></tr>`;
}

function simpleList(items, fallback) {
  if (!Array.isArray(items) || !items.length) return `<p class="note">${escapeHtml(fallback)}</p>`;
  return `<ul class="simple-list">${items.slice(0, 5).map((item) => `<li>${escapeHtml(item)}</li>`).join("\n")}</ul>`;
}

function metricCard(metric) {
  return `<article class="card">
    <h2>${escapeHtml(metric.label)}</h2>
    <div class="metric"><strong>${escapeHtml(metric.coverage)}</strong><span>covered ${escapeHtml(metric.covered)}</span></div>
    <div class="bar" aria-hidden="true"><span style="width: ${Math.max(metric.percent, 1)}%"></span></div>
    <p class="note">누락 ${escapeHtml(metric.missed)}</p>
  </article>`;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function formatNumber(value) {
  if (value === null || value === undefined || Number.isNaN(value)) return "정보 없음";
  return Number(value).toLocaleString("ko-KR");
}

function escapeRegExp(value) {
  return String(value).replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}
