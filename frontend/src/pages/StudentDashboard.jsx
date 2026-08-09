import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getSubjects, getWeakTopics } from "../services/examService";

export default function StudentDashboard() {
  const navigate = useNavigate();
  const [subjects, setSubjects] = useState([]);
  const [weakTopics, setWeakTopics] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getSubjects(), getWeakTopics()])
      .then(([subjectsRes, weakRes]) => {
        setSubjects(subjectsRes.data);
        setWeakTopics(weakRes.data);
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="loading">Loading your dashboard...</p>;

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Your subjects</h1>
        <button className="pomodoro-link" onClick={() => navigate("/pomodoro")}>
          Study timer
        </button>
      </div>

      {weakTopics.length > 0 && (
        <section className="weak-topics-banner">
          <h2>Focus on these first</h2>
          <ul>
            {weakTopics.map((t) => (
              <li key={t.topicId}>
                <span>
                  {t.topicName} ({t.subjectName}) — {t.accuracy}% accuracy
                </span>
                <button onClick={() => navigate(`/drill/${t.subjectId}/${t.topicId}`)}>
                  Practice now
                </button>
              </li>
            ))}
          </ul>
        </section>
      )}

      <section className="subject-grid">
        {subjects.map((s) => (
          <button
            key={s.id}
            className="subject-card"
            onClick={() => navigate(`/subjects/${s.id}`)}
          >
            <h3>{s.name}</h3>
            <p>{s.topicCount} topics</p>
          </button>
        ))}
      </section>
    </div>
  );
}
