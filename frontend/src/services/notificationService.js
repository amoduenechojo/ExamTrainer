// Thin wrapper around the browser Notification API. Falls back silently (no crash) on
// browsers/environments where Notification isn't available (e.g. some in-app webviews).

export function isNotificationSupported() {
  return typeof window !== "undefined" && "Notification" in window;
}

export async function requestNotificationPermission() {
  if (!isNotificationSupported()) return "unsupported";
  if (Notification.permission === "granted" || Notification.permission === "denied") {
    return Notification.permission;
  }
  return Notification.requestPermission();
}

// Fires a browser notification if permission was granted; otherwise this is a no-op —
// callers should also show an in-app banner so the reminder isn't silently lost.
export function notify(title, body) {
  if (!isNotificationSupported() || Notification.permission !== "granted") return;
  new Notification(title, { body, icon: "/favicon.svg" });
}
