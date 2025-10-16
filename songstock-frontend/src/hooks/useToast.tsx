import React, { createContext, useCallback, useContext, useMemo, useState } from 'react';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface Toast {
  id: string;
  message: string;
  type: ToastType;
  duration?: number;
  priority?: number;
  action?: { label: string; onClick?: () => void } | null;
}

interface ToastContextValue {
  toasts: Toast[];
  showToast: (message: string, type?: ToastType, duration?: number, options?: { priority?: number; action?: { label: string; onClick?: () => void } | null }) => void;
  removeToast: (id: string) => void;
  clearAllToasts: () => void;
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined);

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, setToasts] = useState<Toast[]>([]);

  const showToast = useCallback(
    (
      message: string,
      type: ToastType = 'info',
      duration: number = 5000,
      options?: { priority?: number; action?: { label: string; onClick?: () => void } | null }
    ) => {
      const id = Math.random().toString(36).slice(2, 11);
      const newToast: Toast = {
        id,
        message,
        type,
        duration,
        priority: options?.priority ?? 0,
        action: options?.action ?? null,
      };

      setToasts(prev => {
        // insert by priority (higher first)
        const copy = [...prev, newToast];
        copy.sort((a, b) => (b.priority ?? 0) - (a.priority ?? 0));
        return copy;
      });

      // keep console fallback for now
      if (type === 'error') console.error(message);
      else console.log(`[${type.toUpperCase()}] ${message}`);
    },
    []
  );

  const removeToast = useCallback((id: string) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  }, []);

  const clearAllToasts = useCallback(() => {
    setToasts([]);
  }, []);

  const value = useMemo(() => ({ toasts, showToast, removeToast, clearAllToasts }), [toasts, showToast, removeToast, clearAllToasts]);

  return <ToastContext.Provider value={value}>{children}</ToastContext.Provider>;
};

export const useToast = (): ToastContextValue => {
  const ctx = useContext(ToastContext);
  if (!ctx) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return ctx;
};
