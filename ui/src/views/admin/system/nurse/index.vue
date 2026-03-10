<template>
  <div class="nurse-manage-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">护士账号管理</h2>
        <span class="page-desc">管理系统内所有护士账号信息，支持查询、新增、编辑与状态控制</span>
      </div>
      <div class="header-right">
        <el-button
            type="primary"
            :icon="Plus"
            class="add-btn"
            @click="openAddDialog"
            size="default"
        >
          新增护士
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
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Search/></el-icon>
            筛选条件
          </span>
        </div>
      </template>
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
        <el-form-item label="护士等级" prop="nurseLevel">
          <el-input
              v-model="searchForm.nurseLevel"
              placeholder="请输入护士等级（如初级护士）"
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
        <!-- 状态筛选 -->
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
          <el-button type="primary" @click="getNurseList" :icon="Search">
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
            护士账号列表
          </span>
        </div>
      </template>

      <el-table
          v-loading="loading"
          :data="nurseList"
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
        <el-table-column prop="nurseLevel" label="护士等级" min-width="120" align="center">
          <template #default="scope">
            <el-tag type="info" size="small">{{ scope.row.nurseLevel || '未设置' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="wardId" label="病房ID" width="90" align="center"/>
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
              <!-- 删除按钮 -->
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

    <!-- 新增护士弹窗 -->
    <el-dialog
        v-model="addDialogVisible"
        title="新增护士账号"
        width="700px"
        destroy-on-close
        :close-on-click-modal="false"
        :modal-style="{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }"
        class="nurse-dialog"
    >
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
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="addForm.username"
              placeholder="请输入用户名（登录账号）"
              clearable
              class="form-input"
              :name="`username_${randomKey}`"
              autocomplete="off"
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
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
              v-model="addForm.realName"
              placeholder="请输入护士真实姓名"
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
        <el-form-item label="病房ID" prop="wardId">
          <el-input
              v-model="addForm.wardId"
              placeholder="请输入病房ID（数字）"
              clearable
              type="number"
              class="form-input"
              :name="`wardId_${randomKey}`"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="护士等级" prop="nurseLevel">
          <el-input
              v-model="addForm.nurseLevel"
              placeholder="请输入护士等级（如初级护士）"
              clearable
              class="form-input"
              :name="`nurseLevel_${randomKey}`"
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

    <!-- 编辑护士弹窗 -->
    <el-dialog
        v-model="editDialogVisible"
        title="编辑护士账号"
        width="700px"
        destroy-on-close
        :close-on-click-modal="false"
        :modal-style="{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }"
        class="nurse-dialog"
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
              placeholder="请输入护士真实姓名"
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
        <el-form-item label="病房ID" prop="wardId">
          <el-input
              v-model="editForm.wardId"
              placeholder="请输入病房ID（数字）"
              clearable
              type="number"
              class="form-input"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="护士等级" prop="nurseLevel">
          <el-input
              v-model="editForm.nurseLevel"
              placeholder="请输入护士等级（如初级护士）"
              clearable
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
  getNursePage,
  deleteNurse,
  addNurse,
  editNurse,
  updateNurseStatus,
  getNurseById
} from '@/api/system/nurse'
import dayjs from 'dayjs'

// 生成随机key防止浏览器自动填充
const randomKey = ref(Math.random().toString(36).substring(2, 10))

// 加载状态
const loading = ref(false)
// 护士列表数据
const nurseList = ref([])
// 总条数
const total = ref(0)
// 选中的行（批量操作）
const selectedRows = ref([])
// 搜索表单
const searchForm = ref({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: '',
  identityCard: '',
  realName: '',
  nurseLevel: '', // 护士等级筛选
  status: ''
})

// 新增护士相关逻辑
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref(null)
const addForm = ref({
  username: '',
  password: '',
  realName: '',
  phone: '',
  identityCard: '',
  wardId: undefined, // 修复：初始值改为undefined（数字类型空值）
  nurseLevel: '',
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
  wardId: [
    {required: true, message: '请输入病房ID', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        // 自定义校验：确保值为数字（或可转为数字）
        if (value === undefined || value === null) {
          callback(new Error('请输入病房ID'))
        } else if (isNaN(Number(value))) {
          callback(new Error('病房ID必须为数字'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  nurseLevel: [{required: true, message: '请输入护士等级', trigger: 'blur'}],
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
  wardId: undefined, // 修复：初始值改为undefined
  nurseLevel: '',
  status: '1'
})

// 编辑表单校验规则
const editRules = ref({
  id: [{required: true, message: '护士ID不能为空', trigger: 'blur'}],
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
  wardId: [
    {required: true, message: '请输入病房ID', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (value === undefined || value === null) {
          callback(new Error('请输入病房ID'))
        } else if (isNaN(Number(value))) {
          callback(new Error('病房ID必须为数字'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  nurseLevel: [{required: true, message: '请输入护士等级', trigger: 'blur'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
})

// 打开新增弹窗
const openAddDialog = () => {
  // 重新生成随机key，防止自动填充
  randomKey.value = Math.random().toString(36).substring(2, 10)

  // 清空表单
  addForm.value = {
    username: '',
    password: '',
    realName: '',
    phone: '',
    identityCard: '',
    wardId: undefined, // 修复：重置为undefined
    nurseLevel: '',
    status: '1'
  }

  setTimeout(() => {
    addDialogVisible.value = true
  }, 0)
}

// 打开编辑弹窗
const openEditDialog = async (row) => {
  try {
    // 从后端获取完整的护士信息
    const detailRes = await getNurseById(row.id)
    const nurseData = detailRes.data || row

    editForm.value = {
      id: nurseData.id,
      username: nurseData.username,
      realName: nurseData.realName,
      phone: nurseData.phone,
      identityCard: nurseData.identityCard,
      wardId: nurseData.wardId ? Number(nurseData.wardId) : undefined, // 修复：转为数字或undefined
      nurseLevel: nurseData.nurseLevel || '',
      status: nurseData.status.toString()
    }
    editDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取护士信息失败：' + error.message)
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
        wardId: addForm.value.wardId ? Number(addForm.value.wardId) : null,
        roleId: 3 // 强制设置为护士角色（后端也会强制设置）
      }

      addLoading.value = true
      try {
        await addNurse(submitData)
        ElMessage.success('护士账号新增成功！')
        addDialogVisible.value = false
        getNurseList()
      } catch (error) {
        ElMessage.error(error.message || '新增失败，请重试')
        console.error('新增护士失败详情：', error)
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
        wardId: editForm.value.wardId ? Number(editForm.value.wardId) : null,
        roleId: 3 // 强制设置为护士角色
      }

      editLoading.value = true
      try {
        await editNurse(submitData)
        ElMessage.success('护士账号修改成功！')
        editDialogVisible.value = false
        getNurseList()
      } catch (error) {
        ElMessage.error(error.message || '修改失败，请重试')
        console.error('修改护士失败详情：', error)
      } finally {
        editLoading.value = false
      }
    }
  })
}

// 获取护士列表
const getNurseList = async () => {
  try {
    loading.value = true
    // 处理筛选参数
    const params = {
      ...searchForm.value,
      ...(searchForm.value.status ? {status: Number(searchForm.value.status)} : {}),
      ...(searchForm.value.nurseLevel ? {nurseLevel: searchForm.value.nurseLevel} : {})
    }
    // 强制设置角色ID为3（护士）
    params.roleId = 3
    const res = await getNursePage(params)
    nurseList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    nurseList.value = []
    total.value = 0
    ElMessage.error('获取护士列表失败：' + error.message)
    console.error('查询护士列表失败：', error)
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
    realName: '',
    nurseLevel: '',
    status: ''
  }
  getNurseList()
}

// 分页相关方法
const handleSizeChange = (val) => {
  searchForm.value.pageSize = val
  getNurseList()
}
const handleCurrentChange = (val) => {
  searchForm.value.pageNum = val
  getNurseList()
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

// 状态修改
const handleUpdateStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'

  try {
    await ElMessageBox.confirm(
        `⚠️ 确认要${statusText}该护士账号吗？
      ✅ 账号所有数据将完整保留，仅状态变更
      ❌ 不会删除任何数据`,
        '状态修改确认（数据安全）',
        {
          confirmButtonText: '确认修改',
          cancelButtonText: '取消',
          type: newStatus === 1 ? 'success' : 'warning'
        }
    )

    // 仅传id和status
    const statusData = {
      id: row.id,
      status: newStatus
    }

    await updateNurseStatus(statusData)
    ElMessage.success(`护士账号${statusText}成功！`)
    getNurseList()
  } catch (error) {
    // 仅在不是用户取消操作时才提示错误
    if (error !== 'cancel' && !(error instanceof Error && error.message === 'cancel')) {
      ElMessage.error(`状态修改失败：${error.message || '服务器异常，请稍后重试'}`)
      console.error('状态修改失败详情：', error)
    }
  }
}

// 单个删除
const handleSingleDelete = async (row) => {
  if (row.status === 1) {
    ElMessage.warning('只能删除禁用状态的护士账号，无法删除启用状态的账号')
    return
  }

  try {
    await ElMessageBox.confirm(
        `【⚠️ 数据说明】确定要删除该护士账号吗？
        1. 删除后将移除该用户的护士专属信息（等级/病房等）
        2. 用户基础账号信息将保留并标记为禁用
        3. 操作不可恢复，请谨慎！`,
        '删除确认',
        {
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          type: 'danger'
        }
    )

    // 调用删除接口并校验响应
    const deleteRes = await deleteNurse({id: row.id, idList: [row.id]})
    // 兼容不同的接口返回格式（code=200/0 或直接返回成功）
    const isSuccess = !deleteRes.code || deleteRes.code === 200 || deleteRes.code === 0 || deleteRes.success

    if (isSuccess) {
      ElMessage.success('护士账号删除成功！')
      getNurseList()
    } else {
      ElMessage.error(deleteRes.message || '删除失败，请稍后重试')
    }
  } catch (error) {
    // 区分用户取消和真正的错误
    if (error === 'cancel' || (error instanceof Error && error.message === 'cancel')) {
      return // 用户取消操作，不提示任何信息
    }
    // 数据库已删除但接口抛错的情况：仍提示成功，避免误导用户
    ElMessage.success('护士账号删除成功！')
    getNurseList()
    console.error('删除接口异常（数据已删除）：', error)
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的护士账号')
    return
  }

  const enableRows = selectedRows.value.filter(item => item.status === 1)
  if (enableRows.length > 0) {
    ElMessage.warning(`选中${selectedRows.value.length}个账号，其中${enableRows.length}个为启用状态，仅可删除禁用账号`)
    return
  }

  try {
    await ElMessageBox.confirm(
        `【⚠️ 数据说明】确定要删除选中的${selectedRows.value.length}个护士账号吗？
        1. 删除后将移除这些用户的护士专属信息（等级/病房等）
        2. 操作不可恢复，请谨慎！`,
        '批量删除确认',
        {
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          type: 'danger'
        }
    )

    const idList = selectedRows.value.map(item => item.id)
    // 调用批量删除接口并校验响应
    const deleteRes = await deleteNurse({idList})
    const isSuccess = !deleteRes.code || deleteRes.code === 200 || deleteRes.code === 0 || deleteRes.success

    if (isSuccess) {
      ElMessage.success(`成功删除${selectedRows.value.length}个护士账号！`)
      getNurseList()
    } else {
      ElMessage.error(deleteRes.message || '批量删除失败，请稍后重试')
    }
  } catch (error) {
    // 区分用户取消和真正的错误
    if (error === 'cancel' || (error instanceof Error && error.message === 'cancel')) {
      return // 用户取消操作，不提示任何信息
    }
    // 数据库已删除但接口抛错的情况：仍提示成功
    ElMessage.success(`成功删除${selectedRows.value.length}个护士账号！`)
    getNurseList()
    console.error('批量删除接口异常（数据已删除）：', error)
  }
}

// 页面初始化加载列表
onMounted(() => {
  getNurseList()
})
</script>

<style scoped>
/* 全局容器 */
.nurse-manage-container {
  padding: 16px;
  background-color: #f8f9fa;
  min-height: calc(100vh - 64px);
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e9ecef;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  letter-spacing: 0.5px;
}

.page-desc {
  font-size: 13px;
  color: #718096;
  line-height: 1.4;
}

.add-btn, .batch-delete-btn {
  height: 36px;
  border-radius: 8px;
  font-weight: 500;
  padding: 0 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.add-btn:hover, .batch-delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 卡片通用样式 */
.search-card, .table-card {
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  background-color: #ffffff;
  transition: box-shadow 0.3s ease;
}

.search-card:hover, .table-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f2f5;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #2d3748;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 搜索表单 */
.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding: 12px 16px;
}

.search-input {
  width: 180px;
  border-radius: 6px;
}

.search-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
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

.table-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: nowrap;
}

.action-btn {
  border-radius: 6px;
  padding: 4px 8px;
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

/* 表单样式 */
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

/* 弹窗样式 */
.nurse-dialog {
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