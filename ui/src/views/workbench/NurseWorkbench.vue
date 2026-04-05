<template>
  <div class="nurse-workbench">
    <header class="top-nav">
      <div class="nav-left">
        <div class="logo">
          <el-icon :size="20">
            <FirstAidKit/>
          </el-icon>
          <span>MediCo | 护士站</span>
        </div>
      </div>
      <div class="nav-menu">
        <div class="menu-item active">叫号大屏</div>
        <div class="menu-item">患者列表</div>
        <div class="menu-item">诊室管理</div>
        <div class="menu-item">统计报表</div>
      </div>
      <div class="nav-right">
        <div class="status-badge">
          <span class="dot green-dot"></span> 运行中
        </div>
        <div>
          <el-dropdown trigger="click">
            <div class="user-info" style="cursor: pointer; display: flex; align-items: center; gap: 8px;">
              <el-avatar :size="28"
                         :src="userInfo.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" style="border: #ffffff 1px solid;"/>
              <span class="user-role">{{ userInfo.realName || '护士长' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="openProfile">
                  <el-icon>
                    <User/>
                  </el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon>
                    <SwitchButton/>
                  </el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <el-dialog v-model="profileVisible" title="个人资料修改" width="500px" destroy-on-close>
      <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px"
               style="padding: 10px 20px">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled/>
          <div style="font-size: 12px; color: #999">账号名不可修改</div>
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="profileForm.realName" placeholder="请输入姓名"/>
        </el-form-item>
        <el-divider content-position="center" class="my-divider">修改密码（不改请留空）</el-divider>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="profileForm.password" type="password" show-password placeholder="请输入新密码"/>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="profileForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码"/>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号"/>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload class="avatar-uploader" action="#" :show-file-list="false"
                     :http-request="handleProfileAvatarUpload">
            <img v-if="profileForm.avatar" :src="profileForm.avatar" class="profile-avatar"/>
            <el-icon v-else class="avatar-uploader-icon">
              <Plus/>
            </el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="profileVisible = false">取消</el-button>
        <el-button type="primary" :loading="profileLoading" @click="submitProfile">保存修改</el-button>
      </template>
    </el-dialog>

    <main class="main-content">

      <aside class="left-panel">
        <div class="panel-header">
          <h3>候诊队列</h3>
          <el-tag size="small" type="primary" effect="light" round>32人等待</el-tag>
        </div>

        <div class="search-bar">
          <el-input v-model="searchQuery" placeholder="搜索患者..." prefix-icon="Search"/>
        </div>

        <div class="filter-tabs">
          <div class="tab-item active">全部</div>
          <div class="tab-item">普通</div>
          <div class="tab-item">专家</div>
          <div class="tab-item emergency">急诊</div>
        </div>

        <div class="queue-list">
          <div
              v-for="patient in queueList"
              :key="patient.id"
              class="queue-card"
              :class="{ 'is-active': patient.status === '就诊中' }"
          >
            <div class="card-header">
              <div class="patient-info">
                <span class="queue-num" :class="getQueueNumClass(patient.status)">{{ patient.id }}</span>
                <span class="name">{{ patient.name }}</span>
                <span class="desc">{{ patient.gender }} / {{ patient.age }}岁</span>
              </div>
              <div class="status" :class="getStatusClass(patient.status)">
                <span class="dot"></span>{{ patient.status }}
              </div>
            </div>
            <div class="card-body">
              <p>{{ patient.department }} • {{ patient.doctor }}</p>
              <p class="time">
                <el-icon>
                  <Clock/>
                </el-icon>
                {{ patient.time }} 报到
              </p>
            </div>
            <div class="card-actions" v-if="patient.status === '就诊中'">
              <el-button size="small">查看详情</el-button>
              <el-button size="small" type="primary">再次呼叫</el-button>
            </div>
          </div>
        </div>

        <div class="bottom-action">
          <el-button type="info" color="#1e293b" class="full-btn" icon="Plus">现场挂号/取号</el-button>
        </div>
      </aside>

      <section class="center-panel">
        <div class="stats-row">
          <div class="stat-card">
            <div class="icon-wrapper blue">
              <el-icon>
                <Avatar/>
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="label">在岗医生</div>
              <div class="value">8<span class="sub">/10</span></div>
            </div>
          </div>
          <div class="stat-card">
            <div class="icon-wrapper green">
              <el-icon>
                <CircleCheckFilled/>
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="label">已完成就诊</div>
              <div class="value">142 <span class="trend up">+12%</span></div>
            </div>
          </div>
          <div class="stat-card">
            <div class="icon-wrapper orange">
              <el-icon>
                <Timer/>
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="label">平均等待时间</div>
              <div class="value">18 <span class="unit">分钟</span></div>
            </div>
          </div>
        </div>

        <div class="monitor-header">
          <h3>诊室状态监控</h3>
          <div class="legend">
            <span class="item"><span class="dot green-dot"></span>就诊中</span>
            <span class="item"><span class="dot gray-dot"></span>空闲</span>
            <span class="item"><span class="dot red-dot"></span>暂停</span>
          </div>
        </div>

        <div class="clinic-grid">
          <div v-for="clinic in clinicList" :key="clinic.name" class="clinic-card" :class="clinic.statusEn">
            <div class="clinic-header">
              <div class="c-title">
                <h4>{{ clinic.name }}</h4>
                <span class="c-type">{{ clinic.type }}</span>
              </div>
              <div class="c-status" :class="clinic.statusEn">{{ clinic.status }}</div>
            </div>

            <div class="doctor-info">
              <el-avatar :size="40" :src="clinic.docAvatar"/>
              <div class="d-text">
                <div class="d-title">{{ clinic.docTitle }}</div>
                <div class="d-name">{{ clinic.docName }}</div>
              </div>
            </div>

            <div class="clinic-footer">
              <template v-if="clinic.status === '就诊中'">
                <div class="current-patient">
                  <span class="label">当前患者</span>
                  <span class="p-name">{{ clinic.patientId }} {{ clinic.patientName }}</span>
                </div>
                <div class="time">{{ clinic.startTime }} 开始</div>
                <div class="more-btn">
                  <el-icon>
                    <MoreFilled/>
                  </el-icon>
                </div>
              </template>

              <template v-else-if="clinic.status === '空闲中'">
                <el-button class="call-next-btn" plain>+ 呼叫下一位患者</el-button>
              </template>

              <template v-else-if="clinic.status === '暂停'">
                <div class="paused-text">医生暂离，请等待</div>
              </template>
            </div>
          </div>
        </div>
      </section>

      <aside class="right-panel">
        <div class="blue-card current-call">
          <div class="bc-header">
            <span>当前呼叫</span>
            <el-icon>
              <Microphone/>
            </el-icon>
          </div>
          <div class="bc-body">
            <div class="big-num">A01 张伟</div>
            <div class="sub-text">内科一诊室</div>
          </div>
        </div>

        <div class="blue-card upcoming-call">
          <div class="bc-header">
            <span>即将呼叫</span>
          </div>
          <div class="upcoming-list">
            <div class="up-item" v-for="up in upcomingList" :key="up.id">
              <div class="up-info">
                <div class="up-id">{{ up.id }}</div>
                <div class="up-detail">
                  <div class="up-name">{{ up.name }}</div>
                  <div class="up-room">{{ up.room }}</div>
                </div>
              </div>
              <div class="call-icon">
                <el-icon>
                  <Microphone/>
                </el-icon>
              </div>
            </div>
          </div>
          <div class="view-all">查看全部队列 &rarr;</div>
        </div>

        <div class="notification-card">
          <div class="n-title">
            <el-icon>
              <Bell/>
            </el-icon>
            系统通知
          </div>
          <div class="n-list">
            <div class="n-item warning">
              <el-icon class="n-icon">
                <WarningFilled/>
              </el-icon>
              <div class="n-content">
                <div class="n-text">内科三诊室设备故障</div>
                <div class="n-desc">已通知IT部门，预计10分钟修复</div>
                <div class="n-time">5分钟前</div>
              </div>
            </div>
            <div class="n-item info">
              <el-icon class="n-icon">
                <InfoFilled/>
              </el-icon>
              <div class="n-content">
                <div class="n-text">下午专家门诊排班变更</div>
                <div class="n-desc">李主任临时会议，门诊推迟至14:30</div>
                <div class="n-time">30分钟前</div>
              </div>
            </div>
          </div>
        </div>

        <div class="quick-actions">
          <div class="action-btn">
            <el-icon>
              <Printer/>
            </el-icon>
            <span>打印小票</span>
          </div>
          <div class="action-btn">
            <el-icon>
              <VideoPause/>
            </el-icon>
            <span>暂停服务</span>
          </div>
        </div>
      </aside>

    </main>
  </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from 'vue'
import {
  FirstAidKit, Clock, Avatar, CircleCheckFilled, Timer,
  MoreFilled, Microphone, Bell, WarningFilled, InfoFilled, Printer, VideoPause, SwitchButton, User, Plus
} from '@element-plus/icons-vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'
import userApi from '@/api/user'
import {uploadFile} from '@/utils/upload'
import {ElMessage, ElMessageBox} from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

// --- 个人中心逻辑开始 ---
const profileVisible = ref(false)
const profileLoading = ref(false)
const profileFormRef = ref(null)
const profileForm = ref({
  id: null, username: '', realName: '', phone: '', avatar: '', password: '', confirmPassword: ''
})

const profileRules = {
  realName: [{required: true, message: '姓名不能为空', trigger: 'blur'}],
  phone: [{pattern: /^1[0-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur'}],
  password: [{min: 6, message: '密码长度至少为6位', trigger: 'blur'}],
  confirmPassword: [{
    validator: (rule, value, callback) => {
      if (profileForm.value.password && value !== profileForm.value.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }, trigger: 'blur'
  }]
}

const openProfile = () => {
  const {id, username, realName, phone, avatar} = userStore.userInfo
  profileForm.value = {id, username, realName, phone, avatar, password: '', confirmPassword: ''}
  profileVisible.value = true
}

const handleProfileAvatarUpload = async (options) => {
  const url = await uploadFile(options.file)
  profileForm.value.avatar = url
}

const submitProfile = async () => {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      profileLoading.value = true
      try {
        const updateData = {...profileForm.value}
        if (!updateData.password) {
          delete updateData.password
          delete updateData.confirmPassword
        }
        await userApi.updateProfile(updateData)
        ElMessage.success('资料更新成功')
        userStore.setUserInfo({
          ...userStore.userInfo,
          realName: updateData.realName,
          phone: updateData.phone,
          avatar: updateData.avatar
        })
        profileVisible.value = false
      } catch (error) {
        console.error('更新失败', error)
      } finally {
        profileLoading.value = false
      }
    }
  })
}

const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {type: 'warning'}).then(() => {
    userStore.logout()
    router.push('/login')
  })
}


// 模拟数据：搜索框
const searchQuery = ref('')

// 模拟数据：候诊队列
const queueList = ref([
  {
    id: 'A01',
    name: '张伟',
    gender: '男',
    age: 32,
    department: '内科一诊室',
    doctor: '李医生',
    time: '09:15',
    status: '就诊中'
  },
  {
    id: 'A02',
    name: '王秀英',
    gender: '女',
    age: 65,
    department: '内科二诊室',
    doctor: '待分配',
    time: '09:20',
    status: '等待中'
  },
  {
    id: 'A03',
    name: '刘强',
    gender: '男',
    age: 45,
    department: '内科一诊室',
    doctor: '李医生',
    time: '09:25',
    status: '等待中'
  },
  {
    id: 'A04',
    name: '陈静',
    gender: '女',
    age: 28,
    department: '过号重新报到',
    doctor: '',
    time: '09:30',
    status: '过号'
  },
])

// 模拟数据：诊室状态
const clinicList = ref([
  {
    name: '内科一诊室', type: '普通门诊', status: '就诊中', statusEn: 'active',
    docAvatar: 'https://randomuser.me/api/portraits/men/32.jpg', docTitle: '主治医师', docName: 'Dr. Michael Johnson',
    patientId: 'A01', patientName: '张伟', startTime: '09:12'
  },
  {
    name: '内科二诊室', type: '专家门诊', status: '就诊中', statusEn: 'active',
    docAvatar: 'https://randomuser.me/api/portraits/women/44.jpg', docTitle: '主任医师', docName: 'Dr. Alisa Marlin',
    patientId: 'B05', patientName: '李建国', startTime: '09:05'
  },
  {
    name: '内科三诊室', type: '普通门诊', status: '空闲中', statusEn: 'idle',
    docAvatar: 'https://randomuser.me/api/portraits/men/62.jpg', docTitle: '主治医师', docName: 'Dr. Ekram Islam'
  },
  {
    name: '内科四诊室', type: '专家门诊', status: '暂停', statusEn: 'paused',
    docAvatar: 'https://randomuser.me/api/portraits/women/68.jpg', docTitle: '副主任医师', docName: 'Dr. Emily Davis'
  }
])

// 模拟数据：即将呼叫
const upcomingList = ref([
  {id: 'A02', name: '王秀英', room: '内科二诊室'},
  {id: 'A03', name: '刘强', room: '内科一诊室'},
  {id: 'A04', name: '陈静', room: '过号重排'},
])

// 辅助函数：状态样式
const getStatusClass = (status) => {
  if (status === '就诊中') return 'c-green'
  if (status === '等待中') return 'c-yellow'
  return 'c-gray'
}

const getQueueNumClass = (status) => {
  if (status === '就诊中') return 'bg-blue'
  return 'bg-gray'
}
</script>

<style scoped>
/* 基础变量与重置 */
.nurse-workbench {
  --primary-color: #2b85e4;
  --bg-color: #f0f2f5;
  --panel-bg: #ffffff;
  --text-main: #1f2937;
  --text-sub: #6b7280;
  --border-color: #e5e7eb;
  --success-color: #10b981;
  --warning-color: #f59e0b;
  --danger-color: #ef4444;

  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-color);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  overflow: hidden;
}

/* 小圆点 */
.dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}

.green-dot {
  background-color: var(--success-color);
}

.gray-dot {
  background-color: #9ca3af;
}

.red-dot {
  background-color: var(--danger-color);
}

/* ================= 顶部导航 ================= */
.top-nav {
  height: 60px;
  background-color: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.nav-left .logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}

.nav-menu {
  display: flex;
  gap: 10px;
}

.menu-item {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.menu-item.active {
  background-color: rgba(255, 255, 255, 0.2);
  font-weight: 500;
}

.menu-item:hover:not(.active) {
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-badge {
  background: rgba(0, 0, 0, 0.15);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #FFFFFFFF;
}

.user-role {
  font-size: 14px;
}

/* ================= 主体网格布局 ================= */
.main-content {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
  overflow: hidden;
}

/* ================= 左侧：候诊队列 ================= */
.left-panel {
  width: 320px;
  background: var(--panel-bg);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.panel-header {
  padding: 20px 20px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--text-main);
}

.search-bar {
  padding: 0 20px 15px;
}

.filter-tabs {
  display: flex;
  padding: 0 20px 15px;
  gap: 10px;
}

.tab-item {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  background: #f3f4f6;
  color: var(--text-sub);
  cursor: pointer;
}

.tab-item.active {
  background: var(--primary-color);
  color: white;
}

.tab-item.emergency {
  color: var(--danger-color);
  background: #fef2f2;
}

.queue-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.queue-card {
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 12px;
  transition: all 0.3s;
}

.queue-card.is-active {
  border-color: #bfdbfe;
  background: #eff6ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.patient-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.queue-num {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}

.queue-num.bg-blue {
  background: #dbeafe;
  color: var(--primary-color);
}

.queue-num.bg-gray {
  background: #f3f4f6;
  color: var(--text-sub);
}

.name {
  font-weight: 600;
  color: var(--text-main);
}

.desc {
  font-size: 12px;
  color: var(--text-sub);
}

.status {
  font-size: 12px;
  font-weight: 500;
}

.c-green {
  color: var(--success-color);
}

.c-yellow {
  color: var(--warning-color);
}

.c-gray {
  color: #9ca3af;
}

.c-green .dot {
  background: var(--success-color);
}

.c-yellow .dot {
  background: var(--warning-color);
}

.card-body p {
  margin: 0 0 6px 0;
  font-size: 13px;
  color: var(--text-main);
}

.card-body .time {
  color: var(--text-sub);
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.bottom-action {
  padding: 15px 20px;
  border-top: 1px solid var(--border-color);
}

.full-btn {
  width: 100%;
  border-radius: 8px;
  height: 40px;
}

/* ================= 中间：数据与监控 ================= */
.center-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
}

.stats-row {
  display: flex;
  gap: 20px;
}

.stat-card {
  flex: 1;
  background: var(--panel-bg);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.icon-wrapper.blue {
  background: #eff6ff;
  color: var(--primary-color);
}

.icon-wrapper.green {
  background: #ecfdf5;
  color: var(--success-color);
}

.icon-wrapper.orange {
  background: #fffbeb;
  color: var(--warning-color);
}

.stat-info .label {
  font-size: 13px;
  color: var(--text-sub);
  margin-bottom: 4px;
}

.stat-info .value {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-main);
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.stat-info .value .sub, .stat-info .value .unit {
  font-size: 14px;
  font-weight: normal;
  color: var(--text-sub);
}

.stat-info .value .trend {
  font-size: 13px;
  font-weight: 500;
  color: var(--success-color);
  background: #ecfdf5;
  padding: 2px 6px;
  border-radius: 10px;
  margin-left: 8px;
}

.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.monitor-header h3 {
  margin: 0;
  font-size: 18px;
  color: var(--text-main);
}

.legend {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: var(--text-sub);
}

.clinic-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.clinic-card {
  background: var(--panel-bg);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border-left: 4px solid transparent;
  display: flex;
  flex-direction: column;
}

.clinic-card.active {
  border-left-color: var(--success-color);
}

.clinic-card.idle {
  border-left-color: #d1d5db;
}

.clinic-card.paused {
  border-left-color: var(--danger-color);
}

.clinic-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.c-title h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: var(--text-main);
}

.c-type {
  font-size: 12px;
  color: var(--text-sub);
}

.c-status {
  font-size: 13px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 12px;
}

.c-status.active {
  background: #ecfdf5;
  color: var(--success-color);
}

.c-status.idle {
  background: #f3f4f6;
  color: var(--text-sub);
}

.c-status.paused {
  background: #fef2f2;
  color: var(--danger-color);
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.d-title {
  font-size: 12px;
  color: var(--text-sub);
}

.d-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-main);
}

.clinic-footer {
  margin-top: auto;
  padding-top: 15px;
  border-top: 1px dashed var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-patient {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.current-patient .label {
  font-size: 12px;
  color: var(--text-sub);
}

.current-patient .p-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-color);
}

.clinic-footer .time {
  font-size: 12px;
  color: var(--text-sub);
}

.more-btn {
  color: #9ca3af;
  cursor: pointer;
}

.call-next-btn {
  width: 100%;
  border-style: dashed;
}

.paused-text {
  width: 100%;
  text-align: center;
  font-size: 13px;
  color: var(--text-sub);
}

/* ================= 右侧：侧边栏面板 ================= */
.right-panel {
  width: 280px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.blue-card {
  background: var(--primary-color);
  border-radius: 12px;
  color: white;
  padding: 20px;
  box-shadow: 0 4px 10px rgba(43, 133, 228, 0.2);
}

.bc-header {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 15px;
}

.current-call .big-num {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 4px;
}

.current-call .sub-text {
  font-size: 14px;
  opacity: 0.8;
}

.upcoming-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 15px;
}

.up-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.1);
  padding: 10px 12px;
  border-radius: 8px;
}

.up-info {
  display: flex;
  gap: 10px;
  align-items: center;
}

.up-id {
  font-size: 14px;
  font-weight: 600;
}

.up-name {
  font-size: 14px;
  margin-bottom: 2px;
}

.up-room {
  font-size: 11px;
  opacity: 0.7;
}

.call-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: white;
  color: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.view-all {
  text-align: center;
  font-size: 13px;
  cursor: pointer;
  opacity: 0.9;
}

.notification-card {
  background: var(--panel-bg);
  border-radius: 12px;
  padding: 20px;
  flex: 1;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.n-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 15px;
}

.n-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.n-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.n-icon {
  font-size: 16px;
  margin-top: 2px;
}

.n-item.warning .n-icon {
  color: var(--warning-color);
}

.n-item.info .n-icon {
  color: var(--primary-color);
}

.n-text {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-main);
  margin-bottom: 4px;
}

.n-desc {
  font-size: 12px;
  color: var(--text-sub);
  line-height: 1.4;
  margin-bottom: 4px;
}

.n-time {
  font-size: 11px;
  color: #9ca3af;
}

.quick-actions {
  display: flex;
  gap: 15px;
}

.action-btn {
  flex: 1;
  background: var(--panel-bg);
  border-radius: 10px;
  padding: 15px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-main);
  font-size: 13px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: background 0.3s;
}

.action-btn:hover {
  background: #f9fafb;
}

.action-btn .el-icon {
  font-size: 20px;
  color: var(--text-sub);
}

.avatar-uploader .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
}

.profile-avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
  border-radius: 6px;
  border: #c8c8c8 1px solid;
}

.my-divider :deep(.el-divider__text) {
  font-size: 13px;
  color: #d54f4f;
}
</style>