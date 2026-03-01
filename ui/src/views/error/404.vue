<template>
  <div class="not-found-container">
    <!-- 404数字动画 -->
    <div class="error-code">
      <span class="digit">4</span>
      <span class="digit zero">0</span>
      <span class="digit">4</span>
    </div>

    <!-- 提示文本 -->
    <div class="error-message">
      <h2>页面走丢了 😵</h2>
      <p>抱歉，你访问的页面不存在或已被删除</p>
    </div>

    <!-- 返回首页按钮 -->
    <el-button
        type="primary"
        size="large"
        class="back-home-btn"
        @click="goHome"
        icon="House"
    >
      返回首页
    </el-button>

    <!-- 装饰元素 -->
    <div class="decorations">
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { House } from '@element-plus/icons-vue'

const router = useRouter()

// 返回首页
const goHome = () => {
  router.push('/admin/dashboard')
}
</script>

<style scoped>
.not-found-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

/* 404数字样式 */
.error-code {
  font-size: 12rem;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.digit {
  animation: float 3s ease-in-out infinite;
  text-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.digit:nth-child(1) {
  animation-delay: 0s;
}

.zero {
  animation-delay: 0.5s;
  color: #ffd700;
}

.digit:nth-child(3) {
  animation-delay: 1s;
}

/* 浮动动画 */
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 提示文本 */
.error-message {
  text-align: center;
  color: white;
  margin-bottom: 40px;
  font-family: 'Arial', sans-serif;
}

.error-message h2 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.error-message p {
  font-size: 1.2rem;
  opacity: 0.8;
}

/* 返回首页按钮 */
.back-home-btn {
  padding: 15px 40px;
  font-size: 1.1rem;
  border-radius: 50px;
  background: white;
  color: #667eea;
  border: none;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.back-home-btn:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
  background: #f8f9fa;
}

/* 装饰粒子 */
.decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  animation: drift 8s linear infinite;
}

.particle:nth-child(1) {
  top: 10%;
  left: 20%;
  animation-delay: 0s;
  width: 8px;
  height: 8px;
}

.particle:nth-child(2) {
  top: 20%;
  left: 80%;
  animation-delay: 1s;
  width: 12px;
  height: 12px;
}

.particle:nth-child(3) {
  top: 70%;
  left: 30%;
  animation-delay: 2s;
  width: 6px;
  height: 6px;
}

.particle:nth-child(4) {
  top: 80%;
  left: 70%;
  animation-delay: 3s;
  width: 14px;
  height: 14px;
}

.particle:nth-child(5) {
  top: 50%;
  left: 50%;
  animation-delay: 4s;
  width: 10px;
  height: 10px;
}

@keyframes drift {
  0% {
    transform: translate(0, 0) rotate(0deg);
    opacity: 0.8;
  }
  100% {
    transform: translate(100px, -100px) rotate(360deg);
    opacity: 0;
  }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .error-code {
    font-size: 8rem;
  }

  .error-message h2 {
    font-size: 2rem;
  }

  .error-message p {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .error-code {
    font-size: 5rem;
  }
}
</style>