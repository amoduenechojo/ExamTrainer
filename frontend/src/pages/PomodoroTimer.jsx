import { useEffect, useRef, useState } from "react";
import {
  requestNotificationPermission,
  notify,
  isNotificationSupported,
} from "../services/notificationService";

const WORK_SECONDS = 25 * 60;
const SHORT_BREAK_SECONDS = 5 * 60;
const LONG_BREAK_SECONDS = 15 * 60;
const SESSIONS_BEFORE_LONG_BREAK = 4;

const PHASE_LABELS = {
  work: "Focus",
  shortBreak: "Short break",
  longBreak: "Long break",
};

function durationFor(phase) {
  if (phase === "work") return WORK_SECONDS;
  if (phase === "shortBreak") return SHORT_BREAK_SECONDS;
  return LONG_BREAK_SECONDS;
}

export default function PomodoroTimer() {
  const [phase, setPhase] = useState("work");
  const [secondsLeft, setSecondsLeft] = useState(WORK_SECONDS);
  const [isRunning, setIsRunning] = useState(false);
  const [completedWorkSessions, setCompletedWorkSessions] = useState(0);
  const [banner, setBanner] = useState(null);
  const [notificationPermission, setNotificationPermission] = useState(
    isNotificationSupported() ? Notification.permission : "unsupported"
  );

  // Kept in a ref so the interval effect doesn't need to re-run every time these change.
  const stateRef = useRef({ phase, completedWorkSessions });
  stateRef.current = { phase, completedWorkSessions };

  useEffect(() => {
    if (!isRunning) return;
    if (secondsLeft <= 0) {
      advancePhase();
      return;
    }
    const timer = setTimeout(() => setSecondsLeft((s) => s - 1), 1000);
    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isRunning, secondsLeft]);

  function advancePhase() {
    const { phase: currentPhase, completedWorkSessions: currentCount } = stateRef.current;

    if (currentPhase === "work") {
      const newCount = currentCount + 1;
      const nextPhase = newCount % SESSIONS_BEFORE_LONG_BREAK === 0 ? "longBreak" : "shortBreak";
      setCompletedWorkSessions(newCount);
      setPhase(nextPhase);
      setSecondsLeft(durationFor(nextPhase));
      remind(
        "Focus session done",
        nextPhase === "longBreak"
          ? "Four sessions done — take a proper 15-minute break."
          : "Nice work. Take a 5-minute break."
      );
    } else {
      setPhase("work");
      setSecondsLeft(WORK_SECONDS);
      remind("Break's over", "Back to studying when you're ready.");
    }

    setIsRunning(false); // require a conscious "Start" for the next phase
  }

  function remind(title, body) {
    notify(title, body);
    setBanner(`${title} — ${body}`);
  }

  async function handleStart() {
    if (notificationPermission === "default") {
      const result = await requestNotificationPermission();
      setNotificationPermission(result);
    }
    setBanner(null);
    setIsRunning(true);
  }

  function handlePause() {
    setIsRunning(false);
  }

  function handleReset() {
    setIsRunning(false);
    setPhase("work");
    setSecondsLeft(WORK_SECONDS);
    setCompletedWorkSessions(0);
    setBanner(null);
  }

  function handleSkip() {
    setSecondsLeft(0);
    advancePhase();
  }

  const minutes = Math.floor(secondsLeft / 60).toString().padStart(2, "0");
  const seconds = (secondsLeft % 60).toString().padStart(2, "0");

  return (
    <div className="pomodoro-page">
      <h1>Study timer</h1>
      <p className="pomodoro-subtitle">
        25 minutes focused, 5 minutes off — a long break after every 4 rounds.
      </p>

      {notificationPermission === "denied" && (
        <p className="pomodoro-notice">
          Notifications are blocked, so reminders will only show up in this tab — enable them in
          your browser settings if you want an alert even when you've switched tabs.
        </p>
      )}

      {banner && <p className="pomodoro-banner">{banner}</p>}

      <div className={`pomodoro-clock pomodoro-clock--${phase}`}>
        <span className="pomodoro-phase-label">{PHASE_LABELS[phase]}</span>
        <span className="pomodoro-time">
          {minutes}:{seconds}
        </span>
      </div>

      <div className="pomodoro-controls">
        {isRunning ? (
          <button onClick={handlePause}>Pause</button>
        ) : (
          <button onClick={handleStart}>{secondsLeft === durationFor(phase) ? "Start" : "Resume"}</button>
        )}
        <button onClick={handleSkip} className="secondary">
          Skip
        </button>
        <button onClick={handleReset} className="secondary">
          Reset
        </button>
      </div>

      <p className="pomodoro-count">Focus sessions completed today: {completedWorkSessions}</p>
    </div>
  );
}
