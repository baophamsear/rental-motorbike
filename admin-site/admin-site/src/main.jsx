// src/main.jsx – Sửa lại như sau:
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App /> {/* Không cần Provider */}
  </React.StrictMode>,
)
