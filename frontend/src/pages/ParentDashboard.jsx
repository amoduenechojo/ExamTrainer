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
    api.get("/parents/me/students").then((response) => setStudents(response.data));
  }

  useEffect(() => {
    loadLinkedStudents();
  }, []);

  useEffect(() => {
    students.forEach((student) => {
      getStudentProgress(student.id).then((response) =>
        setProgressByStudent((prev) => ({ ...prev, [student.id]: response.data }))
      );
    });
  }, [students]);

  async function handleLink(event) {
    event.preventDefault();
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
            onChange={(event) => setInviteCode(event.target.value)}
          />
        </label>
        <button type="submit">Link</button>
        {linkError && <p className="form-error">{linkError}</p>}
      </form>

      {students.length === 0 && <p>No students linked yet — ask your child for their invite code.</p>}

      <section className="student-progress-list">
        {students.map((student) => {
          const progress = progressByStudent[student.id];
          return (
            <div key={student.id} className="student-progress-card">
              <h3>{student.fullName}</h3>
              {progress ? (
                <>
                  <p>Overall accuracy: {progress.overallAccuracy}%</p>
                  <p>Sessions completed: {progress.sessionsCompleted}</p>
                  <div>
                    <strong>Weakest topics</strong>
                    <ul>
                      {progress.weakTopics.map((weakTopic) => (
                        <li key={weakTopic.topicId}>
                          {weakTopic.topicName} ({weakTopic.subjectName}) — {weakTopic.accuracy}%
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
