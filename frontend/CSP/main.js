import { app, BrowserWindow } from 'electron'
import path from 'path'

const isDev = true;

function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: false,
    },
  })

  if (isDev) {
    // Während der Entwicklung: Vite-Server laden
    win.loadURL('http://localhost:5173')
  } else {
    // Nach dem Build: statische Dateien laden
    win.loadFile(path.join(__dirname, '../dist/index.html'))
  }
}

app.whenReady().then(createWindow)
