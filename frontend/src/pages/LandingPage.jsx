import { Link } from "react-router-dom";
import "./LandingPage.css";

const OPTION_LABELS = ["A", "B", "C", "D"];

const DIFFERENTIATORS = [
  {
    label: "Where questions come from",
    detail:
      "Authored or rights-cleared, checked by a subject-competent human before it ships — not copied from a scanned PDF with an uncorrected answer key.",
  },
  {
    label: "What you get after you answer",
    detail:
      "A full step-by-step explanation, plus the shortcut method for that topic — not just a letter and a checkmark.",
  },
  {
    label: "How you practice",
    detail:
      "Drill one topic on demand, a whole subject, or a timed 40-question mock — not a fixed PDF order you can't break out of.",
  },
];

const STEPS = [
  {
    number: "01",
    title: "Pick a subject, or one topic inside it",
    detail: "Mathematics, Biology, Chemistry, Physics, English — drill everything, or just Electrolysis.",
  },
  {
    number: "02",
    title: "Answer, get corrected instantly",
    detail: "Every question comes back with the right option, the full working, and the shortcut method.",
  },
  {
    number: "03",
    title: "See exactly where you're weak",
    detail: "Topic accuracy is tracked as you go, so the app tells you what to drill next — not the other way round.",
  },
  {
    number: "04",
    title: "Simulate the real thing",
    detail: "40 questions, 30 minutes, when you're ready to test yourself under the same pressure as exam day.",
  },
];

const SUBJECTS = ["Mathematics", "Biology", "Chemistry", "Physics", "English"];

export default function LandingPage() {
  return (
    <div className="landing">
      <header className="landing-nav">
        <span className="landing-wordmark">Postutme Trainer</span>
        <nav className="landing-nav-links">
          <Link to="/login">Log in</Link>
          <Link to="/register" className="landing-nav-cta">
            Get started
          </Link>
        </nav>
      </header>

      <section className="landing-hero">
        <div className="landing-hero-copy">
          <p className="landing-eyebrow">JAMB · Post-UTME</p>
          <h1>
            40 questions.
            <br />
            30 minutes.
            <br />
            Zero guessing.
          </h1>
          <p className="landing-hero-sub">
            Drill real past-question topics and get the correct answer explained step by step —
            plus the shortcut, so you're not still working it out when time's up.
          </p>
          <div className="landing-hero-actions">
            <Link to="/register" className="landing-btn landing-btn-primary">
              Start drilling — it's free
            </Link>
            <Link to="/register" className="landing-btn landing-btn-ghost">
              I'm a parent
            </Link>
          </div>
        </div>

        <div className="landing-hero-visual" aria-hidden="true">
          <div className="bubble-sheet">
            <div className="bubble-sheet-row bubble-sheet-question">
              <span className="bubble-sheet-qnum">Q17</span>
              {OPTION_LABELS.map((label) => (
                <span key={label} className={`bubble bubble-${label === "C" ? "filled" : "empty"}`}>
                  {label}
                </span>
              ))}
            </div>
            <div className="bubble-sheet-feedback">
              <span className="bubble-sheet-check">✓ Correct</span>
              <span className="bubble-sheet-shortcut">Shortcut: eliminate A &amp; D first — both break the ratio</span>
            </div>
          </div>
        </div>
      </section>

      <section className="landing-section landing-proof">
        <p className="landing-eyebrow">Why this, not another PDF</p>
        <div className="landing-proof-grid">
          {DIFFERENTIATORS.map((item) => (
            <div key={item.label} className="landing-proof-card">
              <h3>{item.label}</h3>
              <p>{item.detail}</p>
            </div>
          ))}
        </div>
      </section>

      <section className="landing-section landing-how">
        <p className="landing-eyebrow">How it works</p>
        <div className="landing-steps">
          {STEPS.map((step) => (
            <div key={step.number} className="landing-step">
              <span className="landing-step-number">{step.number}</span>
              <h3>{step.title}</h3>
              <p>{step.detail}</p>
            </div>
          ))}
        </div>
      </section>

      <section className="landing-section landing-subjects">
        <p className="landing-eyebrow">Subjects on the platform today</p>
        <div className="landing-subject-list">
          {SUBJECTS.map((subject) => (
            <span key={subject} className="landing-subject-pill">
              {subject}
            </span>
          ))}
        </div>
      </section>

      <section className="landing-section landing-extras">
        <div className="landing-extra-card">
          <h3>A study timer built in</h3>
          <p>
            25 minutes focused, 5-minute break, a longer one every 4 rounds — with a reminder when
            it's time to switch, so the studying itself stays structured too.
          </p>
        </div>
        <div className="landing-extra-card">
          <h3>Parents can follow along</h3>
          <p>
            Link a parent account with an invite code and they'll see progress and weak topics —
            without needing your login.
          </p>
        </div>
      </section>

      <section className="landing-cta-final">
        <h2>Stop guessing which answer key to trust.</h2>
        <Link to="/register" className="landing-btn landing-btn-primary">
          Create your free account
        </Link>
      </section>

      <footer className="landing-footer">
        <span>Postutme Trainer</span>
        <span>Built for JAMB &amp; Post-UTME candidates.</span>
      </footer>
    </div>
  );
}
