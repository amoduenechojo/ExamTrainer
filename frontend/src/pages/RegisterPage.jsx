import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerStudent, registerParent } from "../services/authService";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [role, setRole] = useState("STUDENT");
  const [form, setForm] = useState({ fullName: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [inviteCode, setInviteCode] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      if (role === "STUDENT") {
        const { data } = await registerStudent(form);
        // Show the new student their invite code so they can share it with a parent.
        setInviteCode(data.inviteCode);
      } else {
        await registerParent(form);
        navigate("/login");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  if (inviteCode) {
    return (
      <div className="auth-page">
        <h1>You're all set</h1>
        <p>Share this invite code with a parent so they can follow your progress:</p>
        <p className="invite-code">{inviteCode}</p>
        <button onClick={() => navigate("/login")}>Continue to login</button>
      </div>
    );
  }

  return (
    <div className="auth-page">
      <h1>Create an account</h1>
      <div className="role-toggle">
        <button type="button" className={role === "STUDENT" ? "active" : ""} onClick={() => setRole("STUDENT")}>
          I'm a student
        </button>
        <button type="button" className={role === "PARENT" ? "active" : ""} onClick={() => setRole("PARENT")}>
          I'm a parent
        </button>
      </div>
      <form onSubmit={handleSubmit} className="auth-form">
        <label>
          Full name
          <input
            required
            value={form.fullName}
            onChange={(e) => setForm({ ...form, fullName: e.target.value })}
          />
        </label>
        <label>
          Email
          <input
            type="email"
            required
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
        </label>
        <label>
          Password
          <input
            type="password"
            required
            minLength={8}
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
        </label>
        {error && <p className="form-error">{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? "Creating account..." : "Create account"}
        </button>
      </form>
    </div>
  );
}
