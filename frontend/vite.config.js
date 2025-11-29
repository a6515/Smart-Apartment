import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
// 引入刚刚添加的自动导入插件
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    vue(),
    // 自动导入 Vue 相关函数 (如 ref, computed)
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    // 自动导入 Element Plus 组件 (如 ElButton)
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 3000,
    hmr: {
      overlay: false
    },
    proxy: {
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        timeout: 120000
      },
      '/ws': {
        target: 'http://localhost:9090',
        ws: true,
        changeOrigin: true,
        timeout: 300000
      }
    }
  },
  // 解决 sockjs 在浏览器端的兼容问题
  define: {
    global: 'window' 
  }
})