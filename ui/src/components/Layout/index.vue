<template>
  <div class="layout-page">
    <el-container class="modern-layout">
      <el-aside width="240px" class="aside-container">
        <div class="brand">
          <div class="logo-box">M</div>
          <span class="brand-text">Medico<span class="sub">智慧门诊</span></span>
        </div>

        <el-scrollbar>
          <el-menu
              :default-active="$route.path"
              router
              class="custom-menu"
              background-color="transparent"
              text-color="#b8becc"
              active-text-color="#ffffff"
          >
            <el-menu-item index="/admin/statistics">
              <el-icon>
                <DataLine/>
              </el-icon>
              <span>数据看板</span>
            </el-menu-item>

            <div class="menu-label">核心业务</div>

            <el-sub-menu index="doctor">
              <template #title>
                <el-icon>
                  <FirstAidKit/>
                </el-icon>
                <span>医生工作台</span>
              </template>
              <el-menu-item index="/admin/doctor/appointment">接诊列表</el-menu-item>
              <el-menu-item index="/admin/doctor/diagnosis">医生接诊</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="nurse">
              <template #title>
                <el-icon>
                  <Bell/>
                </el-icon>
                <span>护士叫号站</span>
              </template>
              <el-menu-item index="/admin/nurse/queue">叫号大屏</el-menu-item>
              <el-menu-item index="/admin/nurse/prescription">处方管理</el-menu-item>
            </el-sub-menu>

            <div class="menu-label">系统配置</div>

            <el-sub-menu index="system">
              <template #title>
                <el-icon>
                  <Setting/>
                </el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/admin/system/doctor">医生账号</el-menu-item>
              <el-menu-item index="/admin/system/nurse">护士账号</el-menu-item>
              <el-menu-item index="/admin/system/role">权限配置</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-scrollbar>
      </el-aside>

      <el-container class="main-wrapper">
        <el-header class="glass-header">
          <div class="header-left">
            <el-breadcrumb separator-icon="ArrowRight">
              <el-breadcrumb-item>控制台</el-breadcrumb-item>
              <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <div class="status-tag">
              <span class="dot"></span> 实时运行中
            </div>
            <el-divider direction="vertical"/>
            <el-dropdown @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="32" src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"/>
                <span class="username">Admin</span>
                <el-icon>
                  <ArrowDown/>
                </el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="content-body">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component"/>
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/modules/user'
import {
  DataLine, FirstAidKit, Bell, Setting, ArrowRight, ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.modern-layout {
  height: 100vh;
  background-color: #f4f7fe;
}

.aside-container {
  background-color: #0d1b3e;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 10px rgba(0, 0, 0, 0.05);
}

.brand {
  height: 80px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 12px;
}

.logo-box {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #3a7bd5, #00d2ff);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 20px;
}

.brand-text {
  color: white;
  font-size: 20px;
  font-weight: 600;
}

.brand-text .sub {
  font-size: 12px;
  color: #6d7ea8;
  margin-left: 6px;
  font-weight: normal;
}

.menu-label {
  padding: 20px 24px 8px;
  font-size: 12px;
  color: #526484;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.custom-menu {
  border-right: none;
}

.custom-menu :deep(.el-menu-item).is-active {
  background: linear-gradient(90deg, #2a52be 0%, transparent 100%) !important;
  border-left: 4px solid #00d2ff;
  color: white !important;
}

.glass-header {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  border-bottom: 1px solid #eef2f7;
  z-index: 100;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: 0.3s;
}

.user-info:hover {
  background: #f0f4ff;
}

.username {
  font-weight: 500;
  font-size: 14px;
  color: #344054;
}

.status-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #10b981;
  background: #ecfdf5;
  padding: 4px 12px;
  border-radius: 12px;
}

.dot {
  width: 6px;
  height: 6px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.content-body {
  padding: 24px;
  overflow-y: auto;
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
  100% {
    opacity: 1;
  }
}

</style>