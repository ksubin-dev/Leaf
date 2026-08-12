import fs from "node:fs";
import path from "node:path";

const DEFAULT_MODEL = "gpt-5-mini";
const DEFAULT_BASE_URL = "https://api.openai.com/v1";
const PROMPT_VERSION = "1.1";
const SCHEMA_VERSION = "1.0";

const args = parseArgs(process.argv.slice(2));
const summaryPath = args.summary;
const promptPath = args.prompt ?? "docs/ai-coverage-analysis-prompt.md";
const outPath = args.out ?? "docs/ai-coverage-analysis-result.json";
const model = args.model ?? process.env.OPENAI_MODEL ?? DEFAULT_MODEL;
const maxOutputTokens = Number(args["max-output-tokens"] ?? process.env.OPENAI_MAX_OUTPUT_TOKENS ?? 5000);
const dryRun = args["dry-run"] === "true" || args["dry-run"] === "1";

if (!summaryPath) {
  console.error(
    "Usage: node scripts/generate-ai-coverage-analysis.mjs --summary <path> [--prompt <path>] [--out <path>] [--model <model>] [--dry-run true]",
  );
  process.exit(1);
}

const summaryMarkdown = fs.readFileSync(summaryPath, "utf8");
const promptMarkdown = fs.readFileSync(promptPath, "utf8");

if (!Number.isInteger(maxOutputTokens) || maxOutputTokens <= 0) {
  console.error("--max-output-tokens must be a positive integer.");
  process.exit(1);
}

const requestBody = buildRequestBody({ promptMarkdown, summaryMarkdown, model, maxOutputTokens });

if (dryRun) {
  console.log(`AI coverage analysis request is valid: model=${model}, prompt=${promptPath}, summary=${summaryPath}`);
  process.exit(0);
}

const apiKey = process.env.OPENAI_API_KEY;
if (!apiKey) {
  console.error("OPENAI_API_KEY is required to generate AI coverage analysis.");
  process.exit(1);
}

const baseUrl = (process.env.OPENAI_BASE_URL ?? DEFAULT_BASE_URL).replace(/\/$/, "");
const response = await fetch(`${baseUrl}/responses`, {
  method: "POST",
  headers: {
    Authorization: `Bearer ${apiKey}`,
    "Content-Type": "application/json",
  },
  body: JSON.stringify(requestBody),
});

const responseText = await response.text();
if (!response.ok) {
  console.error(`OpenAI Responses API request failed: ${response.status} ${response.statusText}`);
  console.error(responseText);
  process.exit(1);
}

const responseJson = JSON.parse(responseText);
const outputText = collectOutputText(responseJson);
if (!outputText) {
  console.error("OpenAI Responses API response did not include output text.");
  process.exit(1);
}

const analysis = parseJsonOutput(outputText);
validateAnalysis(analysis);

fs.mkdirSync(path.dirname(outPath), { recursive: true });
fs.writeFileSync(outPath, `${JSON.stringify(analysis, null, 2)}\n`, "utf8");

console.log(`AI coverage analysis generated: ${outPath}`);

function parseArgs(rawArgs) {
  const parsed = {};
  for (let index = 0; index < rawArgs.length; index += 1) {
    const rawKey = rawArgs[index];
    if (!rawKey?.startsWith("--")) continue;

    const key = rawKey.replace(/^--/, "");
    const nextValue = rawArgs[index + 1];
    if (!nextValue || nextValue.startsWith("--")) {
      parsed[key] = "true";
    } else {
      parsed[key] = nextValue;
      index += 1;
    }
  }
  return parsed;
}

function buildRequestBody({ promptMarkdown, summaryMarkdown, model, maxOutputTokens }) {
  return {
    model,
    input: [
      {
        role: "developer",
        content: [
          {
            type: "input_text",
            text: [
              "You generate Leafy coverage analysis for CI artifacts.",
              "Return only valid JSON that matches the requested schema.",
              "Do not include markdown fences or commentary outside the JSON object.",
            ].join(" "),
          },
        ],
      },
      {
        role: "user",
        content: [
          {
            type: "input_text",
            text: [
              promptMarkdown,
              "",
              "## 실제 coverage-summary.md",
              "",
              "```md",
              summaryMarkdown,
              "```",
            ].join("\n"),
          },
        ],
      },
    ],
    max_output_tokens: maxOutputTokens,
    store: false,
    text: {
      format: {
        type: "json_schema",
        name: "leafy_ai_coverage_analysis",
        description: "AI analysis JSON rendered by the Leafy custom coverage report.",
        strict: true,
        schema: analysisSchema(),
      },
    },
  };
}

function analysisSchema() {
  const nullableNumber = ["number", "null"];
  const nullableInteger = ["integer", "null"];

  return {
    type: "object",
    additionalProperties: false,
    required: [
      "schemaVersion",
      "promptVersion",
      "overallSummary",
      "metrics",
      "riskAreas",
      "testCandidates",
      "deferredAreas",
      "requiredContext",
      "limitations",
      "suggestedIssues",
    ],
    properties: {
      schemaVersion: { type: "string", enum: [SCHEMA_VERSION] },
      promptVersion: { type: "string", enum: [PROMPT_VERSION] },
      overallSummary: {
        type: "object",
        additionalProperties: false,
        required: ["plainLanguageSummary", "technicalSummary", "oneLineSummary"],
        properties: {
          plainLanguageSummary: { type: "string" },
          technicalSummary: { type: "string" },
          oneLineSummary: { type: "string" },
        },
      },
      metrics: {
        type: "object",
        additionalProperties: false,
        required: ["instruction", "branch", "line", "method", "zeroLineCoverageClassCount"],
        properties: {
          instruction: metricSchema(nullableNumber, nullableInteger),
          branch: metricSchema(nullableNumber, nullableInteger),
          line: metricSchema(nullableNumber, nullableInteger),
          method: metricSchema(nullableNumber, nullableInteger),
          zeroLineCoverageClassCount: { type: nullableInteger },
        },
      },
      riskAreas: {
        type: "array",
        items: {
          type: "object",
          additionalProperties: false,
          required: [
            "rank",
            "priority",
            "featureArea",
            "technicalTarget",
            "layer",
            "lineCoveragePercent",
            "coveredLines",
            "missedLines",
            "riskSignal",
            "plainLanguageImpact",
            "evidenceLevel",
            "assumptions",
          ],
          properties: {
            rank: { type: "integer" },
            priority: { type: "string", enum: ["P0", "P1", "P2", "P3"] },
            featureArea: { type: "string" },
            technicalTarget: { type: "string" },
            layer: { type: "string" },
            lineCoveragePercent: { type: nullableNumber },
            coveredLines: { type: nullableInteger },
            missedLines: { type: nullableInteger },
            riskSignal: { type: "string" },
            plainLanguageImpact: { type: "string" },
            evidenceLevel: { type: "string", enum: ["low", "medium", "high"] },
            assumptions: stringArraySchema(),
          },
        },
      },
      testCandidates: {
        type: "array",
        items: {
          type: "object",
          additionalProperties: false,
          required: ["rank", "target", "scenario", "expectedResult", "testType", "reason"],
          properties: {
            rank: { type: "integer" },
            target: { type: "string" },
            scenario: { type: "string" },
            expectedResult: { type: "string" },
            testType: { type: "string" },
            reason: { type: "string" },
          },
        },
      },
      deferredAreas: {
        type: "array",
        items: {
          type: "object",
          additionalProperties: false,
          required: ["target", "reason", "revisitCondition"],
          properties: {
            target: { type: "string" },
            reason: { type: "string" },
            revisitCondition: { type: "string" },
          },
        },
      },
      requiredContext: stringArraySchema(),
      limitations: stringArraySchema(),
      suggestedIssues: {
        type: "array",
        items: {
          type: "object",
          additionalProperties: false,
          required: ["title", "acceptanceCriteria"],
          properties: {
            title: { type: "string" },
            acceptanceCriteria: stringArraySchema(),
          },
        },
      },
    },
  };
}

function metricSchema(nullableNumber, nullableInteger) {
  return {
    type: "object",
    additionalProperties: false,
    required: ["coveragePercent", "covered", "missed"],
    properties: {
      coveragePercent: { type: nullableNumber },
      covered: { type: nullableInteger },
      missed: { type: nullableInteger },
    },
  };
}

function stringArraySchema() {
  return {
    type: "array",
    items: { type: "string" },
  };
}

function collectOutputText(responseJson) {
  if (typeof responseJson.output_text === "string") {
    return responseJson.output_text;
  }

  const parts = [];
  for (const item of responseJson.output ?? []) {
    for (const content of item.content ?? []) {
      if (content.type === "output_text" && typeof content.text === "string") {
        parts.push(content.text);
      }
    }
  }
  return parts.join("\n");
}

function parseJsonOutput(outputText) {
  try {
    return JSON.parse(outputText);
  } catch (error) {
    console.error("AI coverage analysis output was not valid JSON.");
    console.error(error.message);
    process.exit(1);
  }
}

function validateAnalysis(analysis) {
  const missing = [
    "overallSummary",
    "metrics",
    "riskAreas",
    "testCandidates",
    "deferredAreas",
    "requiredContext",
    "limitations",
    "suggestedIssues",
  ].filter((key) => !(key in analysis));

  if (analysis.schemaVersion !== SCHEMA_VERSION || analysis.promptVersion !== PROMPT_VERSION || missing.length > 0) {
    console.error(
      `AI coverage analysis JSON failed validation: schemaVersion=${analysis.schemaVersion}, promptVersion=${analysis.promptVersion}, missing=${missing.join(", ")}`,
    );
    process.exit(1);
  }
}
