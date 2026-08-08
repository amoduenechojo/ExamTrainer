import { useEffect, useState } from "react";
import { linkToStudent } from "../services/authService";
import { getStudentProgress } from "../services/examService";
import api from "../services/api";

export default function ParentDashboard() {
  const [students, setStudents] = useState([]);
  const [inviteCode, setInviteCode] = useState("");
  const [linkError, setLinkError] = useState("");
  const [progressByStudent, setProgressByStudent] = useState({});

  function loadLinkedStudents() {
    api.get("/parents/me/students").then((res) => setStudents(res.data));
  }

  useEffect(() => {
    loadLinkedStudents();
  }, []);

  useEffect(() => {
    students.forEach((s) => {
      getStudentProgress(s.id).then((res) =>
        setProgressByStudent((prev) => ({ ...prev, [s.id]: res.data }))
      );
    });
  }, [students]);

  async function handleLink(e) {
    e.preventDefault();
    setLinkError("");
    try {
      await linkToStudent(inviteCode);
      setInviteCode("");
      loadLinkedStudents();
    } catch (err) {
      setLinkError(err.response?.data?.message || "That invite code didn't work.");
    }
  }

  return (
    <div className="dashboard">
      <h1>Your children</h1>

      <form onSubmit={handleLink} className="link-student-form">
        <label>
          Link a student
          <input
            placeholder="Enter invite code"
            value={inviteCode}
            onChange={(e) => setInviteCode(e.target.value)}
          />
        </label>
        <button type="submit">Link</button>
        {linkError && <p className="form-error">{linkError}</p>}
      </form>

      {students.length === 0 && <p>No students linked yet — ask your child for their invite code.</p>}

      <section className="student-progress-list">
        {students.map((s) => {
          const progress = progressByStudent[s.id];
          return (
            <div key={s.id} className="student-progress-card">
              <h3>{s.fullName}</h3>
              {progress ? (
                <>
                  <p>Overall accuracy: {progress.overallAccuracy}%</p>
                  <p>Sessions completed: {progress.sessionsCompleted}</p>
                  <div>
                    <strong>Weakest topics</strong>
                    <ul>
                      {progress.weakTopics.map((t) => (
                        <li key={t.topicId}>
                          {t.topicName} ({t.subjectName}) — {t.accuracy}%
                        </li>
                      ))}
                    </ul>
                  </div>
                </>
              ) : (
                <p className="loading">Loading progress...</p>
              )}
            </div>
          );
        })}
      </section>
    </div>
  );
}
