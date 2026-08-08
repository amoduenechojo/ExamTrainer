import api from "./api";

export function getSubjects() {
  return api.get("/subjects");
}

export function getTopics(subjectId) {
  return api.get(`/subjects/${subjectId}/topics`);
}

// mode: "topic" | "subject" | "full-mock"
export function startSession({ subjectId, topicId, mode }) {
  return api.post("/sessions", { subjectId, topicId, mode });
}

export function getSessionQuestions(sessionId) {
  return api.get(`/sessions/${sessionId}/questions`);
}

// Submits one answer and immediately returns correctness + explanation + shortcut.
export function submitAnswer(sessionId, questionId, chosenOptionId) {
  return api.post(`/sessions/${sessionId}/answers`, {
    questionId,
    chosenOptionId,
  });
}

export function completeSession(sessionId) {
  return api.post(`/sessions/${sessionId}/complete`);
}

export function getSessionResults(sessionId) {
  return api.get(`/sessions/${sessionId}/results`);
}

// Per-topic accuracy for the logged-in student, used to flag weak topics.
export function getWeakTopics() {
  return api.get("/students/me/weak-topics");
}

// Parent-facing: progress for a specific linked student.
export function getStudentProgress(studentId) {
  return api.get(`/parents/me/students/${studentId}/progress`);
}
