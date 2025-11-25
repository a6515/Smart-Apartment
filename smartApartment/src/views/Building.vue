<template>
  <div class="building-container">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="楼宇名称">
          <el-input v-model="searchForm.buildingName" placeholder="请输入楼宇名称" clearable />
        </el-form-item>
        <el-form-item label="楼宇类型">
          <el-select v-model="searchForm.buildingType" placeholder="请选择" clearable>
            <el-option label="男生宿舍" :value="1" />
            <el-option label="女生宿舍" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="buildingName" label="楼宇名称" />
        <el-table-column prop="buildingCode" label="楼宇编号" />
        <el-table-column prop="buildingType" label="楼宇类型">
          <template #default="{ row }">
            <el-tag :type="row.buildingType === 1 ? 'primary' : 'danger'">
              {{ row.buildingType === 1 ? '男生宿舍' : '女生宿舍' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="floors" label="楼层数" />
        <el-table-column prop="totalRooms" label="房间总数" />
        <el-table-column prop="managerName" label="宿管姓名" />
        <el-table-column prop="managerPhone" label="联系电话" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadData"
        @size-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="楼宇名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="请输入楼宇名称" />
        </el-form-item>
        <el-form-item label="楼宇编号" prop="buildingCode">
          <el-input v-model="form.buildingCode" placeholder="请输入楼宇编号" />
        </el-form-item>
        <el-form-item label="楼宇类型" prop="buildingType">
          <el-radio-group v-model="form.buildingType">
            <el-radio :label="1">男生宿舍</el-radio>
            <el-radio :label="2">女生宿舍</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="楼层数" prop="floors">
          <el-input-number v-model="form.floors" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="房间总数" prop="totalRooms">
          <el-input-number v-model="form.totalRooms" :min="1" :max="1000" />
        </el-form-item>
        <el-form-item label="楼宇地址">
          <el-input v-model="form.address" placeholder="请输入楼宇地址" />
        </el-form-item>
        <el-form-item label="宿管姓名">
          <el-input v-model="form.managerName" placeholder="请输入宿管姓名" />
        </el-form-item>
        <el-form-item label="宿管电话">
          <el-input v-model="form.managerPhone" placeholder="请输入宿管电话" />
        </el-form-item>
        <el-form-item label="楼宇描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuildingPage, addBuilding, updateBuilding, deleteBuilding } from '@/api/building'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  buildingName: '',
  buildingType: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  buildingName: '',
  buildingCode: '',
  buildingType: 1,
  floors: 6,
  totalRooms: 0,
  address: '',
  managerName: '',
  managerPhone: '',
  description: '',
  status: 1
})

const rules = {
  buildingName: [{ required: true, message: '请输入楼宇名称', trigger: 'blur' }],
  buildingCode: [{ required: true, message: '请输入楼宇编号', trigger: 'blur' }],
  buildingType: [{ required: true, message: '请选择楼宇类型', trigger: 'change' }],
  floors: [{ required: true, message: '请输入楼层数', trigger: 'blur' }],
  totalRooms: [{ required: true, message: '请输入房间总数', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBuildingPage({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.buildingName = ''
  searchForm.buildingType = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增楼宇'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑楼宇'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该楼宇吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteBuilding(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 用户取消
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (form.id) {
      await updateBuilding(form)
      ElMessage.success('更新成功')
    } else {
      await addBuilding(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    buildingName: '',
    buildingCode: '',
    buildingType: 1,
    floors: 6,
    totalRooms: 0,
    address: '',
    managerName: '',
    managerPhone: '',
    description: '',
    status: 1
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}
</style>
