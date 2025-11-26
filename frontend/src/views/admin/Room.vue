<template>
  <div class="room-page">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="所属楼宇">
          <el-select v-model="searchForm.buildingId" placeholder="请选择楼宇" clearable style="width: 200px">
            <el-option v-for="b in buildingList" :key="b.id" :label="b.buildingName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="房间状态">
          <el-select v-model="searchForm.roomStatus" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="空闲" :value="1" />
            <el-option label="部分入住" :value="2" />
            <el-option label="已满" :value="3" />
            <el-option label="维修中" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData" :icon="Search">查询</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
          <el-button type="success" @click="handleAdd" :icon="Plus">新增</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="buildingName" label="所属楼宇" />
        <el-table-column prop="roomNumber" label="房间号" />
        <el-table-column prop="floor" label="楼层" />
        <el-table-column prop="roomType" label="房间类型">
          <template #default="{ row }">
            <el-tag v-if="row.roomType === 1">4人间</el-tag>
            <el-tag v-else-if="row.roomType === 2" type="success">6人间</el-tag>
            <el-tag v-else type="info">其他</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalBeds" label="总床位" />
        <el-table-column prop="availableBeds" label="剩余床位" />
        <el-table-column prop="roomStatus" label="状态">
          <template #default="{ row }">
            <el-tag v-if="row.roomStatus === 1" type="success">空闲</el-tag>
            <el-tag v-else-if="row.roomStatus === 2" type="primary">部分入住</el-tag>
            <el-tag v-else-if="row.roomStatus === 3" type="warning">已满</el-tag>
            <el-tag v-else type="danger">维修中</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="所属楼宇" prop="buildingId">
          <el-select v-model="formData.buildingId" style="width: 100%">
            <el-option v-for="b in buildingList" :key="b.id" :label="b.buildingName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="formData.roomNumber" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="formData.floor" :min="1" />
        </el-form-item>
        <el-form-item label="房间类型" prop="roomType">
          <el-radio-group v-model="formData.roomType" @change="handleRoomTypeChange">
            <el-radio :label="1">4人间</el-radio>
            <el-radio :label="2">6人间</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="总床位数" prop="totalBeds">
          <el-input-number v-model="formData.totalBeds" :min="1" :max="8" disabled />
        </el-form-item>
        <el-form-item label="可用床位" prop="availableBeds">
          <el-input-number v-model="formData.availableBeds" :min="0" :max="formData.totalBeds" />
        </el-form-item>
        <el-form-item label="状态" prop="roomStatus">
          <el-select v-model="formData.roomStatus" style="width: 100%">
            <el-option label="空闲" :value="1" />
            <el-option label="部分入住" :value="2" />
            <el-option label="已满" :value="3" />
            <el-option label="维修中" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
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
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getRoomPage, addRoom, updateRoom, deleteRoom } from '@/api/room'
import { getBuildingList } from '@/api/building'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增房间')
const submitLoading = ref(false)
const formRef = ref(null)
const buildingList = ref([])

const searchForm = reactive({
  buildingId: '',
  roomNumber: '',
  roomStatus: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  id: null,
  buildingId: '',
  roomNumber: '',
  floor: 1,
  roomType: 1,
  totalBeds: 4,
  availableBeds: 4,
  roomStatus: 1,
  description: ''
})

const formRules = {
  buildingId: [{ required: true, message: '请选择所属楼宇', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
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
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadBuildingList = async () => {
  try {
    const res = await getBuildingList()
    buildingList.value = res.data
  } catch (error) {
    ElMessage.error('加载楼宇列表失败')
  }
}

const handleReset = () => {
  Object.assign(searchForm, {
    buildingId: '',
    roomNumber: '',
    roomStatus: ''
  })
  loadData()
}

const handleRoomTypeChange = (value) => {
  // 根据房间类型自动设置床位数
  if (value === 1) {
    formData.totalBeds = 4
    formData.availableBeds = 4
  } else if (value === 2) {
    formData.totalBeds = 6
    formData.availableBeds = 6
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增房间'
  Object.assign(formData, {
    id: null,
    buildingId: '',
    roomNumber: '',
    floor: 1,
    roomType: 1,
    totalBeds: 4,
    availableBeds: 4,
    roomStatus: 1,
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑房间'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateRoom(formData)
      ElMessage.success('更新成功')
    } else {
      await addRoom(formData)
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
  ElMessageBox.confirm('确定要删除该房间吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRoom(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  loadData()
  loadBuildingList()
})
</script>

<style scoped>
.room-page {
  padding: 0;
}
</style>
