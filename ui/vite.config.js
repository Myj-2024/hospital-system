import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  // 配置路径别名（方便开发）
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  // 配置自动打开浏览器
  server: {
    open: '/login', // 核心配置：启动后自动打开网页
    port: 5173, // 可选：指定端口（避免冲突）
    host: '0.0.0.0' // 可选：允许局域网访问，同时不影响自动打开本地页面
  },

})