<template>
  <div class="doctor-workbench">
    <header class="top-nav">
      <div class="nav-left">
        <div class="logo">
          <el-icon :size="20">
            <FirstAidKit/>
          </el-icon>
          <span>MediCo | 医生诊疗站</span>
        </div>
      </div>
      <div class="nav-menu">
        <div class="menu-item active">接诊中心</div>
        <div class="menu-item">病历库</div>
        <div class="menu-item">检查检验查询</div>
        <div class="menu-item">排班计划</div>
      </div>
      <div class="nav-right">

        <div class="user-info-wrapper">
          <el-tag type="success" effect="dark" round>正在接诊</el-tag>
          <el-dropdown trigger="click">
            <div class="user-info" style="cursor: pointer">
              <span class="dept-name">内科一诊室</span>
              <el-divider direction="vertical"/>
              <span class="doctor-name">{{ userInfo.realName }}</span>
              <el-avatar :size="28" :src="userInfo.avatar || 'https://randomuser.me/api/portraits/men/32.jpg'"
                         style="border: #ffffff 1px solid; margin: 0 10px"/>
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
          <div class="title-group">
            <h3>候诊列表</h3>
            <span class="count">今日 24</span>
          </div>
          <el-button link type="primary" icon="Refresh">刷新</el-button>
        </div>

        <div class="search-box">
          <el-input v-model="searchKey" placeholder="姓名/挂号号" prefix-icon="Search" clearable/>
        </div>

        <div class="queue-tabs">
          <div
              v-for="tab in tabs"
              :key="tab.value"
              class="tab-item"
              :class="{ active: activeTab === tab.value }"
              @click="activeTab = tab.value"
          >
            {{ tab.label }} ({{ tab.num }})
          </div>
        </div>

        <div class="patient-list">
          <div
              v-for="item in filteredAppointments"
              :key="item.appointment_no"
              class="patient-card"
              :class="{ 'is-diagnosing': item.status === 1 }"
              @click="handleSelectPatient(item)"
          >
            <div class="card-top">
              <span class="seq">{{ item.seq }}</span>
              <span class="name">{{ item.patient_name }}</span>
              <el-tag size="small" :type="item.type === '专家' ? 'warning' : 'info'">{{ item.type }}</el-tag>
            </div>
            <div class="card-mid">
              <span>{{ item.gender }} | {{ item.age }}岁</span>
              <span class="time">{{ item.appointment_time }}</span>
            </div>
            <div class="card-bottom">
              <span class="no">NO: {{ item.appointment_no }}</span>
              <span class="status-text" :class="'status-' + item.status">
                {{ getStatusText(item.status) }}
              </span>
            </div>
          </div>
        </div>

        <div class="call-action">
          <el-button type="primary" class="full-btn" icon="Microphone" @click="handleCallNext">
            呼叫下一位 (A08)
          </el-button>
        </div>
      </aside>

      <section class="center-panel">
        <div class="patient-summary" v-if="currentPatient">
          <div class="basic-info">
            <span class="p-name">{{ currentPatient.patient_name }}</span>
            <span class="p-tags">{{ currentPatient.gender }} / {{
                currentPatient.age
              }}岁 / ID: {{ currentPatient.patient_id_card }}</span>
          </div>
          <div class="vitals">
            <div class="v-item">血压: <span class="v-val">120/80</span></div>
            <div class="v-item">心率: <span class="v-val">78</span></div>
            <div class="v-item">体温: <span class="v-val">36.5℃</span></div>
          </div>
          <div class="history-tags">
            <el-tag size="small" type="danger" effect="plain">过敏：青霉素</el-tag>
            <el-tag size="small" type="warning" effect="plain">既往：高血压</el-tag>
          </div>
        </div>

        <div class="emr-container">
          <el-tabs v-model="emrTab" class="diagnosis-tabs">
            <el-tab-pane label="门诊病历书写" name="write">
              <div class="form-scroll">
                <el-form label-position="top">
                  <el-row :gutter="20">
                    <el-col :span="24">
                      <el-form-item label="主诉">
                        <el-input v-model="diagnosisForm.chief" type="textarea" :rows="2"
                                  placeholder="患者自述主要症状及持续时间..."/>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="现病史">
                        <el-input v-model="diagnosisForm.present" type="textarea" :rows="3"/>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="既往史">
                        <el-input v-model="diagnosisForm.past" type="textarea" :rows="3"/>
                      </el-form-item>
                    </el-col>
                    <el-col :span="24">
                      <el-form-item label="初步诊断">
                        <el-select v-model="diagnosisForm.icd" multiple filterable placeholder="选择 ICD-10 诊断码"
                                   style="width:100%">
                          <el-option label="[J06.900] 急性上呼吸道感染" value="J06.900"/>
                          <el-option label="[I10.x00] 高血压" value="I10.x00"/>
                          <el-option label="[E11.900] 2型糖尿病" value="E11.900"/>
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </el-tab-pane>
            <el-tab-pane label="处方/医嘱" name="prescription">
              <div class="prescription-box">
                <div class="p-toolbar">
                  <el-button type="primary" size="small" icon="Plus">新增药品</el-button>
                  <el-button size="small" icon="Memo">常用模板</el-button>
                </div>
                <el-table :data="prescriptionList" size="small" border stripe>
                  <el-table-column label="药品名称" prop="name"/>
                  <el-table-column label="规格" prop="spec" width="120"/>
                  <el-table-column label="用法用量" prop="usage"/>
                  <el-table-column label="数量" prop="count" width="80"/>
                  <el-table-column label="操作" width="80" fixed="right">
                    <template #default>
                      <el-button link type="danger">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>
          </el-tabs>

          <div class="bottom-toolbar">
            <div class="left-tools">
              <el-checkbox v-model="saveAsTemplate">存为常用模板</el-checkbox>
            </div>
            <div class="right-tools">
              <el-button type="info" plain>暂存</el-button>
              <el-button type="success" icon="Checked" @click="handleFinish">完成诊疗 & 打印</el-button>
            </div>
          </div>
        </div>
      </section>

      <aside class="right-panel">
        <div class="side-card stats-card">
          <div class="card-title">今日诊疗量</div>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="val">18</div>
              <div class="lab">已诊</div>
            </div>
            <div class="stat-item">
              <div class="val orange">6</div>
              <div class="lab">待诊</div>
            </div>
            <div class="stat-item">
              <div class="val blue">2</div>
              <div class="lab">预约</div>
            </div>
          </div>
        </div>

        <div class="side-card calendar-card">
          <div class="card-title">今日排班</div>
          <div class="schedule-info">
            <div class="sch-item">
              <el-icon>
                <Calendar/>
              </el-icon>
              <span>2026-04-05 (周日)</span>
            </div>
            <div class="sch-item">
              <el-icon>
                <Timer/>
              </el-icon>
              <span>全天班 (08:30 - 17:30)</span>
            </div>
          </div>
        </div>

        <div class="side-card notify-card">
          <div class="card-title">通知公告</div>
          <div class="msg-list">
            <div class="msg-item" v-for="i in 3" :key="i">
              <div class="msg-top">
                <span class="dot"></span>
                <span class="m-title">医院关于清明节排班通知</span>
              </div>
              <div class="m-time">2026-04-01</div>
            </div>
          </div>
        </div>

        <div class="side-card tool-card">
          <div class="card-title">常用快捷工具</div>
          <div class="tool-grid">
            <div class="t-btn">
              <el-icon>
                <FolderOpened/>
              </el-icon>
              <span>调阅影像</span></div>
            <div class="t-btn">
              <el-icon>
                <Files/>
              </el-icon>
              <span>检验报告</span></div>
            <div class="t-btn">
              <el-icon>
                <Collection/>
              </el-icon>
              <span>知识库</span></div>
            <div class="t-btn">
              <el-icon>
                <Service/>
              </el-icon>
              <span>会诊申请</span></div>
          </div>
        </div>
      </aside>

    </main>
  </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'
import userApi from '@/api/user'
import {uploadFile} from '@/utils/upload'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  User, SwitchButton, Plus, FirstAidKit, Calendar, Timer, FolderOpened, Files, Collection, Service
} from '@element-plus/icons-vue'

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


// 模拟数据：选项卡
const tabs = [
  {label: '待诊', value: 0, num: 6},
  {label: '诊中', value: 1, num: 1},
  {label: '已诊', value: 3, num: 18}
]
const activeTab = ref(0)
const searchKey = ref('')
const emrTab = ref('write')

// 模拟数据：预约列表 (对接 medical_appointment 表结构)
const appointments = ref([
  {
    seq: 'A07',
    patient_name: '张三',
    gender: '男',
    age: 45,
    appointment_time: '10:30',
    appointment_no: 'AP20260405001',
    type: '专家',
    status: 1,
    patient_id_card: '4201**********1234'
  },
  {
    seq: 'A08',
    patient_name: '李四',
    gender: '女',
    age: 32,
    appointment_time: '10:45',
    appointment_no: 'AP20260405002',
    type: '普通',
    status: 0,
    patient_id_card: '4201**********5678'
  },
  {
    seq: 'A09',
    patient_name: '王五',
    gender: '男',
    age: 28,
    appointment_time: '11:00',
    appointment_no: 'AP20260405003',
    type: '普通',
    status: 0,
    patient_id_card: '4201**********9012'
  },
  {
    seq: 'A10',
    patient_name: '赵六',
    gender: '女',
    age: 54,
    appointment_time: '11:15',
    appointment_no: 'AP20260405004',
    type: '专家',
    status: 0,
    patient_id_card: '4201**********3456'
  },
])

// 当前选中的患者
const currentPatient = ref(appointments.value[0])

// 诊疗表单
const diagnosisForm = reactive({
  chief: '咳嗽、发热3天，加重1天。',
  present: '患者于3天前受凉后出现咳嗽，咳黄痰，伴发热，体温最高38.5℃。',
  past: '否认肝炎、结核等传染病史。',
  icd: ['J06.900']
})

// 模拟处方数据
const prescriptionList = ref([
  {name: '阿莫西林胶囊', spec: '0.25g*20粒', usage: '口服，一日3次', count: 2},
  {name: '止咳糖浆', spec: '100ml/瓶', usage: '口服，一日3次', count: 1}
])

const saveAsTemplate = ref(false)

// 过滤列表
const filteredAppointments = computed(() => {
  return appointments.value.filter(item => {
    const matchTab = item.status === activeTab.value
    const matchKey = item.patient_name.includes(searchKey.value) || item.appointment_no.includes(searchKey.value)
    return matchTab && matchKey
  })
})

const getStatusText = (status) => {
  const map = {0: '待确认', 1: '接诊中', 2: '已取消', 3: '已完成'}
  return map[status]
}

const handleSelectPatient = (patient) => {
  currentPatient.value = patient
  ElMessage.info(`切换至患者：${patient.patient_name}`)
}

const handleCallNext = () => {
  ElMessage.success("正在呼叫下一位：A08 李四")
}

const handleFinish = () => {
  ElMessage.success("诊疗已完成，处方已发送至药房")
}
</script>

<style scoped>
.doctor-workbench {
  --primary-blue: #2d5cf6;
  --bg-main: #f4f7fc;
  --panel-white: #ffffff;
  --text-dark: #ffffff;
  --text-gray: #7f8c8d;
  --border-color: #e6ebf5;

  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-main);
  overflow: hidden;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 顶部导航 */
.top-nav {
  height: 60px;
  background: var(--primary-blue);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
}

.nav-menu {
  display: flex;
  gap: 5px;
}

.menu-item {
  padding: 6px 15px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  opacity: 0.8;
}

.menu-item.active {
  background: rgba(255, 255, 255, 0.2);
  opacity: 1;
}

.user-info-wrapper {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #FFFFFFFF;
}

/* 主体布局 */
.main-content {
  flex: 1;
  display: flex;
  padding: 15px;
  gap: 15px;
  overflow: hidden;
}

/* 左侧面板 */
.left-panel {
  width: 300px;
  background: var(--panel-white);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.panel-header {
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-group {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.title-group h3 {
  margin: 0;
  font-size: 16px;
}

.count {
  font-size: 12px;
  color: var(--text-gray);
}

.search-box {
  padding: 0 15px 10px;
}

.queue-tabs {
  display: flex;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 10px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  font-size: 13px;
  color: var(--text-gray);
  cursor: pointer;
  border-bottom: 2px solid transparent;
}

.tab-item.active {
  color: var(--primary-blue);
  border-bottom-color: var(--primary-blue);
  font-weight: bold;
}

.patient-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.patient-card {
  border: 1px solid var(--border-color);
  border-radius: 6px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.patient-card:hover {
  border-color: var(--primary-blue);
  background: #f0f7ff;
}

.patient-card.is-diagnosing {
  background: #f0f7ff;
  border-color: var(--primary-blue);
  box-shadow: 0 2px 8px rgba(45, 92, 246, 0.1);
}

.card-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.seq {
  font-weight: bold;
  color: var(--primary-blue);
  background: #ecf2ff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.name {
  font-weight: bold;
  font-size: 15px;
  flex: 1;
}

.card-mid {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-gray);
  margin-bottom: 5px;
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
}

.status-text {
  font-weight: 500;
}

.status-1 {
  color: #67c23a;
}

.status-0 {
  color: #e6a23c;
}

.call-action {
  padding: 15px;
  border-top: 1px solid var(--border-color);
}

.full-btn {
  width: 100%;
  height: 40px;
  border-radius: 6px;
}

/* 中间区域 */
.center-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
  min-width: 600px;
}

.patient-summary {
  background: var(--panel-white);
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.p-name {
  font-size: 20px;
  font-weight: bold;
  margin-right: 15px;
}

.p-tags {
  font-size: 14px;
  color: var(--text-gray);
}

.vitals {
  display: flex;
  gap: 20px;
  font-size: 13px;
}

.v-val {
  font-weight: bold;
  color: var(--primary-blue);
}

.emr-container {
  flex: 1;
  background: var(--panel-white);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.diagnosis-tabs {
  padding: 0 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.el-tabs__content) {
  flex: 1;
  overflow: hidden;
}

:deep(.el-tab-pane) {
  height: 100%;
  padding: 20px;
  overflow-y: auto;
}

.prescription-box {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.p-toolbar {
  display: flex;
  gap: 10px;
}

.bottom-toolbar {
  padding: 15px 20px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafafa;
}

/* 右侧面板 */
.right-panel {
  width: 260px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.side-card {
  background: var(--panel-white);
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid var(--primary-blue);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  text-align: center;
  gap: 5px;
}

.stat-item .val {
  font-size: 20px;
  font-weight: bold;
  color: #67c23a;
}

.stat-item .val.orange {
  color: #e6a23c;
}

.stat-item .val.blue {
  color: var(--primary-blue);
}

.stat-item .lab {
  font-size: 11px;
  color: var(--text-gray);
  margin-top: 4px;
}

.sch-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-dark);
  margin-bottom: 10px;
}

.sch-item .el-icon {
  color: var(--primary-blue);
}

.msg-item {
  padding: 8px 0;
  border-bottom: 1px solid #f5f7fa;
}

.msg-top {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.dot {
  width: 6px;
  height: 6px;
  background: #f56c6c;
  border-radius: 50%;
}

.m-title {
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.m-time {
  font-size: 11px;
  color: #9ca3af;
  padding-left: 12px;
}

.tool-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.t-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  padding: 10px;
  background: #f8faff;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.t-btn:hover {
  background: #eef4ff;
}

.t-btn .el-icon {
  font-size: 18px;
  color: var(--primary-blue);
}

.t-btn span {
  font-size: 12px;
  color: var(--text-dark);
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
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
}

.my-divider :deep(.el-divider__text) {
  font-size: 13px;
  color: #d54f4f;
}
</style>