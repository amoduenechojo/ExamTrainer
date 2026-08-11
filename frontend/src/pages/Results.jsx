import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { getSessionResults, startSession } from "../services/examService";

export default function Results() {
  const { sessionId } = useParams();
  const navigate = useNavigate();
  const [results, setResults] = useState(null);
  const [startingTopicId, setStartingTopicId] = useState(null);

  useEffect(() => {
    getSessionResults(sessionId).then(({ data }) => setResults(data));
  }, [sessionId]);

  async function handleRedrill(weakTopic) {
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

  if (!results) return <p className="loading">Scoring your session...</p>;

  return (
    <div className="results-page">
      <h1>
        {results.score} / {results.totalQuestions}
      </h1>
      <p>{results.percentage}% correct</p>

      {results.topicsToRevisit.length > 0 && (
        <section>
          <h2>Topics to revisit</h2>
          <ul>
            {results.topicsToRevisit.map((weakTopic) => (
              <li key={weakTopic.topicId}>
                <span>
                  {weakTopic.topicName} — {weakTopic.accuracy}%
                </span>
                <button
                  disabled={startingTopicId === weakTopic.topicId}
                  onClick={() => handleRedrill(weakTopic)}
                >
                  {startingTopicId === weakTopic.topicId ? "Starting..." : "Redrill this topic"}
                </button>
              </li>
            ))}
          </ul>
        </section>
      )}

      <Link to="/dashboard">Back to dashboard</Link>
    </div>
  );
}
