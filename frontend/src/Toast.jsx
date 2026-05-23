function Toast({ message, type = "success", onDismiss }) {
  return (
    <div className={`toast toast-${type}`} role="status" aria-live="polite">
      <span>{message}</span>
      <button
        type="button"
        className="toast-close"
        onClick={onDismiss}
        aria-label="Închide notificarea"
      >
        ×
      </button>
    </div>
  );
}

export default Toast;
