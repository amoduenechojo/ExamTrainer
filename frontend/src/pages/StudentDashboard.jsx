import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getSubjects, getWeakTopics, startSession } from "../services/examService";

export default function StudentDashboard() {
  const navigate = useNavigate();
  const [subjects, setSubjects] = useState([]);
  const [weakTopics, setWeakTopics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [startingTopicId, setStartingTopicId] = useState(null);

  useEffect(() => {
    Promise.all([getSubjects(), getWeakTopics()])
      .then(([subjectsResponse, weakTopicsResponse]) => {
        setSubjects(subjectsResponse.data);
        setWeakTopics(weakTopicsResponse.data);
      })
      .finally(() => setLoading(false));
  }, []);

  async function handlePracticeNow(weakTopic) {
    setStartingTopicId(weakTopic.topicId);
    try {
      const { data } = await startSession({
        subjectId: weakTopic.subjectId,
        topicId: weakTopic.topicId,
        mode: "topic",
      });
      navigate(`/drill/session/${data.sessionId}`);
    } finally {
      setStartingTopicId(null);
    }
  }

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
            {weakTopics.map((weakTopic) => (
              <li key={weakTopic.topicId}>
                <span>
                  {weakTopic.topicName} ({weakTopic.subjectName}) — {weakTopic.accuracy}% accuracy
                </span>
                <button
                  disabled={startingTopicId === weakTopic.topicId}
                  onClick={() => handlePracticeNow(weakTopic)}
                >
                  {startingTopicId === weakTopic.topicId ? "Starting..." : "Practice now"}
                </button>
              </li>
            ))}
          </ul>
        </section>
      )}

      <section className="subject-grid">
        {subjects.map((subject) => (
          <button
            key={subject.id}
            className="subject-card"
            onClick={() => navigate(`/subjects/${subject.id}`)}
          >
            <h3>{subject.name}</h3>
            <p>{subject.topicCount} topics</p>
          </button>
        ))}
      </section>
    </div>
  );
}
