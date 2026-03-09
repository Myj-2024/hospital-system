<template>
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
          <!-- 显示角色名称 -->
          <span class="username">{{ roleName }}</span>
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
</template>

<script setup>
import {computed} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/modules/user'
import {ArrowRight, ArrowDown} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 根据角色显示名称
const roleName = computed(() => {
  const role = userStore.role || localStorage.getItem('userRole')
  const roleMap = {
    admin: '管理员',
    doctor: '医生',
    nurse: '护士'
  }
  return roleMap[role] || '未知用户'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.glass-header {
  height: 60px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  border-bottom: 1px solid #eef2f7;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
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