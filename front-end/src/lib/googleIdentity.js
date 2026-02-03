let googleScriptPromise;

export function loadGoogleScript() {
  if (googleScriptPromise) {
    return googleScriptPromise;
  }

  googleScriptPromise = new Promise((resolve, reject) => {
    const existing = document.querySelector('script[src="https://accounts.google.com/gsi/client"]');
    if (existing) {
      resolve();
      return;
    }

    const script = document.createElement("script");
    script.src = "https://accounts.google.com/gsi/client";
    script.async = true;
    script.defer = true;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error("Failed to load Google Identity script"));
    document.head.appendChild(script);
  });

  return googleScriptPromise;
}

export function renderGoogleButton({ clientId, container, onCredential }) {
  if (!window.google?.accounts?.id) {
    throw new Error("Google Identity is not available");
  }
  if (!container) {
    throw new Error("Google button container is missing");
  }

  window.google.accounts.id.initialize({
    client_id: clientId,
    callback: (response) => {
      if (response?.credential) {
        onCredential(response.credential);
      }
    },
  });

  container.innerHTML = "";
  window.google.accounts.id.renderButton(container, {
    theme: "outline",
    size: "large",
    width: 380,
    text: "continue_with",
    shape: "rectangular",
  });
}
