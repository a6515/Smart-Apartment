<template>
  <div class="room-container">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="楼宇">
          <el-select v-model="searchForm.buildingId" placeholder="请选择楼宇" clearable>
            <el-option 
              v-for="building in buildingList" 
              :key="building.id" 
              :label="building.buildingName" 
              :value="building.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="房间状态">
          <el-select v-model="searchForm.roomStatus" placeholder="请选择" clearable>
            <el-option label="空闲" :value="1" />
            <el-option label="部分入住" :value="2" />
            <el-option label="已满" :value="3" />
            <el-option label="维修中" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">查询</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
          <el-button type="success" @click="handleAdd" :icon="Plus">新增</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="buildingName" label="楼宇名称" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="roomType" label="房间类型" width="100">
          <template #default="{ row }">
            {{ getRoomTypeName(row.roomType) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalBeds" label="总床位" width="80" />
        <el-table-column prop="availableBeds" label="可用床位" width="90" />
        <el-table-column prop="area" label="面积(㎡)" width="90" />
        <el-table-column prop="price" label="价格(元/学期)" width="120" />
        <el-table-column prop="roomStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.roomStatus === 1" type="success">空闲</el-tag>
            <el-tag v-else-if="row.roomStatus === 2" type="warning">部分入住</el-tag>
            <el-tag v-else-if="row.roomStatus === 3" type="info">已满</el-tag>
            <el-tag v-else type="danger">维修中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" :icon="Edit">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      @close="handleDialogClose">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="楼宇" prop="buildingId">
          <el-select v-model="formData.buildingId" placeholder="请选择楼宇" style="width: 100%">
            <el-option 
              v-for="building in buildingList" 
              :key="building.id" 
              :label="building.buildingName" 
              :value="building.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="formData.roomNumber" placeholder="请输入房间号，如：101" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="formData.floor" :min="1" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="房间类型" prop="roomType">
          <el-select v-model="formData.roomType" placeholder="请选择房间类型" style="width: 100%">
            <el-option label="单人间" :value="1" />
            <el-option label="双人间" :value="2" />
            <el-option label="四人间" :value="3" />
            <el-option label="六人间" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="总床位数" prop="totalBeds">
          <el-input-number v-model="formData.totalBeds" :min="1" :max="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="面积(㎡)" prop="area">
          <el-input-number v-model="formData.area" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="价格(元)" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="房间状态" prop="roomStatus">
          <el-select v-model="formData.roomStatus" placeholder="请选择状态" style="width: 100%">
            <el-option label="空闲" :value="1" />
            <el-option label="部分入住" :value="2" />
            <el-option label="已满" :value="3" />
            <el-option label="维修中" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入房间描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getRoomPage, saveRoom, updateRoom, deleteRoom } from '@/api/room'
import { getBuildingList } from '@/api/building'

const loading = ref(false)
const tableData = ref([])
const buildingList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  buildingId: null,
  roomNumber: '',
  roomStatus: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  id: null,
  buildingId: null,
  roomNumber: '',
  floor: 1,
  roomType: 3,
  totalBeds: 4,
  area: 20,
  price: 1200,
  roomStatus: 1,
  description: ''
})

const formRules = {
  buildingId: [{ required: true, message: '请选择楼宇', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择房间类型', trigger: 'change' }],
  totalBeds: [{ required: true, message: '请输入总床位数', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const getRoomTypeName = (type) => {
  const typeMap = { 1: '单人间', 2: '双人间', 3: '四人间', 4: '六人间' }
  return typeMap[type] || '未知'
}

const loadBuildingList = async () => {
  try {
    const res = await getBuildingList()
    buildingList.value = res.data
  } catch (error) {
    console.error('加载楼宇列表失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await getRoomPage(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.buildingId = null
  searchForm.roomNumber = ''
  searchForm.roomStatus = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增房间'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑房间'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    id: null,
    buildingId: null,
    roomNumber: '',
    floor: 1,
    roomType: 3,
    totalBeds: 4,
    area: 20,
    price: 1200,
    roomStatus: 1,
    description: ''
  })
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateRoom(formData)
      ElMessage.success('更新成功')
    } else {
      await saveRoom(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除房间"${row.roomNumber}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRoom(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

onMounted(() => {
  loadBuildingList()
  loadData()
})
</script>

<style scoped>
.room-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
