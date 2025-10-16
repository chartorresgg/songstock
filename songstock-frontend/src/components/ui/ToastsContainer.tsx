import React, { useEffect, useRef, useState } from 'react';
import { useToast, Toast } from '../../hooks/useToast';

const typeColor = (t: Toast['type']) => {
  switch (t) {
    case 'success': return 'bg-green-50 border-green-400 text-green-800';
    case 'error': return 'bg-red-50 border-red-400 text-red-800';
    case 'warning': return 'bg-yellow-50 border-yellow-400 text-yellow-800';
    case 'info':
    default:
      return 'bg-blue-50 border-blue-400 text-blue-800';
  }
};

const ToastItem: React.FC<{ t: Toast; onRemove: (id: string) => void }> = ({ t, onRemove }) => {
  const [visible, setVisible] = useState(false);
  const timerRef = useRef<number | null>(null);
  const startRef = useRef<number | null>(null);
  const remainingRef = useRef<number>(t.duration ?? 5000);

  const handleClose = () => {
    // animate out then remove after animation duration
    setVisible(false);
    window.setTimeout(() => onRemove(t.id), 220); // match transition duration (200ms) + margin
  };

  useEffect(() => {
    // entrance animation
    const id = window.setTimeout(() => setVisible(true), 10);
    // start auto-remove timer (will call handleClose to animate out)
    startRef.current = Date.now();
    timerRef.current = window.setTimeout(() => handleClose(), remainingRef.current);
    return () => {
      window.clearTimeout(id);
      if (timerRef.current) window.clearTimeout(timerRef.current);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleMouseEnter = () => {
    // pause timer
    if (timerRef.current) {
      window.clearTimeout(timerRef.current);
      timerRef.current = null;
      if (startRef.current) {
        const elapsed = Date.now() - startRef.current;
        remainingRef.current = Math.max((t.duration ?? 5000) - elapsed, 0);
      }
    }
  };

  const handleMouseLeave = () => {
    // resume timer
    startRef.current = Date.now();
    timerRef.current = window.setTimeout(() => handleClose(), remainingRef.current);
  };

  return (
    <div
      role="status"
      aria-live="polite"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      className={`max-w-sm w-full border-l-4 p-3 rounded shadow transform transition-all duration-200 ${visible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-2'} ${typeColor(t.type)}`}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="flex-1">
          <div className="font-semibold">{t.type === 'error' ? 'Error' : t.type === 'success' ? 'Éxito' : t.type}</div>
          <div className="text-sm mt-1">{t.message}</div>
          {t.action && (
            <div className="mt-2">
              <button
                className="text-sm font-medium text-blue-700 hover:underline"
                onClick={() => {
                  try { t.action?.onClick && t.action.onClick(); } catch (e) { console.error(e); }
                  onRemove(t.id);
                }}
              >
                {t.action.label}
              </button>
            </div>
          )}
        </div>
        <div>
          <button
            aria-label="Cerrar notificación"
            className="text-sm opacity-70 hover:opacity-100"
            onClick={handleClose}
          >
            ✕
          </button>
        </div>
      </div>
    </div>
  );
};

const ToastsContainer: React.FC = () => {
  const { toasts, removeToast } = useToast();

  if (!toasts || toasts.length === 0) return null;

  return (
    <div className="fixed right-4 bottom-4 z-50 flex flex-col gap-3 items-end">
      {toasts.map((t) => (
        <ToastItem key={t.id} t={t} onRemove={removeToast} />
      ))}
    </div>
  );
};

export default ToastsContainer;
