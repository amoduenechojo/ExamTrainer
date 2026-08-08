import api from "./api";

// Student sign-up. Backend returns the created profile plus that student's invite code.
export function registerStudent(payload) {
  return api.post("/auth/register/student", payload);
}

// Parent sign-up. Linking to a student happens separately via the invite code.
export function registerParent(payload) {
  return api.post("/auth/register/parent", payload);
}

export function login(credentials) {
  return api.post("/auth/login", credentials);
}

export function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("role");
}

// Parent enters a student's invite code to link accounts.
export function linkToStudent(inviteCode) {
  return api.post("/parents/me/link", { inviteCode });
}
