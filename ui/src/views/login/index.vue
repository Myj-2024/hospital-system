<template>
  <div class="login-container">
    <div class="glass-circle circle-1"></div>
    <div class="glass-circle circle-2"></div>

    <div class="login-box">
      <div class="login-banner">
        <img class="bg-img" src="/src/assets/images/background.png" alt="背景图片">

        <div class="banner-content">
          <div class="logo-area">
            <div class="logo-icon">
              <el-icon :size="40" color="#2d5af1">
                <OfficeBuilding/>
              </el-icon>
            </div>
            <div class="brand-info">
              <span class="brand-name">Smart Clinic</span>
              <span class="brand-ver">V2.0 专业版</span>
            </div>
          </div>

          <div class="title-group">
            <h1 class="main-title">智慧门诊</h1>
            <h1 class="sub-title">综合管理系统</h1>
          </div>

          <div class="accent-line"></div>

          <div class="english-slogan">
            <p>INTELLIGENT OUTPATIENT</p>
            <p>MANAGEMENT SYSTEM</p>
          </div>

          <div class="tag-list">
            <div class="tag-item"><span>● 高效诊疗</span></div>
            <div class="tag-item"><span>● 精准管控</span></div>
            <div class="tag-item"><span>● 安全可靠</span></div>
          </div>
        </div>
        <div class="banner-deco"></div>
      </div>

      <div class="login-form-section">
        <div class="form-header">
          <h2>账号登录</h2>
          <div class="title-bar"></div>
        </div>

        <el-tabs v-model="loginForm.role" class="role-tabs">
          <el-tab-pane label="医生端" name="doctor"></el-tab-pane>
          <el-tab-pane label="护士端" name="nurse"></el-tab-pane>
          <el-tab-pane label="管理员" name="admin"></el-tab-pane>
        </el-tabs>

        <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            size="large"
            class="custom-form"
        >
          <el-form-item prop="username">
            <el-input
                v-model="loginForm.username"
                placeholder="请输入登录账号"
                :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
            />
          </el-form-item>

          <el-form-item prop="captcha">
            <div class="captcha-row">
              <el-input
                  v-model="loginForm.captcha"
                  placeholder="验证码"
                  :prefix-icon="CircleCheck"
                  class="captcha-input-box"
              />
              <div class="captcha-img-wrapper" @click="refreshCaptcha">
                <img :src="captchaImg" alt="captcha"/>
              </div>
            </div>
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">自动登录</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>

          <el-button
              type="primary"
              class="submit-btn"
              :loading="loading"
              @click="handleLogin"
          >
            {{ loading ? '身份验证中...' : '立即登录' }}
          </el-button>
        </el-form>

        <div class="form-footer">
          <p>© 2026 智慧门诊医疗信息化管理平台 版权所有</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {ref, reactive, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/modules/user'
import {User, Lock, CircleCheck, OfficeBuilding} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import {getCaptcha} from '@/api/user/index.js'   // 🔥 新增

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const captchaImg = ref('')
const captchaKey = ref('')

const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  role: 'doctor'
})

const loginRules = {
  username: [{required: true, message: '请输入账号', trigger: 'blur'}],
  password: [{required: true, message: '请输入密码', trigger: 'blur'}],
  captcha: [{required: true, message: '请输入验证码', trigger: 'blur'}]
}

/**
 * 获取验证码
 */
const loadCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaImg.value = 'data:image/png;base64,' + res.data.image
    captchaKey.value = res.data.key
  } catch (e) {
    ElMessage.error('验证码加载失败')
  }
}

const refreshCaptcha = () => {
  loadCaptcha()
}
/**
 * 登录
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 调用 store 登录
        await userStore.login({
          ...loginForm,
          captchaKey: captchaKey.value
        })

        ElMessage.success('登录成功')

        // 🔥 关键修正点：补全父级路径 /admin
        // 必须与 router/index.js 中定义的完整路径完全一致
        const redirectPath = {
          doctor: '/admin/doctor/appointment',
          nurse: '/admin/nurse/queue',
          admin: '/admin/statistics'
        }

        // 获取目标路径，如果匹配不到则默认去看板
        const target = redirectPath[loginForm.role] || '/admin/statistics'

        console.log('正在跳转至：', target) // 调试用
        router.push(target)

      } catch (error) {
        console.error('登录失败详情：', error)
        refreshCaptcha() // 登录失败自动刷新验证码
      } finally {
        loading.value = false
      }
    }
  })
}
onMounted(() => {
  loadCaptcha()
})
</script>
<style>
/* 全屏容器及背景效果 */
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: #f7fafd;
  backdrop-filter: blur(40px);
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
}

/* 毛玻璃装饰圆 */
.glass-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(251, 170, 108, 0.15);
  backdrop-filter: blur(80px);
  z-index: 0;
}

.circle-1 {
  width: 500px;
  height: 500px;
  top: -100px;
  left: -100px;
}

.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  right: -50px;
  background: rgba(48, 244, 169, 0.15);
}

/* 登录主卡片 */
.login-box {
  width: 1100px;
  height: 650px;
  display: flex;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(40px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);
  z-index: 2;
  overflow: hidden;
}

/* 左侧 Banner */
.login-banner {
  flex: 1;
  background: linear-gradient(150deg, rgba(45, 123, 239, 0.7) 0%, rgba(239, 247, 253, 0.7) 70%);
  position: relative;
  display: flex;
  padding: 60px;
  overflow: hidden;
}

/* 修改点：图片作为背景铺满 */
.bg-img {
  position: absolute;
  top: 45%;
  left: 25%;
  width: 75%;
  z-index: 1;
  opacity: 0.08;
}

.banner-content {
  position: relative;
  z-index: 2;
  width: 100%;
}

.logo-area {
  display: flex;
  align-items: center;
  margin-bottom: 50px;
}

.logo-icon {
  width: 60px;
  height: 60px;
  background: #ffffff;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 8px 15px rgba(45, 90, 241, 0.1);
}

.brand-info {
  margin-left: 15px;
  display: flex;
  flex-direction: column;
}

.brand-name {
  color: #fff;
  font-size: 20px;
  font-weight: 700;
}

.brand-ver {
  color: #fff;
  font-size: 12px;
  margin-top: 2px;
}

.main-title, .sub-title {
  font-size: 40px;
  color: #38579a;
  font-weight: 800;
  line-height: 1.1;
  margin: 10px 0;
}

.accent-line {
  width: 240px;
  height: 2px;
  background: #38579a;
  border-radius: 3px;
  margin: 30px 0;
}

.english-slogan p {
  color: #38579a;
  font-size: 15px;
  letter-spacing: 3px;
  margin: 4px 0;
  font-weight: 600;
}

.tag-list {
  margin-top: 60px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.tag-item {
  color: #1d1e20;
  font-size: 16px;
}

.banner-deco {
  position: absolute;
  bottom: -50px;
  right: -50px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  z-index: 1;
}

/* 右侧表单区 */
.login-form-section {
  width: 500px;
  padding: 70px 65px;
  display: flex;
  flex-direction: column;
}

.form-header {
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 28px;
  color: #1e293b;
  font-weight: 700;
  margin: 0;
}

.title-bar {
  width: 30px;
  height: 4px;
  background: #2d5af1;
  margin-top: 10px;
  border-radius: 2px;
}

.role-tabs {
  margin-bottom: 30px;
}

.captcha-row {
  display: flex;
  width: 100%;
  gap: 12px;
  align-items: center;
}

.captcha-input-box {
  flex: 1;
}

.captcha-img-wrapper {
  width: 110px;
  height: 44px;
  cursor: pointer;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
}

.captcha-img-wrapper img {
  width: 90%;
  height: 90%;
  margin: auto;
}

.form-options {
  display: flex;
  justify-content: space-between;
  margin: 15px 0 35px;
}

.submit-btn {
  width: 100%;
  height: 52px;
  font-size: 17px;
  font-weight: 600;
  border-radius: 10px;
  background: #2d5af1;
  border: none;
  color: #ffffff;
  box-shadow: 0 10px 25px rgba(45, 90, 241, 0.25);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background: #1a46e6;
  transform: translateY(-1px);
}

.form-footer {
  margin-top: 20px;
  text-align: center;
}

.form-footer p {
  font-size: 12px;
  color: #94a3b8;
}
</style>