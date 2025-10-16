import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './tailwind.css'
import './index.css'
import { ToastProvider } from './hooks/useToast'
import ToastsContainer from './components/ui/ToastsContainer'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ToastProvider>
      <App />
      <ToastsContainer />
    </ToastProvider>
  </React.StrictMode>,
)