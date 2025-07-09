// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [
    vue()
  ],
  server: {
    proxy: {
      // Si vous utilisez un proxy vers Spring Boot
      '/pros': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/matches': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/playermatches': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true
      }
    }
  }
})
