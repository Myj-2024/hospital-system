<template>
  <div class="diagnosis-container">
    <el-row :gutter="15">
      <el-col :span="6">
        <el-card class="list-card" shadow="never">
          <div class="list-header">
            <span class="title">候诊列表 (24)</span>
            <el-button link type="primary">刷新</el-button>
          </div>

          <div class="stat-grid">
            <div class="stat-item blue">
              <div class="label">今日挂号</div>
              <div class="value">42</div>
            </div>
            <div class="stat-item green">
              <div class="label">已接诊</div>
              <div class="value">18</div>
            </div>
          </div>

          <div class="patient-list">
            <div
                v-for="item in waitingList"
                :key="item.id"
                class="patient-item"
                :class="{ active: currentPatient.id === item.id }"
                @click="currentPatient = item"
            >
              <div class="item-main">
                <span class="no">{{ item.no }}</span>
                <span class="name">{{ item.name }}</span>
                <el-tag size="small" :type="item.type === '初诊' ? 'warning' : 'success'">{{ item.type }}</el-tag>
              </div>
              <div class="item-sub">
                <span>{{ item.gender }} {{ item.age }}岁</span>
                <span>{{ item.time }} 预约</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card class="patient-info-card" shadow="never">
          <div class="info-wrapper">
            <el-avatar :size="50" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
            <div class="base-info">
              <div class="name-row">
                <span class="name">{{ currentPatient.name }}</span>
                <el-tag round effect="plain">{{ currentPatient.gender }} {{ currentPatient.age }}岁</el-tag>
                <span class="id-card">身份证：3201***********1234</span>
              </div>
              <div class="medical-tags">
                <span class="tag-item">过敏史：<span class="danger">青霉素</span></span>
                <span class="tag-item">体重：72kg</span>
                <span class="tag-item">血压：128/85mmHg</span>
              </div>
            </div>
            <div class="action-btns">
              <el-button>查看历史</el-button>
              <el-button type="primary">结束接诊</el-button>
            </div>
          </div>
        </el-card>

        <el-tabs v-model="activeTab" class="diagnosis-tabs">
          <el-tab-pane label="电子病历" name="medical">
            <el-form label-position="top">
              <el-form-item label="主诉 (Chief Complaint)">
                <el-input type="textarea" :rows="3" placeholder="请输入患者主诉..."/>
              </el-form-item>
              <el-form-item label="现病史 (History of Present Illness)">
                <el-input type="textarea" :rows="4" placeholder="请输入现病史..."/>
              </el-form-item>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="体格检查">
                    <div class="check-tags">
                      <el-tag closable>心律齐</el-tag>
                      <el-tag closable>双肺呼吸音清</el-tag>
                      <el-button size="small" icon="Plus">添加</el-button>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="初步诊断">
                    <el-select v-model="diagnosis" placeholder="搜索ICD-10诊断编码" filterable style="width: 100%">
                      <el-option label="上呼吸道感染" value="J06.900"/>
                      <el-option label="急性支气管炎" value="J20.900"/>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="检查检验" name="exam">检查检验内容...</el-tab-pane>
          <el-tab-pane label="处方开立" name="prescription">处方开立内容...</el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref} from 'vue'

const activeTab = ref('medical')
const diagnosis = ref('')

const waitingList = ref([
  {id: 1, no: '019', name: '张伟', gender: '男', age: 45, type: '复诊', time: '10:30'},
  {id: 2, no: '020', name: '王芳', gender: '女', age: 32, type: '初诊', time: '10:45'},
  {id: 3, no: '021', name: '李强', gender: '男', age: 67, type: '复诊', time: '11:00'},
  {id: 4, no: '022', name: '赵敏', gender: '女', age: 28, type: '初诊', time: '11:15'}
])

const currentPatient = ref(waitingList.value[0])
</script>

<style scoped>
.diagnosis-container {
  padding: 10px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 100px);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.stat-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 20px;
}

.stat-item {
  padding: 15px;
  border-radius: 8px;
  text-align: center;
}

.stat-item.blue {
  background: #eef5fe;
  color: #409efc;
}

.stat-item.green {
  background: #f0f9eb;
  color: #67c23a;
}

.stat-item .value {
  font-size: 20px;
  font-weight: bold;
}

.patient-item {
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  margin-bottom: 10px;
  cursor: pointer;
  transition: 0.3s;
}

.patient-item.active {
  background: #409efc;
  color: #fff;
  border-color: #409efc;
}

.patient-item.active .item-sub {
  color: rgba(255, 255, 255, 0.8);
}

.item-main {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
  font-weight: bold;
}

.item-sub {
  font-size: 12px;
  color: #909399;
  display: flex;
  justify-content: space-between;
}

.patient-info-card {
  margin-bottom: 15px;
  border-radius: 8px;
}

.info-wrapper {
  display: flex;
  align-items: center;
  gap: 20px;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 8px;
}

.name {
  font-size: 18px;
  font-weight: bold;
}

.id-card {
  color: #909399;
  font-size: 13px;
}

.medical-tags {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #606266;
}

.danger {
  color: #f56c6c;
  font-weight: bold;
}

.diagnosis-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  min-height: 500px;
}

.check-tags {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}
</style>