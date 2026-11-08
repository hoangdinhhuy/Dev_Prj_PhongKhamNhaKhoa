# AI inventory + TTS implementation checkpoints

## Checkpoint 1 — repository audit (DONE)
- Source task: `C:\Users\dinhh\Downloads\CODEX_AI_TTS_INVENTORY_IMPLEMENTATION.md` (not present at repo root).
- Read `README_RULES.md`, `README_THANH.md`, `README_DUY.md`, and acceptance scenario.
- Existing queue path: `HangCho_BUS.goiSo` returns CHO_KHAM without changing state; UI only changes local labels. No HTTP/WebSocket/SSE gateway or call event storage exists.
- Existing inventory: `HangHoa` UI/entity/DAO/BUS and `013_hanghoa.sql` + seed exist. Data has current stock and minimum only; no movement/consumption history.
- Existing AI helper files are deleted in working tree; preserve unrelated user changes and do not restore those files.
- API references checked: Google Cloud TTS REST/voice list and official Google GenAI Java SDK. Selected Neural2-A where available; fallback voice configurable.

## Checkpoint 1b — dependency/API decision (DONE)
- Official Google Gen AI Java SDK selected (`com.google.genai:google-genai`, Flash model config `GEMINI_MODEL`, default `gemini-flash-latest`; SDK supports JSON schema).
- Cloud TTS uses Google Auth Library ADC on gateway + official `text:synthesize` REST API; voice `vi-VN-Neural2-A` verified in current voice list.
- Gateway uses JDK `HttpServer` + SSE; no websocket framework or separate service infrastructure.

## Checkpoint 2 — TTS gateway + call flow (DONE — MOCK verified)
- Added JDK `HttpServer` gateway, read-only `/display`, `/api/snapshot`, SSE `/api/events`, protected `/api/call`, `/audio/{hash}.mp3`, `/health`.
- CALL/RECALL carries eventId, time, room, STT and synthesized phrase only. Dedupe + ordered queue; reconnect snapshot does not announce old calls. 001/025/100 spoken as Vietnamese words.
- TTS MOCK and `speechSynthesis` fallback; LIVE uses ADC + Cloud TTS REST, config voice/rate/volume and disk MP3 cache.
- JavaFX calls gateway asynchronously after `HangCho_BUS.goiSo`; Gọi lại rechecks CHO_KHAM. No state transition added.
- Local SSE test received `025`, room `P002`; display HTTP 200; duplicate event not repeated; bad call token 403.

## Checkpoint 3 — Gemini inventory (DONE — MOCK verified)
- Added AI button/result area to existing HangHoa page. JavaFX `Task` keeps UI responsive; BUS checks QUAN_LY and builds a six-field aggregate-only metric whitelist (including total minimum stock).
- Server sanitizes metrics/recomputes risk; official GenAI SDK Flash path uses server-only API key, JSON schema/validation, 15s request timeout and max 2 attempts for 429/5xx. Mock requires no key. No AI write endpoints.
- LIVE not run: no Gemini key/Google billing setup provided. Inventory movement history is absent, so no consumption trend/runway is reported.
- Local MOCK test: six-item payload with total minimum stock returned `MEDIUM`; unapproved `patientName` field was dropped; empty inventory returned `UNKNOWN`; wrong token 403.

## Checkpoint 4 — verification + docs (DONE with regression blocker)
- `mvn -q clean compile`: PASS.
- Gateway HTTP/SSE/authorization/dedupe/number words/JSON whitelist MOCK checks: PASS.
- `mvn -q exec:java '-Dexec.mainClass=main.TestRunner'`: FAIL/blocked at test T05. The working DB already contains generated patient key `BN26110001`; current shared code generates the same key, so T05 cannot create its test patient and dependent cases abort. Needs owner review of shared `BoDem_DAO`/legacy data; not changed here.
- GUI click-through, full DB regression after fixing the key collision, LIVE Google APIs, two-device LAN and physical speaker: NOT_RUN.
- Created `SETUP.md`, `TEST_CASES.md`, `DEMO.md`, `IMPLEMENTATION.md`; see tests for per-case status.
