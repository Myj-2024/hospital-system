<template>
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
        <!-- 数据看板：所有登录角色都可见 -->
        <el-menu-item
            v-if="userRole === 'admin'"
            index="/admin/statistics"
        >
          <el-icon>
            <DataLine/>
          </el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item
            v-if="userRole === 'doctor'"
            index="/doctor/statistics"
        >
          <el-icon>
            <DataLine/>
          </el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item
            v-if="userRole === 'nurse'"
            index="/nurse/statistics"
        >
          <el-icon>
            <DataLine/>
          </el-icon>
          <span>数据看板</span>
        </el-menu-item>

        <div v-if="showCoreBusiness" class="menu-label">核心业务</div>

        <!-- 医生工作台：仅医生/管理员可见 -->
        <el-sub-menu v-if="userRole === 'doctor' || userRole === 'admin'" index="doctor">
          <template #title>
            <el-icon>
              <FirstAidKit/>
            </el-icon>
            <span>医生工作台</span>
          </template>
          <el-menu-item index="/doctor/appointment">接诊列表</el-menu-item>
          <el-menu-item index="/doctor/diagnosis">医生接诊</el-menu-item>
        </el-sub-menu>

        <!-- 护士叫号站：仅护士/管理员可见 -->
        <el-sub-menu v-if="userRole === 'nurse' || userRole === 'admin'" index="nurse">
          <template #title>
            <el-icon>
              <Bell/>
            </el-icon>
            <span>护士叫号站</span>
          </template>
          <el-menu-item index="/nurse/queue">叫号大屏</el-menu-item>
          <el-menu-item index="/nurse/prescription">处方管理</el-menu-item>
        </el-sub-menu>

        <!-- 系统配置：仅管理员可见 -->
        <div v-if="userRole === 'admin'" class="menu-label">系统配置</div>
        <el-sub-menu v-if="userRole === 'admin'" index="system">
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
</template>

<script setup>
import {computed} from 'vue'
import {useUserStore} from '@/store/modules/user'
import {DataLine, FirstAidKit, Bell, Setting} from '@element-plus/icons-vue'

// 获取当前用户角色
const userStore = useUserStore()
const userRole = computed(() => userStore.role || localStorage.getItem('userRole'))

// 控制核心业务标签是否显示
const showCoreBusiness = computed(() => {
  return userRole.value === 'doctor' || userRole.value === 'nurse' || userRole.value === 'admin'
})
</script>

<style scoped>
/* 样式不变，无需修改 */
.aside-container {
  background-color: #0d1b3e;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 10px rgba(0, 0, 0, 0.05);
  height: 100vh;
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
</style>