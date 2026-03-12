<template>
  <div class="department-manage-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">科室管理</h2>
      </div>
      <div class="header-right">
        <el-button
            type="primary"
            :icon="Plus"
            class="add-btn"
            @click="openAddDialog"
            size="default"
        >
          新增科室
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
        <el-form-item label="科室名称" prop="deptName">
          <el-input
              v-model="searchForm.deptName"
              placeholder="请输入科室名称"
              clearable
              :prefix-icon="OfficeBuilding"
              class="search-input"
          />
        </el-form-item>
        <el-form-item label="科室描述" prop="deptDesc">
          <el-input
              v-model="searchForm.deptDesc"
              placeholder="请输入科室描述"
              clearable
              :prefix-icon="Document"
              class="search-input"
          />
        </el-form-item>
        <el-form-item label="科室状态" prop="status">
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
          <el-button type="primary" @click="getDepartmentList" :icon="Search">
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
            科室列表
          </span>
        </div>
      </template>

      <el-table
          v-loading="loading"
          :data="departmentList"
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
        <el-table-column prop="deptName" label="科室名称" min-width="150">
          <template #default="scope">
            <div class="dept-cell">
              <el-icon :size="18" color="#4299e1">
                <OfficeBuilding/>
              </el-icon>
              <span class="deptname-text">{{ scope.row.deptName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="deptDesc" label="科室描述" min-width="200" align="center">
          <template #default="scope">
            <span class="desc-text">{{ scope.row.deptDesc || '未设置' }}</span>
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

    <!-- 新增科室弹窗 -->
    <el-dialog
        v-model="addDialogVisible"
        title="新增科室"
        width="600px"
        destroy-on-close
        :close-on-click-modal="false"
        :modal-style="{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }"
        class="department-dialog"
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
        <el-form-item label="科室名称" prop="deptName">
          <el-input
              v-model="addForm.deptName"
              placeholder="请输入科室名称"
              clearable
              class="form-input"
              :name="`deptName_${randomKey}`"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="科室描述" prop="deptDesc">
          <el-input
              v-model="addForm.deptDesc"
              placeholder="请输入科室描述（如：内科主要处理呼吸系统疾病等）"
              clearable
              type="textarea"
              :rows="3"
              class="form-input"
              :name="`deptDesc_${randomKey}`"
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

    <!-- 编辑科室弹窗 -->
    <el-dialog
        v-model="editDialogVisible"
        title="编辑科室"
        width="600px"
        destroy-on-close
        :close-on-click-modal="false"
        :modal-style="{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }"
        class="department-dialog"
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
        <el-form-item label="科室名称" prop="deptName">
          <el-input
              v-model="editForm.deptName"
              placeholder="请输入科室名称"
              clearable
              class="form-input"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="科室描述" prop="deptDesc">
          <el-input
              v-model="editForm.deptDesc"
              placeholder="请输入科室描述"
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
  OfficeBuilding,
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
  getDepartmentPage,
  deleteDepartment,
  addDepartment,
  editDepartment,
  updateDepartmentStatus,
  getDepartmentById
} from '@/api/system/department'
import dayjs from 'dayjs'

// 生成随机key防止浏览器自动填充
const randomKey = ref(Math.random().toString(36).substring(2, 10))

// 加载状态
const loading = ref(false)
// 科室列表数据
const departmentList = ref([])
// 总条数
const total = ref(0)
// 选中的行（批量操作）
const selectedRows = ref([])
// 搜索表单
const searchForm = ref({
  pageNum: 1,
  pageSize: 10,
  deptName: '',
  deptDesc: '',
  status: ''
})

// 新增科室相关逻辑
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref(null)
const addForm = ref({
  deptName: '',
  deptDesc: '',
  status: '1'
})

// 新增表单校验规则
const addRules = ref({
  deptName: [{required: true, message: '请输入科室名称', trigger: 'blur'}],
  deptDesc: [{required: true, message: '请输入科室描述', trigger: 'blur'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
})

// 编辑相关逻辑
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  id: '',
  deptName: '',
  deptDesc: '',
  status: '1'
})

// 编辑表单校验规则
const editRules = ref({
  id: [{required: true, message: '科室ID不能为空', trigger: 'blur'}],
  deptName: [{required: true, message: '请输入科室名称', trigger: 'blur'}],
  deptDesc: [{required: true, message: '请输入科室描述', trigger: 'blur'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
})

// 打开新增弹窗
const openAddDialog = () => {
  // 重新生成随机key，防止自动填充
  randomKey.value = Math.random().toString(36).substring(2, 10)

  // 清空表单
  addForm.value = {
    deptName: '',
    deptDesc: '',
    status: '1'
  }

  // 延迟打开弹窗
  setTimeout(() => {
    addDialogVisible.value = true
  }, 0)
}

// 打开编辑弹窗
const openEditDialog = async (row) => {
  try {
    // 获取科室详情
    const detailRes = await getDepartmentById(row.id)
    const deptData = detailRes.data || row

    editForm.value = {
      id: deptData.id,
      deptName: deptData.deptName || '',
      deptDesc: deptData.deptDesc || '',
      status: deptData.status.toString()
    }
    editDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取科室信息失败：' + error.message)
  }
}

// 提交新增表单
const submitAddForm = async () => {
  if (!addFormRef.value) return

  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      const submitData = {
        ...addForm.value,
        status: Number(addForm.value.status)
      }

      addLoading.value = true
      try {
        await addDepartment(submitData)
        ElMessage.success('科室新增成功！')
        addDialogVisible.value = false
        getDepartmentList()
      } catch (error) {
        ElMessage.error(error.message || '新增失败，请重试')
        console.error('新增科室失败详情：', error)
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
        status: Number(editForm.value.status)
      }

      editLoading.value = true
      try {
        await editDepartment(submitData)
        ElMessage.success('科室修改成功！')
        editDialogVisible.value = false
        getDepartmentList()
      } catch (error) {
        ElMessage.error(error.message || '修改失败，请重试')
        console.error('修改科室失败详情：', error)
      } finally {
        editLoading.value = false
      }
    }
  })
}

// 获取科室列表
const getDepartmentList = async () => {
  try {
    loading.value = true
    // 处理筛选参数
    const params = {
      ...searchForm.value,
      ...(searchForm.value.status ? {status: Number(searchForm.value.status)} : {})
    }
    const res = await getDepartmentPage(params)
    departmentList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    departmentList.value = []
    total.value = 0
    ElMessage.error('获取科室列表失败：' + error.message)
    console.error('查询科室列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索条件
const resetSearch = () => {
  searchForm.value = {
    pageNum: 1,
    pageSize: 10,
    deptName: '',
    deptDesc: '',
    status: ''
  }
  getDepartmentList()
}

// 分页相关方法
const handleSizeChange = (val) => {
  searchForm.value.pageSize = val
  getDepartmentList()
}
const handleCurrentChange = (val) => {
  searchForm.value.pageNum = val
  getDepartmentList()
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
        `⚠️ 确认要${statusText}该科室吗？
      ✅ 科室所有数据将完整保留，仅状态变更
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

    await updateDepartmentStatus(statusData)
    ElMessage.success(`科室${statusText}成功！数据已完整保留`)
    getDepartmentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`状态修改失败：${error.message || '请检查接口是否只修改状态'}`)
    }
  }
}

// 单个删除（添加状态校验）
const handleSingleDelete = async (row) => {
  // 校验状态：启用状态禁止删除
  if (row.status === 1) {
    ElMessage.warning('只能删除禁用状态的科室账号，无法删除启用状态的账号')
    return
  }

  try {
    await ElMessageBox.confirm(
        `【⚠️ 高危操作】确定要彻底删除该科室吗？
        1. 删除后将彻底清除该科室的所有数据
        2. 操作不可逆，数据无法恢复，请务必谨慎！`,
        '彻底删除确认',
        {
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          type: 'danger'
        }
    )
    await deleteDepartment({id: row.id, idList: [row.id]})
    ElMessage.success('科室已彻底删除！所有相关数据已清除')
    getDepartmentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败：该科室不存在或已被删除')
    }
  }
}

// 批量删除（添加状态校验）
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的科室')
    return
  }

  // 校验选中项状态：过滤出启用状态的科室
  const enableRows = selectedRows.value.filter(item => item.status === 1)
  if (enableRows.length > 0) {
    ElMessage.warning(`选中${selectedRows.value.length}个科室，其中${enableRows.length}个为启用状态，仅可删除禁用科室`)
    return
  }

  try {
    await ElMessageBox.confirm(
        `【⚠️ 高危操作】确定要彻底删除选中的${selectedRows.value.length}个科室吗？
        1. 删除后将彻底清除这些科室的所有数据
        2. 操作不可逆，数据无法恢复，请务必谨慎！`,
        '批量彻底删除确认',
        {
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          type: 'danger'
        }
    )
    const idList = selectedRows.value.map(item => item.id)
    await deleteDepartment({idList})
    ElMessage.success(`成功彻底删除${selectedRows.value.length}个科室！所有相关数据已清除`)
    getDepartmentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败：部分科室可能不存在或已被删除')
    }
  }
}

// 页面初始化加载列表
onMounted(() => {
  getDepartmentList()
})
</script>

<style scoped>
/* 全局容器 */
.department-manage-container {
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

.dept-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.deptname-text {
  font-weight: 500;
  color: #2d3748;
}

.id-cell {
  font-family: 'Consolas', 'Monaco', monospace;
  color: #4299e1;
  font-weight: 600;
}

.desc-text {
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
.department-dialog {
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

  .desc-text {
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