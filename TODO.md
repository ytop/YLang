# YLang Improvement Backlog (Prioritized)

## High Priority
- Implement semantic analysis warnings in `backend/src/main/java/com/ylang/backend/service/CompilationService.java:150-161` (unused vars, type issues, unreachable code).
- Modernize ANTLR input to `CharStreams.fromString(...)` in `backend/src/main/java/com/ylang/backend/service/YLanguageParserService.java:34` and ensure updated runtime usage.
- Fix duplicate/conflicting visitor methods in TypeScript translator: `visitBlock` at `backend/src/main/java/com/ylang/backend/translator/TypeScriptTranslator.java:215` and `308`, `visitLoopStatement` at `159` and `478`, plus duplicate stubs for `visitAssignment`, `visitIfStatement`, `visitReturnStatement`.
- Fix duplicate/conflicting visitor methods in Rust translator: `visitBlock` at `backend/src/main/java/com/ylang/backend/translator/RustTranslator.java:224` and `321`, `visitLoopStatement` at `168` and `478`, plus duplicate stubs for `visitAssignment`, `visitIfStatement`, `visitReturnStatement`.
- Correct structure member generation referencing `statement` instead of `member` in structure visitors: `backend/src/main/java/com/ylang/backend/translator/TypeScriptTranslator.java:562` and `backend/src/main/java/com/ylang/backend/translator/RustTranslator.java:563`.
- Add controller endpoint tests for `backend/src/main/java/com/ylang/backend/controller/CompilationController.java` (status codes, JSON schema, error cases).

## Medium Priority
- Centralize CORS configuration: remove `@CrossOrigin` in controller and rely on properties `backend/src/main/resources/application.properties:13-17`.
- Improve error schema to include line/column positions; propagate arrays of errors to frontend (`frontend/src/services/api.ts:53-56` currently surfaces only first error).
- Add frontend tests (Vitest/RTL) for `frontend/src/components/LanguagePlayground.tsx` interactions and API service.
- Normalize TypeScript map/object translation to consistent `Record` or object literal semantics (`backend/src/main/java/com/ylang/backend/translator/TypeScriptTranslator.java:623-645`).
- Standardize health endpoint usage: prefer Actuator `/actuator/health` and update compose healthcheck (`docker-compose.yml`).

## Low Priority
- Optimize frontend Docker build: ensure dev deps available during build, add `.dockerignore`, review Nginx timeouts and security headers (`frontend/nginx.conf`).
- Enhance logging: structured logs, correlate by `projectId`, refine log levels in hot paths.
- Implement translator caching per config (`backend/src/main/resources/application.properties:30-31`) with LRU to avoid redundant translations.
- Documentation refresh: align `README.md` and examples with current endpoints and semantics.