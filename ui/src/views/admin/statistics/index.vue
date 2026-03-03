<template>
  <div class="stats-container">
    <div class="welcome-bar">
      <h2>概览看板 <small>实时监控中心</small></h2>
      <el-date-picker v-model="currentDate" type="date" placeholder="选择日期" size="default" round/>
    </div>

    <el-row :gutter="20">
      <el-col :span="6" v-for="(item, index) in stats" :key="index">
        <div class="data-card" :class="item.type">
          <div class="card-icon">
            <el-icon>
              <component :is="item.icon"/>
            </el-icon>
          </div>
          <div class="card-main">
            <span class="label">{{ item.label }}</span>
            <h3 class="value">{{ item.value }}</h3>
            <div class="trend">
              <span :class="item.isUp ? 'up' : 'down'">
                {{ item.isUp ? '+' : '-' }}{{ item.percent }}%
              </span>
              较昨日
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 24px;">
      <el-col :span="16">
        <el-card class="chart-card" shadow="never" header="门诊就诊趋势 (24h)">
          <div class="mock-chart">
            <div class="wave-bg"></div>
            <p>ECharts 动态曲线图加载中...</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="list-card" shadow="never" header="诊室忙碌度">
          <div class="dept-stat" v-for="n in 5" :key="n">
            <div class="dept-name">
              <span>内科 {{ n }} 诊室</span>
              <span class="percent">{{ 40 + n * 10 }}%</span>
            </div>
            <el-progress :percentage="40 + n*10" :color="n > 3 ? '#f87171' : '#60a5fa'" :show-text="false"/>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {User, Timer, ShoppingCart, TrendCharts} from '@element-plus/icons-vue'

const currentDate = ref(new Date())

const stats = [
  {label: '今日挂号', value: '1,284', percent: '12', isUp: true, icon: 'User', type: 'blue'},
  {label: '待就诊', value: '42', percent: '5', isUp: false, icon: 'Timer', type: 'orange'},
  {label: '今日营收', value: '￥52,400', percent: '8', isUp: true, icon: 'ShoppingCart', type: 'green'},
  {label: '周转率', value: '94.2%', percent: '2', isUp: true, icon: 'TrendCharts', type: 'purple'}
]
</script>

<style scoped>
.stats-container {
  animation: slideUp 0.6s ease-out;
}

.welcome-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.welcome-bar h2 {
  font-size: 24px;
  color: #1e293b;
}

.welcome-bar small {
  font-size: 14px;
  color: #94a3b8;
  font-weight: normal;
  margin-left: 10px;
}

.data-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  display: flex;
  gap: 20px;
  align-items: center;
  border: 1px solid #eef2f7;
  transition: 0.3s;
}

.data-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.blue .card-icon {
  background: #e0e7ff;
  color: #4f46e5;
}

.orange .card-icon {
  background: #ffedd5;
  color: #f97316;
}

.green .card-icon {
  background: #dcfce7;
  color: #16a34a;
}

.purple .card-icon {
  background: #f3e8ff;
  color: #9333ea;
}

.card-main .label {
  font-size: 14px;
  color: #64748b;
}

.card-main .value {
  font-size: 26px;
  font-weight: bold;
  margin: 4px 0;
  color: #1e293b;
}

.trend {
  font-size: 12px;
  color: #94a3b8;
}

.up {
  color: #22c55e;
  font-weight: bold;
}

.down {
  color: #ef4444;
  font-weight: bold;
}

.chart-card, .list-card {
  border-radius: 20px;
  border: 1px solid #eef2f7;
}

.mock-chart {
  height: 300px;
  background: #f8fafc;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  position: relative;
  overflow: hidden;
}

.dept-stat {
  margin-bottom: 18px;
}

.dept-name {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 14px;
  color: #475569;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>