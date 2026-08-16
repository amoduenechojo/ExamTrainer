import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";

import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import StudentDashboard from "./pages/StudentDashboard";
import ParentDashboard from "./pages/ParentDashboard";
import SubjectTopicPicker from "./pages/SubjectTopicPicker";
import Drill from "./pages/Drill";
import Results from "./pages/Results";
import PomodoroTimer from "./pages/PomodoroTimer";

import "./App.css";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route
            path="/dashboard"
            element={
              <ProtectedRoute allowedRoles={["STUDENT"]}>
                <StudentDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/parent"
            element={
              <ProtectedRoute allowedRoles={["PARENT"]}>
                <ParentDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/subjects/:subjectId"
            element={
              <ProtectedRoute allowedRoles={["STUDENT"]}>
                <SubjectTopicPicker />
              </ProtectedRoute>
            }
          />
          <Route
            path="/drill/session/:sessionId"
            element={
              <ProtectedRoute allowedRoles={["STUDENT"]}>
                <Drill />
              </ProtectedRoute>
            }
          />
          <Route
            path="/drill/session/:sessionId/results"
            element={
              <ProtectedRoute allowedRoles={["STUDENT"]}>
                <Results />
              </ProtectedRoute>
            }
          />
          <Route
            path="/pomodoro"
            element={
              <ProtectedRoute allowedRoles={["STUDENT"]}>
                <PomodoroTimer />
              </ProtectedRoute>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
