import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getTopics, startSession } from "../services/examService";

export default function SubjectTopicPicker() {
  const { subjectId } = useParams();
  const navigate = useNavigate();
  const [topics, setTopics] = useState([]);
  const [starting, setStarting] = useState(false);

  useEffect(() => {
    getTopics(subjectId).then((res) => setTopics(res.data));
  }, [subjectId]);

  async function begin(mode, topicId) {
    setStarting(true);
    try {
      const { data } = await startSession({ subjectId, topicId, mode });
      navigate(`/drill/session/${data.sessionId}`);
    } finally {
      setStarting(false);
    }
  }

  return (
    <div className="topic-picker">
      <h1>Choose how you want to practice</h1>

      <section>
        <button disabled={starting} onClick={() => begin("subject")}>
          Practice the whole subject
        </button>
        <button disabled={starting} onClick={() => begin("full-mock")}>
          Full timed mock (40 questions / 30 min)
        </button>
      </section>

      <h2>Or drill one topic</h2>
      <div className="topic-grid">
        {topics.map((t) => (
          <button
            key={t.id}
            className="topic-card"
            disabled={starting}
            onClick={() => begin("topic", t.id)}
          >
            <h3>{t.name}</h3>
            <p>{t.questionCount} questions</p>
          </button>
        ))}
      </div>
    </div>
  );
}
