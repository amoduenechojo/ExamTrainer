# Postutme Trainer

A subject → topic → question study platform for JAMB/Postutme candidates. Students drill by topic
or full subject, get instant scoring with a step-by-step correction and a topic-level shortcut on
every question, and parents can link to a student's account (via invite code) to follow their
progress and weak-topic trends.

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
- `security/` — JWT issuing/validation and the Spring Security filter chain.
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
  `SubjectTopicPicker`, `Drill` (the click-to-answer flow with instant correction), `Results`.
- `src/services/` — `api.js` (axios instance + JWT attach), `authService.js`, `examService.js`.
- `src/context/AuthContext.jsx` — holds the logged-in user/role app-wide.
- `src/components/ProtectedRoute.jsx` — route guard, optionally restricted by role.

The full-mock mode (`ExamMode.FULL_MOCK`) is the only timed mode — 30 minutes, capped at 40
questions — matching the real exam's pressure. Topic and subject drills are untimed.

## What's scaffolded vs. what's still a stub

**Working end-to-end:** student/parent registration and login (JWT), invite-code linking, listing
subjects/topics, starting a session, fetching questions, submitting an answer (with explanation +
shortcut returned immediately), completing a session, scoring, and weak-topic detection (topic
accuracy < 60% with at least 3 attempts).

**Deliberately left for the next pass:**
- No question data loaded yet — the schema is ready, the content isn't in it.
- Weak-topic thresholds are hardcoded constants in `ExamService` — worth making configurable once
  we see real usage data.
- No refresh tokens — the JWT just expires after `app.jwt.expiration-minutes` (60 by default).
- No rate limiting or lockout on login attempts.
- No admin/CMS surface for entering questions — content currently has to go in via direct DB access
  or a script.
