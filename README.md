# Postutme Trainer

A subject → topic → question study platform for JAMB/Postutme candidates. Students drill by topic
or full subject, get instant scoring with a step-by-step correction and a topic-level shortcut on
every question, and a built-in Pomodoro timer helps structure the study session itself. Parents
can link to a student's account (via invite code) to follow their progress and weak-topic trends.

See `Postutme_Trainer_Design_Document.docx` for the full product/design writeup.

## Structure

```
postutme-trainer/
├── frontend/   React (Vite) — student & parent UI
└── backend/    Spring Boot (Java 21, Maven) — REST API, auth, scoring, weak-topic detection
```

## Backend — `backend/`

Requires Java 21 and Maven, plus a running PostgreSQL instance.

```bash
cd backend
cp .env.example .env   # then edit DB_USERNAME / DB_PASSWORD / JWT_SECRET
export $(cat .env | xargs)
mvn spring-boot:run
```

The API listens on `http://localhost:8080/api`. `GET /api/health` returns `{"status":"ok"}` once it's up.

Key packages:

- `model/` — JPA entities: `User`, `Student`, `Parent`, `ParentStudentLink`, `Subject`, `Topic`,
  `Shortcut`, `Question`, `QuestionOption`, `ExamSession`, `Attempt`.
- `repository/` — Spring Data JPA repositories.
- `service/` — `AuthService` (register/login, invite codes), `ExamService` (sessions, scoring,
  weak-topic detection), `ParentService` (invite-code linking, authorization).
- `security/` — the whole authentication layer, split by responsibility:
  - `security/config/` — `SecurityConfig` (filter chain, CORS, session policy) and
    `SecureBeanConfig` (`PasswordEncoder`, `AuthenticationManager`).
  - `security/dto/` — auth-only request/response shapes (`LoginRequest`,
    `RegisterStudentRequest`, `RegisterParentRequest`, `AuthResponse`), kept separate from the
    general `dto/` package used by the rest of the API.
  - `security/exception/` — `AppSecurityException` and `UnsupportedAuthenticationTypeException`.
  - `security/filter/` — `PostutmeTrainerAuthenticationFilter`, reads the JWT off each request.
  - `security/manager/` / `security/provider/` — a small custom `AuthenticationManager` +
    `AuthenticationProvider` pair that does the email/password/UserDetails check explicitly,
    instead of relying on Spring's default `DaoAuthenticationProvider`.
  - `security/service/` — `JwtService` (issue/validate tokens) and
    `PostutmeTrainerUserDetailsService`.
- `controller/` — REST endpoints consumed by the frontend.

`spring.jpa.hibernate.ddl-auto` is set to `update` for local development — swap to a real migration
tool (Flyway/Liquibase) plus `validate` before this goes anywhere near production data.

Nothing in `model/` is seeded yet — there's no Subject/Topic/Question data loaded. That's the next
real chunk of work: either a data loader (`CommandLineRunner`) or a small admin/import endpoint to
get your existing question bank in.

## Frontend — `frontend/`

```bash
cd frontend
cp .env.example .env   # points at the backend, defaults to localhost:8080
npm install
npm run dev
```

Key structure:

- `src/pages/` — `LoginPage`, `RegisterPage` (role toggle), `StudentDashboard`, `ParentDashboard`,
  `SubjectTopicPicker`, `Drill` (the click-to-answer flow with instant correction), `Results`, and
  `PomodoroTimer` (25/5/15-minute focus-break cycle with browser-notification reminders).
- `src/services/` — `api.js` (axios instance + JWT attach), `authService.js`, `examService.js`,
  `notificationService.js` (wraps the browser Notification API for Pomodoro reminders).
- `src/context/AuthContext.jsx` — holds the logged-in user/role app-wide.
- `src/components/ProtectedRoute.jsx` — route guard, optionally restricted by role.
- Linting is ESLint (flat config, `eslint.config.js`) — `npm run lint`.

The full-mock mode (`ExamMode.FULL_MOCK`) is the only timed drill mode — 30 minutes, capped at 40
questions — matching the real exam's pressure. Topic and subject drills are untimed; the Pomodoro
timer is a separate, opt-in study-session structure, not tied to any particular drill.

### Pomodoro timer

`PomodoroTimer.jsx` runs the classic cycle: 25 minutes focused, 5-minute break, and a 15-minute
break after every 4 focus sessions. When a phase ends it fires a browser notification (permission
requested on first "Start") plus an in-app banner as a fallback for when notifications are blocked
or unsupported. It's entirely client-side right now — nothing is sent to the backend, so session
counts reset on page reload. If you want that history to persist (e.g. showing it on the parent
dashboard too), that's backend work we haven't scoped yet.

## What's scaffolded vs. what's still a stub

**Working end-to-end:** student/parent registration and login (JWT, via a custom
`AuthenticationProvider`/`AuthenticationManager`), invite-code linking, listing subjects/topics,
starting a session, fetching questions, submitting an answer (with explanation + shortcut returned
immediately), completing a session, scoring, weak-topic detection (topic accuracy < 60% with at
least 3 attempts), and the Pomodoro study timer.

**Deliberately left for the next pass:**
- No question data loaded yet — the schema is ready, the content isn't in it.
- Weak-topic thresholds are hardcoded constants in `ExamService` — worth making configurable once
  we see real usage data.
- No refresh tokens — the JWT just expires after `app.jwt.expiration-minutes` (60 by default).
- No rate limiting or lockout on login attempts.
- No admin/CMS surface for entering questions — content currently has to go in via direct DB access
  or a script.
- Pomodoro session history isn't persisted to the backend — it's in-memory only, per browser tab.
- `npm run lint` currently reports false-positive `no-unused-vars` errors on every page that uses
  JSX component imports (e.g. `<LoginPage />`). Root cause: `eslint-plugin-react` isn't in the
  dependency set, and without it ESLint's core `no-unused-vars` rule doesn't recognize JSX tags as
  usage of the imported component. Not fixed yet — pending a decision on whether to add the plugin
  back in for that one behavior.
