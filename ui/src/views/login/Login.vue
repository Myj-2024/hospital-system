<template>
  <div class="login-container" :class="[isDark ? 'dark-theme' : 'light-theme']">
    <div class="desktop-top-actions">
      <div class="action-pill">
        <div class="action-icon" @click="isDark = !isDark">
          <el-icon :size="16">
            <Sunny v-if="!isDark"/>
            <Moon v-else/>
          </el-icon>
        </div>
      </div>
    </div>

    <div class="mobile-top-bar">
      <el-button type="primary" size="small" icon="Back" @click="router.push('/home')"
                 class="mobile-back-btn">
        返回首页
      </el-button>
      <span class="mobile-brand-name">智慧门诊综合系统</span>
      <div class="action-icon mobile-theme-toggle" @click="isDark = !isDark">
        <el-icon :size="16">
          <Sunny v-if="!isDark"/>
          <Moon v-else/>
        </el-icon>
      </div>
    </div>

    <div class="login-box">
      <div class="login-left">
        <div class="left-content">
          <div class="logo-area">
            <div class="logo-icon-wrapper">
              <span class="med-symbol">⚕</span>
            </div>
            <h1 class="sys-title">智慧门诊综合管理系统</h1>
            <p class="sys-en-title">Smart Outpatient Management System</p>
          </div>

          <div class="feature-list">
            <div class="feature-item">
              <div class="feat-icon">
                <el-icon :size="18">
                  <UserFilled/>
                </el-icon>
              </div>
              <div class="feat-text">
                <div class="feat-title">全流程数字化</div>
                <div class="feat-desc">从挂号到结算，全链路智能闭环</div>
              </div>
            </div>
            <div class="feature-item">
              <div class="feat-icon">
                <el-icon :size="18">
                  <HelpFilled/>
                </el-icon>
              </div>
              <div class="feat-text">
                <div class="feat-title">数据安全保障</div>
                <div class="feat-desc">银行级加密，敏感信息自动脱敏</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="form-wrapper">
          <div class="welcome-text">
            <h2>欢迎登录</h2>
            <p>请使用您的医护人员账号或管理员账号登录</p>
          </div>

          <div class="role-container">
            <div class="input-label">登录角色</div>
            <div class="role-list">
              <div class="role-item" :class="{ active: loginRole === 'doctor' }" @click="loginRole = 'doctor'">
                <el-icon>
                  <User/>
                </el-icon>
                <span>医生</span>
              </div>
              <div class="role-item" :class="{ active: loginRole === 'nurse' }" @click="loginRole = 'nurse'">
                <el-icon>
                  <Avatar/>
                </el-icon>
                <span>护士</span>
              </div>
              <div class="role-item" :class="{ active: loginRole === 'admin' }" @click="loginRole = 'admin'">
                <el-icon>
                  <Management/>
                </el-icon>
                <span>管理员</span>
              </div>
            </div>
          </div>

          <el-form label-width="0" size="large" class="custom-form">
            <template v-if="loginMode === 'account'">
              <div class="input-label">工号 / 账号</div>
              <el-form-item class="custom-input-item">
                <el-input v-model="username" placeholder="请输入您的工号">
                  <template #prefix>
                    <el-icon>
                      <User/>
                    </el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <div class="input-label">密码</div>
              <el-form-item class="custom-input-item">
                <el-input v-model="password" type="password" placeholder="••••••••" show-password>
                  <template #prefix>
                    <el-icon>
                      <Lock/>
                    </el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </template>

            <el-form-item class="slider-form-item">
              <div class="slider-track" ref="sliderTrack">
                <div class="slider-fill" :style="{ width: sliderWidth + 'px' }"></div>
                <div class="slider-text" :class="{ 'is-passed': isPassed }">
                  {{ isPassed ? '验证通过' : '请按住滑块拖动' }}
                </div>
                <div
                    class="slider-btn"
                    @mousedown="onSliderStart"
                    @touchstart="onSliderStart"
                    :style="{ transform: `translateX(${sliderWidth}px)` }"
                    :class="{ 'is-passed': isPassed }"
                >
                  <el-icon v-if="!isPassed" :size="14">
                    <DArrowRight/>
                  </el-icon>
                  <el-icon v-else :size="14" style="color: #ffffff">
                    <CircleCheckFilled/>
                  </el-icon>
                </div>
              </div>
            </el-form-item>

            <div class="form-footer-actions">
              <el-checkbox v-model="rememberMe" class="custom-checkbox">记住我</el-checkbox>
              <el-link type="primary" underline="never" class="forget-pwd">忘记密码？</el-link>
            </div>

            <el-form-item class="submit-item">
              <el-button type="primary" @click="handleLogin" class="submit-btn" :disabled="!isPassed"
                         :loading="loginLoading">
                安全登录
              </el-button>
            </el-form-item>

          </el-form>
        </div>

        <div class="footer-copyright">
          © 2024 智慧医疗科技股份有限公司 版权所有<br>
          V2.0.1 Build 20240315
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onUnmounted} from 'vue'
import {useUserStore} from '@/store/user.js'
import {loginApi} from '@/api/login.js'
import {ElMessage} from 'element-plus'
import {useRouter} from 'vue-router'
import {
  DArrowRight,
  CircleCheckFilled,
  Sunny,
  Moon,
  User,
  Lock,
  Back,
  UserFilled,
  HelpFilled,
  Avatar,
  Management
} from '@element-plus/icons-vue'

const loginMode = ref('account')
const loginRole = ref('doctor') // 新增：绑定界面的登录角色状态
const username = ref('')
const password = ref('')
const rememberMe = ref(false)
const isDark = ref(false) // 默认采用亮色模式
const userStore = useUserStore()
const router = useRouter()

// --- 滑动验证逻辑 ---
const isPassed = ref(false)
const sliderWidth = ref(0)
const sliderTrack = ref(null)
const loginLoading = ref(false) // 定义 loading 状态
let isDragging = false
let startX = 0

const onSliderStart = (e) => {
  if (isPassed.value) return
  isDragging = true
  startX = (e.type === 'mousedown' ? e.clientX : e.touches[0].clientX) - sliderWidth.value
  window.addEventListener('mousemove', onSliderMove)
  window.addEventListener('touchmove', onSliderMove, {passive: false})
  window.addEventListener('mouseup', onSliderEnd)
  window.addEventListener('touchend', onSliderEnd)
}

const onSliderMove = (e) => {
  if (!isDragging) return
  if (e.type === 'touchmove') e.preventDefault()
  const currentX = e.type === 'mousemove' ? e.clientX : e.touches[0].clientX
  let moveX = currentX - startX
  // 减去滑块自身的宽度，保证滑块不溢出轨道
  const maxWidth = sliderTrack.value.offsetWidth - 42
  if (moveX < 0) moveX = 0
  if (moveX >= maxWidth) {
    moveX = maxWidth
    isPassed.value = true
    onSliderEnd()
  }
  sliderWidth.value = moveX
}

const onSliderEnd = () => {
  isDragging = false
  if (!isPassed.value) sliderWidth.value = 0
  window.removeEventListener('mousemove', onSliderMove)
  window.removeEventListener('touchmove', onSliderMove)
  window.removeEventListener('mouseup', onSliderEnd)
  window.removeEventListener('touchend', onSliderEnd)
}

// ================== 核心修改逻辑开始 ==================
/**
 * 判断是否为移动端环境
 */
const isMobileDevice = () => {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  if (!isPassed.value || loginLoading.value) return

  loginLoading.value = true
  try {
    const res = await loginApi({
      username: username.value,
      password: password.value,
      mode: loginMode.value
      // 业务如有需求，可将新增的 role 字段也传给后端：
      // role: loginRole.value
    })

    const {token, user, roles, permissions} = res.data

    // 存储用户信息
    userStore.setToken(token)
    userStore.setUserInfo(user)
    userStore.setRoles(roles)
    userStore.setPermissions(permissions)

    ElMessage.success('欢迎回来')

    setTimeout(() => {
      const isMobile = isMobileDevice()
      const redirectPath = router.currentRoute.value.query.redirect

      // 检查 roles 数组中是否包含 STAFF 或 WORKER
      const isStaffOrWorker = roles.some(role => ['ROLE_STAFF', 'ROLE_WORKER'].includes(role))

      if (isStaffOrWorker) {
        // 员工/工人角色，强制忽略 redirect，直接跳对应页面
        if (isMobile) {
          // 移动端：强制跳工单列表
          router.push('/m/worker/list')
        } else {
          // PC端：强制跳管理工作台，不使用redirect
          router.push('/home')
        }
        return
      }

      // 普通用户/企业角色，才走重定向逻辑
      router.push(redirectPath || '/home')

    }, 200)

  } catch (error) {
    isPassed.value = false
    sliderWidth.value = 0
    console.error('登录异常:', error)
    ElMessage.error('登录失败，请检查账号密码')
  } finally {
    loginLoading.value = false
  }
}

onUnmounted(() => onSliderEnd())
</script>

<style scoped>
/* ================== 主题基础变量 ================== */
.light-theme {
  --page-bg: #f0f2f5;
  --bg-left-gradient: linear-gradient(145deg, #4a7bba 0%, #164c8a 100%);
  --bg-right: #ffffff;
  --text-main: #1f2937;
  --text-sub: #6b7280;
  --input-bg: #ffffff;
  --input-border: #e5e7eb;
  --input-focus: #3b82f6;
  --slider-bg: #f9fafb;
  --slider-border: #e5e7eb;
  --slider-btn-bg: #ffffff;
  --slider-btn-icon: #9ca3af;
  --divider-color: #f3f4f6;
  --tool-bg: #ffffff;
  --tool-icon: #6b7280;
}

.dark-theme {
  --page-bg: #030712;
  --bg-left-gradient: linear-gradient(145deg, #1b2c47 0%, #121f32 100%);
  --bg-right: #111827;
  --text-main: #f9fafb;
  --text-sub: #9ca3af;
  --input-bg: #1f2937;
  --input-border: #374151;
  --input-focus: #3b82f6;
  --slider-bg: #1f2937;
  --slider-border: #374151;
  --slider-btn-bg: #374151;
  --slider-btn-icon: #9ca3af;
  --divider-color: #374151;
  --tool-bg: #1f2937;
  --tool-icon: #9ca3af;
}

/* ================== 基础整体布局 ================== */
.login-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  align-items: center;
  justify-content: center;
  background-color: var(--page-bg);
  transition: background-color 0.3s ease;
  overflow: hidden;
}

/* 核心卡片布局 */
.login-box {
  display: flex;
  width: 1200px;
  height: 90%;
  background: var(--bg-right);
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

/* ================== 左侧品牌信息区 ================== */
.login-left {
  width: 45%;
  background: var(--bg-left-gradient);
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 50px 40px;
}

.login-left::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('@/assets/ILLUSTRATION.png') no-repeat center center;
  background-size: 98%;
  opacity: 0.15;
  mix-blend-mode: overlay;
  z-index: 1;
}

.left-content {
  position: relative;
  z-index: 2;
  color: #ffffff;
}

.logo-area {
  margin-bottom: 50px;
  text-align: center;
}

.logo-icon-wrapper {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
  display: inline-block;
  line-height: 1;
}

.sys-title {
  font-size: 26px;
  font-weight: 600;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.sys-en-title {
  font-size: 13px;
  opacity: 0.8;
  margin: 0;
  letter-spacing: 0.5px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 30px;
  margin-top: 60px;
  padding-left: 20px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.feat-icon {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.feat-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 6px;
}

.feat-desc {
  font-size: 13px;
  opacity: 0.8;
}

/* ================== 右侧表单操作区 ================== */
.login-right {
  width: 55%;
  background-color: var(--bg-right);
  display: flex;
  flex-direction: column;
  position: relative;
  padding: 50px 0;
  align-items: center;
}

.form-wrapper {
  width: 100%;
  max-width: 380px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.welcome-text {
  text-align: center;
  margin: 20px 0;
}

.welcome-text h2 {
  color: var(--text-main);
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.welcome-text p {
  color: var(--text-sub);
  font-size: 14px;
  margin: 0;
}

/* 独立标签 */
.input-label {
  font-size: 13px;
  color: var(--text-sub);
  margin-bottom: 6px;
}

/* 角色选择 UI */
.role-container {
  margin-bottom: 20px;
}

.role-list {
  display: flex;
  gap: 12px;
}

.role-item {
  flex: 1;
  height: 44px;
  border: 1px solid var(--input-border);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-sub);
  transition: all 0.3s ease;
  background: var(--input-bg);
}

.role-item:hover {
  border-color: var(--input-focus);
}

.role-item.active {
  border-color: var(--input-focus);
  color: var(--input-focus);
  background: rgba(59, 130, 246, 0.05);
}

.dark-theme .role-item.active {
  background: rgba(59, 130, 246, 0.15);
}

/* 输入框定制化 */
.custom-input-item {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--input-focus) inset !important;
}

/* 自动填充样式覆盖 */
:deep(.el-input__inner:-webkit-autofill) {
  -webkit-text-fill-color: var(--text-main) !important;
}

.dark-theme :deep(.el-input__inner:-webkit-autofill) {
  -webkit-box-shadow: 0 0 0 1000px var(--bg-right) inset !important;
}

.light-theme :deep(.el-input__inner:-webkit-autofill) {
  -webkit-box-shadow: 0 0 0 1000px var(--bg-right) inset !important;
}

:deep(.el-input__inner::placeholder) {
  color: var(--text-sub);
}

:deep(.el-input-group__append) {
  background-color: var(--input-bg) !important;
  box-shadow: 0 0 0 1px var(--input-border) inset !important;
  border-left: none !important;
  border-top-right-radius: 6px;
  border-bottom-right-radius: 6px;
  padding: 0 15px;
}

.code-btn-custom {
  color: var(--input-focus) !important;
  font-size: 13px;
  text-align: center;
  margin: 0 auto;
}

/* ================== 滑块验证 ================== */
.slider-form-item {
  margin-bottom: 20px;
  margin-top: 10px;
}

.slider-track {
  background-color: var(--slider-bg);
  border: 1px solid var(--slider-border);
  height: 42px;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
  width: 100%;
}

.slider-fill {
  position: absolute;
  height: 100%;
  background: #10b981;
  opacity: 0.15;
  z-index: 1;
}

.slider-text {
  position: absolute;
  width: 100%;
  height: 100%;
  text-align: center;
  line-height: 40px;
  font-size: 13px;
  color: var(--text-sub);
  z-index: 2;
  user-select: none;
}

.slider-text.is-passed {
  color: #10b981;
}

.slider-btn {
  position: absolute;
  width: 42px;
  height: 42px;
  top: 0;
  left: 0;
  background: var(--slider-btn-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  z-index: 3;
  color: var(--slider-btn-icon);
  border-right: 1px solid var(--slider-border);
  transition: background 0.3s;
}

.slider-btn.is-passed {
  background: #10b981 !important;
}

/* ================== 底部操作区 ================== */
.form-footer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

:deep(.custom-checkbox .el-checkbox__label) {
  color: var(--text-sub);
  font-size: 13px;
}

.forget-pwd {
  font-size: 13px;
  color: var(--input-focus);
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 6px;
  font-size: 15px;
  background: #2b6cf2;
  border-color: #2b6cf2;
  letter-spacing: 2px;
}

.submit-btn:hover {
  background: #3b82f6;
  border-color: #3b82f6;
}

.footer-copyright {
  text-align: center;
  font-size: 12px;
  color: var(--text-sub);
  line-height: 1.6;
  margin-bottom: 20px

}

/* ================== 顶部小部件 ================== */
.desktop-top-actions {
  position: absolute;
  top: 30px;
  right: 40px;
  z-index: 10;
}

.action-pill {
  display: flex;
  width: 38px;
  height: 38px;
  align-items: center;
  background: var(--tool-bg);
  border-radius: 50%;
  padding: 10px;
  border: 1px solid var(--divider-color);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.action-icon {
  display: flex;
  cursor: pointer;
  color: var(--tool-icon);
  transition: color 0.3s;
}

.action-icon:hover {
  color: var(--text-main);
}

.mobile-top-bar {
  display: none;
}

/* ================== 响应式适配 ================== */
@media (max-width: 1050px) {
  .login-box {
    width: 90%;
    height: auto;
    min-height: 600px;
  }
}

@media (max-width: 768px) {
  .login-box {
    width: 100%;
    height: 100vh;
    border-radius: 0;
    box-shadow: none;
    flex-direction: column;
  }

  .login-left {
    display: none;
  }

  .login-right {
    width: 100%;
    flex: 1;
    padding: 20px;
    justify-content: center;
  }

  .desktop-top-actions {
    display: none;
  }

  .mobile-top-bar {
    display: flex;
    width: 100%;
    align-items: center;
    justify-content: space-between;
    padding: 15px 20px;
    background: var(--bg-right);
    box-sizing: border-box;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 100;
    border-bottom: 1px solid var(--divider-color);
  }

  .mobile-brand-name {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-main);
  }

  .form-wrapper {
    margin-top: 60px; /* 规避移动端头部 */
    max-width: 100%;
  }

  .footer-copyright {
    padding-bottom: env(safe-area-inset-bottom);
  }
}
</style>