<template>
  <div class="doctor-manage-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">医生账号管理</h2>
      </div>
      <div class="header-right">
        <el-button
            type="primary"
            :icon="Plus"
            class="add-btn"
            @click="openAddDialog"
            size="default"
        >
          新增医生
        </el-button>
        <!-- 批量删除按钮 -->
        <el-button
            type="danger"
            :icon="Delete"
            class="batch-delete-btn"
            @click="handleBatchDelete"
            size="default"
            :disabled="selectedRows.length === 0"
            style="margin-left: 10px;"
        >
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 搜索卡片 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form" size="default">
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="searchForm.username"
              placeholder="请输入用户名"
              clearable
              :prefix-icon="User"
              class="search-input"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
              v-model="searchForm.phone"
              placeholder="请输入手机号"
              clearable
              :prefix-icon="Phone"
              class="search-input"
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
              v-model="searchForm.realName"
              placeholder="请输入真实姓名"
              clearable
              :prefix-icon="UserFilled"
              class="search-input"
          />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input
              v-model="searchForm.title"
              placeholder="请输入职称（如主任医师）"
              clearable
              class="search-input"
          />
        </el-form-item>
        <el-form-item label="身份证号" prop="identityCard">
          <el-input
              v-model="searchForm.identityCard"
              placeholder="请输入身份证号"
              clearable
              :prefix-icon="Document"
              class="search-input"
          />
        </el-form-item>
        <!-- 新增科室名称筛选 -->
        <el-form-item label="科室名称" prop="deptName">
          <el-select
              v-model="searchForm.deptName"
              placeholder="请选择科室"
              clearable
              class="search-input"
              filterable
              remote
              :remote-method="getDeptList"
              :loading="deptLoading"
          >
            <el-option
                v-for="dept in deptList"
                :key="dept.id"
                :label="dept.deptName"
                :value="dept.deptName"
            />
          </el-select>
        </el-form-item>
        <!-- 新增状态筛选 -->
        <el-form-item label="账号状态" prop="status">
          <el-select
              v-model="searchForm.status"
              placeholder="全部状态"
              clearable
              class="search-input"
          >
            <el-option label="启用" value="1"/>
            <el-option label="禁用" value="0"/>
          </el-select>
        </el-form-item>
        <el-form-item class="search-actions">
          <el-button type="primary" @click="getDoctorList" :icon="Search">
            查询
          </el-button>
          <el-button @click="resetSearch" :icon="Refresh">
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
          :header-cell-style="{
            backgroundColor: '#e8f4f8',
            color: '#2d3748',
            fontWeight: 600,
            borderBottom: '2px solid #4299e1'
          }"
          :cell-style="{ verticalAlign: 'middle' }"
          size="default"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column prop="id" label="ID" width="80" align="center">
          <template #default="scope">
            <span class="id-cell">{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="scope">
            <div class="user-cell">
              <el-icon :size="18" color="#4299e1">
                <User/>
              </el-icon>
              <span class="username-text">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="真实姓名" min-width="120" align="center"/>
        <el-table-column prop="phone" label="手机号" min-width="130" align="center"/>
        <el-table-column prop="title" label="职称" min-width="120" align="center">
          <template #default="scope">
            <el-tag type="info" size="small">{{ scope.row.title || '未设置' }}</el-tag>
          </template>
        </el-table-column>
        <!-- 核心修改：替换科室ID列为科室名称列 -->
        <el-table-column prop="deptName" label="科室名称" min-width="120" align="center">
          <template #default="scope">
            <el-tag type="primary" size="small">{{ scope.row.deptName || '未分配' }}</el-tag>
          </template>
        </el-table-column>
        <!-- 核心修改：skill → specialty 字段对齐 -->
        <el-table-column prop="specialty" label="擅长领域" min-width="200" align="center">
          <template #default="scope">
            <span class="skill-text">{{ scope.row.specialty || '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="identityCard" label="身份证号" min-width="180" align="center">
          <template #default="scope">
            {{ formatIdCard(scope.row.identityCard) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.status === 1 ? 'success' : 'danger'"
                :effect="scope.row.status === 1 ? 'light' : 'dark'"
                size="default"
                class="status-tag"
            >
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" align="center">
          <template #default="scope">
            {{ dayjs(scope.row.createTime).format('YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <!-- 编辑按钮 -->
              <el-button
                  type="primary"
                  size="small"
                  :icon="Edit"
                  text
                  @click="openEditDialog(scope.row)"
                  class="action-btn edit-btn"
              >
                编辑
              </el-button>
              <!-- 状态修改按钮 -->
              <el-button
                  :type="scope.row.status === 1 ? 'warning' : 'success'"
                  size="small"
                  :icon="scope.row.status === 1 ? 'Minus' : 'Check'"
                  text
                  @click="handleUpdateStatus(scope.row)"
                  class="action-btn status-btn"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <!-- 删除按钮：添加disabled限制 -->
              <el-button
                  type="danger"
                  size="small"
                  :icon="Delete"
                  text
                  @click="handleSingleDelete(scope.row)"
                  class="action-btn delete-btn"
                  :disabled="scope.row.status === 1"
              >
                删除
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
            background
            size="default"
        />
      </div>
    </el-card>

    <!-- 新增医生弹窗 - 修复自动填充和背景色 -->
    <el-dialog
        v-model="addDialogVisible"
        title="新增医生账号"
        width="700px"
        destroy-on-close
        :close-on-click-modal="false"
        :modal-style="{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }"
        class="doctor-dialog"
    >
      <!-- 关键修复：添加autocomplete="off" 并设置背景色 -->
      <el-form
          ref="addFormRef"
          :model="addForm"
          :rules="addRules"
          label-width="100px"
          class="add-form"
          size="default"
          autocomplete="off"
          style="background-color: #ffffff;"
      >
        <!-- 关键修复：添加随机name属性 + autocomplete="new-password" 防止自动填充 -->
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="addForm.username"
              placeholder="请输入用户名（登录账号）"
              clearable
              class="form-input"
              :name="`username_${randomKey}`"
              autocomplete="off"
              autocorrect="off"
              autocapitalize="off"
              spellcheck="false"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              v-model="addForm.password"
              type="password"
              placeholder="请输入登录密码"
              show-password
              class="form-input"
              :name="`password_${randomKey}`"
              autocomplete="new-password"
              autocorrect="off"
              autocapitalize="off"
              spellcheck="false"
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
              v-model="addForm.realName"
              placeholder="请输入医生真实姓名"
              clearable
              class="form-input"
              :name="`realName_${randomKey}`"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
              v-model="addForm.phone"
              placeholder="请输入手机号"
              clearable
              class="form-input"
              :name="`phone_${randomKey}`"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="身份证号" prop="identityCard">
          <el-input
              v-model="addForm.identityCard"
              placeholder="请输入身份证号"
              clearable
              class="form-input"
              :name="`idCard_${randomKey}`"
              autocomplete="off"
          />
        </el-form-item>
        <!-- 核心修改：科室ID输入框 → 科室名称下拉选择框 -->
        <el-form-item label="科室名称" prop="deptName">
          <el-select
              v-model="addForm.deptName"
              placeholder="请选择科室"
              clearable
              class="form-input"
              :name="`deptName_${randomKey}`"
              filterable
              :loading="deptLoading"
          >
            <el-option
                v-for="dept in deptList"
                :key="dept.id"
                :label="dept.deptName"
                :value="dept.deptName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input
              v-model="addForm.title"
              placeholder="请输入职称（如主任医师）"
              clearable
              class="form-input"
              :name="`title_${randomKey}`"
              autocomplete="off"
          />
        </el-form-item>
        <!-- 核心修改：skill → specialty 字段对齐 -->
        <el-form-item label="擅长领域" prop="specialty">
          <el-input
              v-model="addForm.specialty"
              placeholder="请输入擅长领域（多个用逗号分隔）"
              clearable
              type="textarea"
              :rows="3"
              class="form-input"
              :name="`specialty_${randomKey}`"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="addForm.status">
            <el-radio label="1" border>启用</el-radio>
            <el-radio label="0" border style="margin-left: 10px;">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false" size="default">取消</el-button>
        <el-button
            type="primary"
            :loading="addLoading"
            @click="submitAddForm"
            size="default"
        >
          确认新增
        </el-button>
      </template>
    </el-dialog>

    <!-- 编辑医生弹窗 -->
    <el-dialog
        v-model="editDialogVisible"
        title="编辑医生账号"
        width="700px"
        destroy-on-close
        :close-on-click-modal="false"
        :modal-style="{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }"
        class="doctor-dialog"
    >
      <el-form
          ref="editFormRef"
          :model="editForm"
          :rules="editRules"
          label-width="100px"
          class="add-form"
          size="default"
          autocomplete="off"
          style="background-color: #ffffff;"
      >
        <el-form-item label="ID" prop="id">
          <el-input v-model="editForm.id" disabled class="form-input"/>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="editForm.username"
              placeholder="请输入用户名（登录账号）"
              clearable
              class="form-input"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
              v-model="editForm.realName"
              placeholder="请输入医生真实姓名"
              clearable
              class="form-input"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
              v-model="editForm.phone"
              placeholder="请输入手机号"
              clearable
              class="form-input"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="身份证号" prop="identityCard">
          <el-input
              v-model="editForm.identityCard"
              placeholder="请输入身份证号"
              clearable
              class="form-input"
              autocomplete="off"
          />
        </el-form-item>
        <!-- 核心修改：科室ID输入框 → 科室名称下拉选择框 -->
        <el-form-item label="科室名称" prop="deptName">
          <el-select
              v-model="editForm.deptName"
              placeholder="请选择科室"
              clearable
              class="form-input"
              filterable
              :loading="deptLoading"
          >
            <el-option
                v-for="dept in deptList"
                :key="dept.id"
                :label="dept.deptName"
                :value="dept.deptName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input
              v-model="editForm.title"
              placeholder="请输入职称（如主任医师）"
              clearable
              class="form-input"
              autocomplete="off"
          />
        </el-form-item>
        <!-- 核心修改：skill → specialty 字段对齐 -->
        <el-form-item label="擅长领域" prop="specialty">
          <el-input
              v-model="editForm.specialty"
              placeholder="请输入擅长领域（多个用逗号分隔）"
              clearable
              type="textarea"
              :rows="3"
              class="form-input"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio label="1" border>启用</el-radio>
            <el-radio label="0" border style="margin-left: 10px;">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false" size="default">取消</el-button>
        <el-button
            type="primary"
            :loading="editLoading"
            @click="submitEditForm"
            size="default"
        >
          确认修改
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
  Check,
  Delete
} from '@element-plus/icons-vue'
import {
  getDoctorPage,
  deleteDoctor,
  addDoctor,
  editDoctor,
  updateDoctorStatus,
  getDoctorById
} from '@/api/system/doctor'
// 核心修改：导入科室分页接口（替代原有的getDeptList）
import {getDepartmentPage} from '@/api/system/department'
import dayjs from 'dayjs'

// 关键修复：生成随机key防止浏览器自动填充
const randomKey = ref(Math.random().toString(36).substring(2, 10))

// 加载状态
const loading = ref(false)
// 科室列表加载状态
const deptLoading = ref(false)
// 医生列表数据
const doctorList = ref([])
// 科室列表数据（下拉选择用）
const deptList = ref([])
// 总条数
const total = ref(0)
// 选中的行（批量操作）
const selectedRows = ref([])
// 搜索表单（新增科室名称筛选）
const searchForm = ref({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: '',
  identityCard: '',
  realName: '',
  title: '',
  deptName: '', // 新增科室名称筛选
  status: ''
})

// 新增医生相关逻辑
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref(null)
const addForm = ref({
  username: '', // 确认：无默认值
  password: '', // 确认：无默认值
  realName: '',
  phone: '',
  identityCard: '',
  deptName: '', // 核心修改：替换departmentId为deptName
  title: '',
  specialty: '', // 核心修改：替换skill为specialty
  status: '1'
})

// 新增表单校验规则 - 核心修改：替换departmentId为deptName
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
  deptName: [ // 替换departmentId校验
    {required: true, message: '请选择科室名称', trigger: 'change'}
  ],
  title: [{required: true, message: '请输入职称', trigger: 'blur'}],
  specialty: [ // 替换skill校验
    {required: true, message: '请输入擅长领域', trigger: 'blur'}
  ],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
})

// 编辑相关逻辑
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  id: '',
  username: '',
  realName: '',
  phone: '',
  identityCard: '',
  deptName: '', // 核心修改：替换departmentId为deptName
  title: '',
  specialty: '', // 核心修改：替换skill为specialty
  status: '1'
})

// 编辑表单校验规则 - 核心修改：替换departmentId为deptName
const editRules = ref({
  id: [{required: true, message: '医生ID不能为空', trigger: 'blur'}],
  username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
  realName: [{required: true, message: '请输入真实姓名', trigger: 'blur'}],
  phone: [
    {required: true, message: '请输入手机号', trigger: 'blur'},
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur'}
  ],
  identityCard: [
    {required: true, message: '请输入身份证号', trigger: 'blur'},
    {pattern: /^\d{17}[\dXx]$/, message: '请输入正确的身份证号格式', trigger: 'blur'}
  ],
  deptName: [ // 替换departmentId校验
    {required: true, message: '请选择科室名称', trigger: 'change'}
  ],
  title: [{required: true, message: '请输入职称', trigger: 'blur'}],
  specialty: [ // 替换skill校验
    {required: true, message: '请输入擅长领域', trigger: 'blur'}
  ],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
})

// 核心修改：获取科室列表（调用分页接口，支持关键词搜索，实时同步科室管理数据）
const getDeptList = async (keyword = '') => {
  try {
    deptLoading.value = true
    // 调用科室分页接口，获取所有启用状态的科室（不分页）
    const res = await getDepartmentPage({
      pageNum: 1,
      pageSize: 1000, // 一次性获取足够多的科室数据
      deptName: keyword,
      status: 1 // 只查询启用状态的科室
    })
    deptList.value = res.data.records || []
  } catch (error) {
    deptList.value = []
    ElMessage.error('获取科室列表失败：' + error.message)
  } finally {
    deptLoading.value = false
  }
}

// 打开新增弹窗 - 关键修复：每次打开重新生成随机key + 强制清空表单
const openAddDialog = () => {
  // 重新生成随机key，彻底防止自动填充
  randomKey.value = Math.random().toString(36).substring(2, 10)

  // 强制清空表单，确保无默认值
  addForm.value = {
    username: '',
    password: '',
    realName: '',
    phone: '',
    identityCard: '',
    deptName: '', // 核心修改：替换departmentId
    title: '',
    specialty: '', // 核心修改：替换skill
    status: '1'
  }

  // 预加载科室列表
  getDeptList()

  // 延迟打开弹窗（确保DOM更新完成）
  setTimeout(() => {
    addDialogVisible.value = true
  }, 0)
}

// 打开编辑弹窗
const openEditDialog = async (row) => {
  try {
    // 预加载科室列表
    getDeptList()

    // 从后端获取完整的医生信息（联表数据）
    const detailRes = await getDoctorById(row.id)
    const doctorData = detailRes.data || row

    editForm.value = {
      id: doctorData.id,
      username: doctorData.username,
      realName: doctorData.realName,
      phone: doctorData.phone,
      identityCard: doctorData.identityCard,
      deptName: doctorData.deptName || '', // 核心修改：赋值科室名称
      title: doctorData.title || '',
      specialty: doctorData.specialty || '', // 核心修改：替换skill
      status: doctorData.status.toString()
    }
    editDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取医生信息失败：' + error.message)
  }
}

// 提交新增表单
const submitAddForm = async () => {
  if (!addFormRef.value) return

  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      const submitData = {
        ...addForm.value,
        status: Number(addForm.value.status),
        roleId: 2, // 强制设置为医生角色
        // 核心修改：传递deptName而非deptId，后端通过名称关联查询ID
        userId: null, // 由后端生成用户ID后关联（根据实际接口调整）
        dailyMaxNum: 20, // 默认每日最大接诊数（可根据需求改为可配置）
        workTime: '' // 出诊时间，可后续扩展
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

// 提交编辑表单
const submitEditForm = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      const submitData = {
        ...editForm.value,
        id: Number(editForm.value.id),
        status: Number(editForm.value.status),
        roleId: 2, // 强制设置为医生角色
        userId: Number(editForm.value.id), // 关联用户ID
        dailyMaxNum: 20, // 默认值（可扩展）
        workTime: '' // 出诊时间（可扩展）
      }

      editLoading.value = true
      try {
        await editDoctor(submitData)
        ElMessage.success('医生账号修改成功！')
        editDialogVisible.value = false
        getDoctorList()
      } catch (error) {
        ElMessage.error(error.message || '修改失败，请重试')
        console.error('修改医生失败详情：', error)
      } finally {
        editLoading.value = false
      }
    }
  })
}

// 获取医生列表（包含科室名称筛选）
const getDoctorList = async () => {
  try {
    loading.value = true
    // 处理筛选参数（空字符串不传）
    const params = {
      ...searchForm.value,
      ...(searchForm.value.status ? {status: Number(searchForm.value.status)} : {}),
      ...(searchForm.value.title ? {title: searchForm.value.title} : {}),
      ...(searchForm.value.deptName ? {deptName: searchForm.value.deptName} : {}) // 新增科室名称筛选
    }
    // 强制设置角色ID为2（医生）
    params.roleId = 2
    const res = await getDoctorPage(params)
    doctorList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    doctorList.value = []
    total.value = 0
    ElMessage.error('获取医生列表失败：' + error.message)
    console.error('查询医生列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索条件（包含科室名称筛选）
const resetSearch = () => {
  searchForm.value = {
    pageNum: 1,
    pageSize: 10,
    username: '',
    phone: '',
    identityCard: '',
    realName: '',
    title: '',
    deptName: '', // 新增科室名称重置
    status: ''
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

// 处理表格多选
const handleSelectionChange = (val) => {
  selectedRows.value = val
}

// 状态修改（适配后端仅更新status逻辑）
const handleUpdateStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'

  try {
    await ElMessageBox.confirm(
        `⚠️ 确认要${statusText}该医生账号吗？
      ✅ 账号所有数据将完整保留，仅状态变更
      ❌ 不会删除任何数据`,
        '状态修改确认（数据安全）',
        {
          confirmButtonText: '确认修改',
          cancelButtonText: '取消',
          type: newStatus === 1 ? 'success' : 'warning'
        }
    )

    // 仅传id和status，符合后端接口要求
    const statusData = {
      id: row.id,
      status: newStatus
    }

    await updateDoctorStatus(statusData)
    ElMessage.success(`医生账号${statusText}成功！数据已完整保留`)
    getDoctorList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`状态修改失败：${error.message || '请检查接口是否只修改状态'}`)
    }
  }
}

// 单个删除（适配后端物理删除逻辑）
const handleSingleDelete = async (row) => {
  if (row.status === 1) {
    ElMessage.warning('只能删除禁用状态的医生账号，无法删除启用状态的账号')
    return
  }

  try {
    await ElMessageBox.confirm(
        `【⚠️ 高危操作】确定要彻底删除该医生账号吗？
        1. 删除后将彻底清除该账号的所有数据（包括用户基础信息、医生专属信息）
        2. 操作不可逆，数据无法恢复，请务必谨慎！`,
        '彻底删除确认',
        {
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          type: 'danger'
        }
    )
    await deleteDoctor({id: row.id, idList: [row.id]})
    ElMessage.success('医生账号已彻底删除！所有相关数据已清除')
    getDoctorList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败：该医生账号不存在或已被删除')
    }
  }
}

// 批量删除（适配后端物理删除逻辑）
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的医生账号')
    return
  }

  const enableRows = selectedRows.value.filter(item => item.status === 1)
  if (enableRows.length > 0) {
    ElMessage.warning(`选中${selectedRows.value.length}个账号，其中${enableRows.length}个为启用状态，仅可删除禁用账号`)
    return
  }

  try {
    await ElMessageBox.confirm(
        `【⚠️ 高危操作】确定要彻底删除选中的${selectedRows.value.length}个医生账号吗？
        1. 删除后将彻底清除这些账号的所有数据（包括用户基础信息、医生专属信息）
        2. 操作不可逆，数据无法恢复，请务必谨慎！`,
        '批量彻底删除确认',
        {
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          type: 'danger'
        }
    )
    const idList = selectedRows.value.map(item => item.id)
    await deleteDoctor({idList})
    ElMessage.success(`成功彻底删除${selectedRows.value.length}个医生账号！所有相关数据已清除`)
    getDoctorList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败：部分账号可能不存在或已被删除')
    }
  }
}

// 页面初始化加载列表和科室列表
onMounted(() => {
  getDoctorList()
  getDeptList()
})
</script>

<style scoped>
/* 全局容器 */
.doctor-manage-container {
  background-color: #f8f9fa;
  min-height: calc(100vh - 64px);
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

/* 页面头部 - 核心调整：减少间距和内边距 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px; /* 从24px减少到12px */
  padding-bottom: 8px; /* 从16px减少到8px */
  border-bottom: 1px solid #e9ecef;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px; /* 从6px减少到4px */
}

.page-title {
  font-size: 20px; /* 从24px减少到20px */
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  letter-spacing: 0.5px;
}

.page-desc {
  font-size: 13px; /* 从14px减少到13px */
  color: #718096;
  line-height: 1.4; /* 略微调整行高 */
}

.add-btn, .batch-delete-btn {
  height: 36px; /* 从40px减少到36px */
  border-radius: 8px;
  font-weight: 500;
  padding: 0 16px; /* 从20px减少到16px */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.add-btn:hover, .batch-delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 卡片通用样式 - 核心调整：减少间距和内边距 */
.search-card, .table-card {
  border-radius: 12px;
  margin-bottom: 12px; /* 从24px减少到12px */
  overflow: hidden;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  background-color: #ffffff;
  transition: box-shadow 0.3s ease;
}

.search-card:hover, .table-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}


/* 搜索表单 - 核心调整：减少间距和内边距 */
.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px; /* 从20px减少到12px */
  padding: 12px 16px; /* 从20px减少到12px 16px */
}

.search-input {
  width: 180px; /* 从200px减少到180px */
  border-radius: 6px;
}

.search-actions {
  margin-left: auto;
  display: flex;
  gap: 8px; /* 从10px减少到8px */
}

/* 表格样式 */
.el-table {
  --el-table-row-hover-bg-color: #f5fafe;
  --el-table-header-text-color: #2d3748;
  --el-table-text-color: #4a5568;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username-text {
  font-weight: 500;
  color: #2d3748;
}

.id-cell {
  font-family: 'Consolas', 'Monaco', monospace;
  color: #4299e1;
  font-weight: 600;
}

.skill-text {
  color: #4a5568;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.table-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: nowrap;
}

.action-btn {
  border-radius: 6px;
  padding: 4px 0;
  transition: all 0.2s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
}

.edit-btn {
  color: #4299e1;
}

.status-btn {
  color: #ed8936;
}

.delete-btn {
  color: #e53e3e;
}

.status-tag {
  border-radius: 4px;
  padding: 2px 8px;
  font-weight: 500;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 0 20px 20px;
}

/* 表单样式 - 关键修复：确保背景色统一为白色 */
.add-form {
  padding: 20px;
  background-color: #ffffff !important;
  border-radius: 8px;
}

.form-input {
  border-radius: 6px;
  transition: border-color 0.2s ease;
}

.form-input:focus {
  border-color: #4299e1;
  box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.2);
}

/* 弹窗样式 - 关键修复：统一弹窗背景色 */
.doctor-dialog {
  --el-dialog-border-radius: 12px;
  --el-dialog-content-padding: 0;
  --el-dialog-bg-color: #ffffff;
}

.el-dialog__header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
  background-color: #ffffff;
}

.el-dialog__body {
  padding: 0 !important;
  background-color: #ffffff;
}

.el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #2d3748;
}

/* 响应式适配 */
@media (max-width: 1400px) {
  .search-form {
    gap: 12px 16px;
  }

  .skill-text {
    -webkit-line-clamp: 1;
  }
}

@media (max-width: 1200px) {
  .search-input {
    width: 160px;
  }

  .table-actions {
    gap: 4px;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .header-right {
    width: 100%;
    display: flex;
    justify-content: space-between;
  }

  .search-form {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-actions {
    margin-left: 0;
    width: 100%;
    justify-content: flex-end;
  }

  .search-input {
    width: 100%;
  }
}
</style>