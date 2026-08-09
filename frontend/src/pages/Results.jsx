import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { getSessionResults } from "../services/examService";

export default function Results() {
  const { sessionId } = useParams();
  const navigate = useNavigate();
  const [results, setResults] = useState(null);

  useEffect(() => {
    getSessionResults(sessionId).then(({ data }) => setResults(data));
  }, [sessionId]);

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
                <button onClick={() => navigate(`/drill/${weakTopic.subjectId}/${weakTopic.topicId}`)}>
                  Redrill this topic
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
