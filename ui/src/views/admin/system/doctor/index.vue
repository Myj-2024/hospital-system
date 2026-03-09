<template>
  <div class="doctor-manage-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">医生账号管理</h2>
        <span class="page-desc">管理系统内所有医生账号信息，支持查询、新增、编辑与状态控制</span>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Plus" class="add-btn" @click="openAddDialog">
          新增医生
        </el-button>
      </div>
    </div>

    <!-- 搜索卡片 -->
    <el-card class="search-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Search/></el-icon>
            筛选条件
          </span>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input
              v-model="searchForm.username"
              placeholder="请输入用户名"
              clearable
              :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
              v-model="searchForm.phone"
              placeholder="请输入手机号"
              clearable
              :prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input
              v-model="searchForm.realName"
              placeholder="请输入真实姓名"
              clearable
              :prefix-icon="UserFilled"
          />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input
              v-model="searchForm.identityCard"
              placeholder="请输入身份证号"
              clearable
              :prefix-icon="Document"
          />
        </el-form-item>
        <el-form-item class="search-actions">
          <el-button type="primary" @click="getDoctorList">
            <el-icon>
              <Search/>
            </el-icon>
            查询
          </el-button>
          <el-button @click="resetSearch">
            <el-icon>
              <Refresh/>
            </el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格卡片 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><List/></el-icon>
            医生账号列表
          </span>
        </div>
      </template>

      <el-table
          v-loading="loading"
          :data="doctorList"
          border
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
          :header-cell-style="{ backgroundColor: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column prop="id" label="ID" width="100" align="center">
          <template #default="scope">
            <span class="id-cell">{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="scope">
            <div class="user-cell">
              <el-icon :size="16" color="#409eff">
                <User/>
              </el-icon>
              <span class="username-text">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="真实姓名" min-width="120"/>
        <el-table-column prop="phone" label="手机号" min-width="130"/>
        <el-table-column prop="identityCard" label="身份证号" min-width="180">
          <template #default="scope">
            {{ formatIdCard(scope.row.identityCard) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.status === 1 ? 'success' : 'danger'"
                :effect="scope.row.status === 1 ? 'light' : 'dark'"
                size="small"
            >
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="scope">
            {{ dayjs(scope.row.createTime).format('YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <div class="table-actions">
              <el-button
                  type="primary"
                  size="small"
                  :icon="Edit"
                  text
              >
                编辑
              </el-button>
              <el-button
                  :type="scope.row.status === 1 ? 'danger' : 'success'"
                  size="small"
                  :icon="scope.row.status === 1 ? 'Minus' : 'Check'"
                  text
                  @click="handleDelete(scope.row.id)"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
            v-model:current-page="searchForm.pageNum"
            v-model:page-size="searchForm.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增医生弹窗 -->
    <el-dialog
        v-model="addDialogVisible"
        title="新增医生账号"
        width="600px"
        destroy-on-close
        :close-on-click-modal="false"
    >
      <el-form
          ref="addFormRef"
          :model="addForm"
          :rules="addRules"
          label-width="100px"
          class="add-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="addForm.username"
              placeholder="请输入用户名（登录账号）"
              clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              v-model="addForm.password"
              type="password"
              placeholder="请输入登录密码"
              show-password
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
              v-model="addForm.realName"
              placeholder="请输入医生真实姓名"
              clearable
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
              v-model="addForm.phone"
              placeholder="请输入手机号"
              clearable
          />
        </el-form-item>
        <el-form-item label="身份证号" prop="identityCard">
          <el-input
              v-model="addForm.identityCard"
              placeholder="请输入身份证号"
              clearable
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="addForm.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button
            type="primary"
            :loading="addLoading"
            @click="submitAddForm"
        >
          确认新增
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  User,
  UserFilled,
  Phone,
  Document,
  Plus,
  Search,
  Refresh,
  List,
  Edit,
  Minus,
  Check
} from '@element-plus/icons-vue'
import {getDoctorPage, deleteDoctor, addDoctor} from '@/api/system/doctor'
import dayjs from 'dayjs'

// 加载状态
const loading = ref(false)
// 医生列表数据
const doctorList = ref([])
// 总条数
const total = ref(0)
// 搜索表单
const searchForm = ref({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: '',
  identityCard: '',
  realName: ''
})

// 新增医生相关逻辑
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref(null)
const addForm = ref({
  username: '',
  password: '',
  realName: '',
  phone: '',
  identityCard: '',
  status: '1'
})

// 新增表单校验规则
const addRules = ref({
  username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
  password: [{required: true, message: '请输入密码', trigger: 'blur'}],
  realName: [{required: true, message: '请输入真实姓名', trigger: 'blur'}],
  phone: [
    {required: true, message: '请输入手机号', trigger: 'blur'},
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur'}
  ],
  identityCard: [
    {required: true, message: '请输入身份证号', trigger: 'blur'},
    {pattern: /^\d{17}[\dXx]$/, message: '请输入正确的身份证号格式', trigger: 'blur'}
  ],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
})

// 打开新增弹窗
const openAddDialog = () => {
  addForm.value = {
    username: '',
    password: '',
    realName: '',
    phone: '',
    identityCard: '',
    status: '1'
  }
  addDialogVisible.value = true
}

// 提交新增表单（关键：添加数据类型转换，适配后端Integer类型）
const submitAddForm = async () => {
  if (!addFormRef.value) return

  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      // 调试：打印最终提交的数据
      console.log('【提交前】最终表单数据：', addForm.value)

      // 转换status为数字（后端通常是Integer类型）
      const submitData = {
        ...addForm.value,
        status: Number(addForm.value.status)
      }

      addLoading.value = true
      try {
        await addDoctor(submitData)
        ElMessage.success('医生账号新增成功！')
        addDialogVisible.value = false
        getDoctorList()
      } catch (error) {
        ElMessage.error(error.message || '新增失败，请重试')
        console.error('新增医生失败详情：', error)
      } finally {
        addLoading.value = false
      }
    }
  })
}

// 页面初始化加载列表
onMounted(() => {
  getDoctorList()
})

// 获取医生列表
const getDoctorList = async () => {
  try {
    loading.value = true
    const res = await getDoctorPage(searchForm.value)
    doctorList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    doctorList.value = []
    total.value = 0
    console.error('查询医生列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索条件
const resetSearch = () => {
  searchForm.value = {
    pageNum: 1,
    pageSize: 10,
    username: '',
    phone: '',
    identityCard: '',
    realName: ''
  }
  getDoctorList()
}

// 分页相关方法
const handleSizeChange = (val) => {
  searchForm.value.pageSize = val
  getDoctorList()
}
const handleCurrentChange = (val) => {
  searchForm.value.pageNum = val
  getDoctorList()
}

// 身份证号脱敏
const formatIdCard = (idCard) => {
  if (!idCard) return ''
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
}

// 批量选择
const handleSelectionChange = (val) => {
  console.log('选中的数据：', val)
}

// 删除/禁用医生
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm(
        '确定要执行此操作吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )
    const res = await deleteDoctor(id)
    ElMessage.success('操作成功')
    getDoctorList()
  } catch (error) {
    ElMessage.info('已取消操作')
  }
}
</script>

<style scoped>
/* 全局容器 */
.doctor-manage-container {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #1d2129;
  margin: 0;
}

.page-desc {
  font-size: 14px;
  color: #86909c;
}

.add-btn {
  height: 40px;
  border-radius: 8px;
  font-weight: 500;
}

/* 卡片通用样式 */
.search-card,
.table-card {
  border-radius: 12px;
  margin-bottom: 20px;
  overflow: hidden;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 搜索表单 */
.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px 24px;
}

.search-actions {
  margin-left: auto;
}

/* 表格样式 */
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username-text {
  font-weight: 500;
  color: #1d2129;
}

.id-cell {
  font-family: 'Consolas', 'Monaco', monospace;
  color: #606266;
}

.table-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 新增表单样式 */
.add-form {
  padding: 10px 0;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .search-form {
    gap: 12px 16px;
  }
}
</style>