<template>
  <div class="nurse-queue-container">
    <div class="queue-header">
      <div class="header-left">
        <el-icon size="24">
          <Monitor/>
        </el-icon>
        <span class="system-name">Medico | 护士站</span>
      </div>
      <div class="header-nav">
        <span class="active">叫号大屏</span>
        <span>患者列表</span>
        <span>诊室管理</span>
        <span>统计报表</span>
      </div>
      <div class="header-right">
        <el-tag type="success" effect="dark" round>运行中</el-tag>
        <div class="user-profile">
          <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
          <span>护士长</span>
        </div>
      </div>
    </div>

    <div class="queue-content">
      <div class="side-panel">
        <div class="panel-title">
          <span>候诊队列</span>
          <el-badge :value="32" type="primary"/>
        </div>
        <el-input placeholder="搜索患者..." prefix-icon="Search" class="search-input"/>
        <div class="filter-btns">
          <el-button size="small" type="primary">全部</el-button>
          <el-button size="small">普通</el-button>
          <el-button size="small">专家</el-button>
          <el-button size="small" type="danger" plain>急诊</el-button>
        </div>
        <div class="waiting-list">
          <div v-for="p in waitingList" :key="p.id" class="p-card" :class="{ active: p.status === '就诊中' }">
            <div class="p-header">
              <span class="p-no">{{ p.no }}</span>
              <span class="p-name">{{ p.name }} / {{ p.age }}岁</span>
              <el-tag size="small" :type="p.status === '就诊中' ? 'success' : 'info'">{{ p.status }}</el-tag>
            </div>
            <div class="p-body">
              <p>{{ p.dept }} - {{ p.doctor }}</p>
              <p class="time">
                <el-icon>
                  <Clock/>
                </el-icon>
                {{ p.time }} 报到
              </p>
            </div>
            <div class="p-actions" v-if="p.status !== '就诊中'">
              <el-button link type="primary">再次呼叫</el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="main-monitor">
        <div class="stats-row">
          <el-card shadow="never" class="stat-card">
            <template #header>在岗医生</template>
            <div class="stat-val">8 <span>/ 10</span></div>
          </el-card>
          <el-card shadow="never" class="stat-card">
            <template #header>已完成就诊</template>
            <div class="stat-val green">142 <span class="trend">+12%</span></div>
          </el-card>
          <el-card shadow="never" class="stat-card">
            <template #header>平均等待时间</template>
            <div class="stat-val orange">18 <span>分钟</span></div>
          </el-card>
          <div class="current-call-card">
            <div class="call-info">
              <p>当前呼叫</p>
              <h3>A01 张伟</h3>
              <p>内科一诊室</p>
            </div>
            <el-icon class="call-icon">
              <Microphone/>
            </el-icon>
          </div>
        </div>

        <div class="room-grid">
          <div v-for="room in rooms" :key="room.id" class="room-card">
            <div class="room-header">
              <span class="room-name">{{ room.name }}</span>
              <el-tag size="small" :type="room.isBusy ? 'success' : 'info'">{{
                  room.isBusy ? '就诊中' : '空闲'
                }}
              </el-tag>
            </div>
            <div class="room-body">
              <el-avatar :size="45" :src="room.docAvatar"/>
              <div class="doc-info">
                <p class="role">{{ room.role }}</p>
                <p class="doc-name">{{ room.doctor }}</p>
              </div>
            </div>
            <div class="room-footer">
              <span class="label">当前患者:</span>
              <span class="p-tag" v-if="room.currentP">{{ room.currentP }}</span>
              <span class="none" v-else>暂无</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'

const waitingList = ref([
  {id: 1, no: 'A01', name: '张伟', age: '32', status: '就诊中', dept: '内科一诊室', doctor: '李医生', time: '09:15'},
  {id: 2, no: 'A02', name: '王秀英', age: '65', status: '等待中', dept: '内科二诊室', doctor: '待分配', time: '09:30'},
  {id: 3, no: 'A03', name: '刘强', age: '45', status: '等待中', dept: '内科一诊室', doctor: '李医生', time: '09:45'}
])

const rooms = ref([
  {
    id: 1,
    name: '内科一诊室',
    isBusy: true,
    doctor: 'Dr. Michael Johnson',
    role: '主治医师',
    currentP: 'A01 张伟',
    docAvatar: 'https://i.pravatar.cc/150?u=1'
  },
  {
    id: 2,
    name: '内科二诊室',
    isBusy: true,
    doctor: 'Dr. Alisa Marlin',
    role: '主任医师',
    currentP: 'B05 李建国',
    docAvatar: 'https://i.pravatar.cc/150?u=2'
  },
  {
    id: 3,
    name: '内科三诊室',
    isBusy: false,
    doctor: 'Dr. Ekram Islam',
    role: '主治医师',
    currentP: '',
    docAvatar: 'https://i.pravatar.cc/150?u=3'
  },
  {
    id: 4,
    name: '内科四诊室',
    isBusy: false,
    doctor: 'Dr. Emily Davis',
    role: '副主任医师',
    currentP: '',
    docAvatar: 'https://i.pravatar.cc/150?u=4'
  }
])
</script>

<style scoped>
.nurse-queue-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
  overflow: hidden;
}

.queue-header {
  height: 64px;
  background: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 24px;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.system-name {
  font-size: 18px;
  font-weight: bold;
}

.header-nav {
  display: flex;
  gap: 30px;
}

.header-nav span {
  cursor: pointer;
  opacity: 0.8;
}

.header-nav span.active {
  opacity: 1;
  font-weight: bold;
  border-bottom: 2px solid white;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
}

.queue-content {
  flex: 1;
  display: flex;
  padding: 16px;
  gap: 16px;
  overflow: hidden;
}

.side-panel {
  width: 300px;
  background: white;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.panel-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  margin-bottom: 12px;
}

.filter-btns {
  margin: 12px 0;
  display: flex;
  gap: 4px;
}

.waiting-list {
  flex: 1;
  overflow-y: auto;
}

.p-card {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.p-card.active {
  border-color: #1890ff;
  background: #e6f7ff;
}

.p-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.p-no {
  font-weight: bold;
  color: #1890ff;
}

.p-body p {
  margin: 4px 0;
  font-size: 13px;
  color: #666;
}

.time {
  color: #999 !important;
  font-size: 12px !important;
}

.main-monitor {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr) 1.5fr;
  gap: 16px;
}

.stat-val {
  font-size: 28px;
  font-weight: bold;
  margin-top: 10px;
}

.stat-val span {
  font-size: 14px;
  color: #999;
}

.green {
  color: #52c41a;
}

.orange {
  color: #fa8c16;
}

.trend {
  font-size: 12px;
  margin-left: 8px;
}

.current-call-card {
  background: #1890ff;
  color: white;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.call-icon {
  font-size: 40px;
  opacity: 0.5;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.room-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  border-left: 4px solid #ccc;
}

.room-card:has(.el-tag--success) {
  border-left-color: #52c41a;
}

.room-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.room-name {
  font-weight: bold;
}

.room-body {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.doc-name {
  font-weight: bold;
  margin-top: 4px;
}

.role {
  font-size: 12px;
  color: #999;
}

.room-footer {
  border-top: 1px dashed #eee;
  padding-top: 12px;
  font-size: 13px;
}

.p-tag {
  color: #1890ff;
  font-weight: bold;
  margin-left: 8px;
}
</style>