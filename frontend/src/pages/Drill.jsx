import { useEffect, useMemo, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getSessionQuestions, submitAnswer, completeSession } from "../services/examService";

const MOCK_DURATION_SECONDS = 30 * 60; // 30 minutes, only used when session.mode === "full-mock"

export default function Drill() {
  const { sessionId } = useParams();
  const navigate = useNavigate();

  const [questions, setQuestions] = useState([]);
  const [index, setIndex] = useState(0);
  const [selectedOptionId, setSelectedOptionId] = useState(null);
  const [feedback, setFeedback] = useState(null); // { isCorrect, correctOptionId, explanation, shortcut }
  const [isTimed, setIsTimed] = useState(false);
  const [secondsLeft, setSecondsLeft] = useState(MOCK_DURATION_SECONDS);
  const [submitting, setSubmitting] = useState(false);

  const currentQuestion = questions[index];

  useEffect(() => {
    getSessionQuestions(sessionId).then(({ data }) => {
      setQuestions(data.questions);
      setIsTimed(data.mode === "full-mock");
    });
  }, [sessionId]);

  // Countdown only applies to full-mock sessions.
  useEffect(() => {
    if (!isTimed) return;
    if (secondsLeft <= 0) {
      finishSession();
      return;
    }
    const timer = setTimeout(() => setSecondsLeft((previousSeconds) => previousSeconds - 1), 1000);
    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isTimed, secondsLeft]);

  const timeLabel = useMemo(() => {
    const m = Math.floor(secondsLeft / 60).toString().padStart(2, "0");
    const s = (secondsLeft % 60).toString().padStart(2, "0");
    return `${m}:${s}`;
  }, [secondsLeft]);

  async function handleAnswer(optionId) {
    if (feedback || submitting) return; // already answered this question
    setSelectedOptionId(optionId);
    setSubmitting(true);
    try {
      const { data } = await submitAnswer(sessionId, currentQuestion.id, optionId);
      setFeedback(data); // { isCorrect, correctOptionId, explanation, shortcut }
    } finally {
      setSubmitting(false);
    }
  }

  function nextQuestion() {
    setSelectedOptionId(null);
    setFeedback(null);
    if (index + 1 < questions.length) {
      setIndex(index + 1);
    } else {
      finishSession();
    }
  }

  async function finishSession() {
    await completeSession(sessionId);
    navigate(`/drill/session/${sessionId}/results`);
  }

  if (!currentQuestion) return <p className="loading">Loading questions...</p>;

  return (
    <div className="drill-page">
      <div className="drill-header">
        <span>
          Question {index + 1} of {questions.length}
        </span>
        {isTimed && <span className="timer">{timeLabel}</span>}
      </div>

      <h2 className="question-stem">{currentQuestion.stem}</h2>

      <div className="options">
        {currentQuestion.options.map((opt) => {
          const isSelected = selectedOptionId === opt.id;
          const isCorrectOption = feedback && opt.id === feedback.correctOptionId;
          const isWrongSelected = feedback && isSelected && !feedback.isCorrect;

          return (
            <button
              key={opt.id}
              className={
                "option-button" +
                (isCorrectOption ? " correct" : "") +
                (isWrongSelected ? " incorrect" : "") +
                (isSelected && !feedback ? " selected" : "")
              }
              onClick={() => handleAnswer(opt.id)}
              disabled={!!feedback}
            >
              {opt.text}
            </button>
          );
        })}
      </div>

      {feedback && (
        <div className="feedback-panel">
          <p className={feedback.isCorrect ? "feedback-correct" : "feedback-incorrect"}>
            {feedback.isCorrect ? "Correct!" : "Not quite."}
          </p>
          <div>
            <strong>Step-by-step</strong>
            <p>{feedback.explanation}</p>
          </div>
          {feedback.shortcut && (
            <div>
              <strong>Shortcut</strong>
              <p>{feedback.shortcut}</p>
            </div>
          )}
          <button onClick={nextQuestion}>
            {index + 1 < questions.length ? "Next question" : "See my results"}
          </button>
        </div>
      )}
    </div>
  );
}
