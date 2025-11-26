<template>
  <div class="checkin-page">
    <el-card>
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="mb-4">
        <el-tab-pane label="全部" name="all">
          <template #label>
            <span>全部 <el-badge :value="totalCount" :max="99" class="ml-1" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="待审批" name="pending">
          <template #label>
            <span>待审批 <el-badge :value="pendingCount" :max="99" class="ml-1" type="primary" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已确认入住" name="checked_in">
          <template #label>
            <span>已确认入住 <el-badge :value="checkedInCount" :max="99" class="ml-1" type="success" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已退宿" name="checked_out">
          <template #label>
            <span>已退宿 <el-badge :value="checkedOutCount" :max="99" class="ml-1" type="warning" /></span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="申请人">
          <el-input v-model="searchForm.studentName" placeholder="请输入申请人姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="审批状态" v-if="activeTab === 'all'">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 200px">
            <el-option label="待审批" :value="1" />
            <el-option label="审批通过" :value="2" />
            <el-option label="审批驳回" :value="3" />
            <el-option label="已确认入住" :value="4" />
            <el-option label="已退宿" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData" :icon="Search">查询</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentName" label="申请人" width="100" />
        <el-table-column prop="studentNumber" label="学号" width="120" />
        <el-table-column prop="buildingName" label="期望楼宇" width="120" />
        <el-table-column prop="applicationReason" label="申请理由" min-width="150" show-overflow-tooltip />
        <el-table-column prop="roomNumber" label="分配房间" width="100" />
        <el-table-column prop="bedNumber" label="床位号" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1">待审批</el-tag>
            <el-tag v-else-if="row.status === 2" type="success">审批通过</el-tag>
            <el-tag v-else-if="row.status === 3" type="danger">审批驳回</el-tag>
            <el-tag v-else-if="row.status === 4" type="info">已确认入住</el-tag>
            <el-tag v-else-if="row.status === 5" type="warning">已退宿</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="primary" size="small" @click="handleApprove(row)">审批</el-button>
            <el-button v-if="row.status === 2" type="success" size="small" @click="handleConfirm(row)">确认入住</el-button>
            <el-button v-if="row.status === 1 || row.status === 2 || row.status === 3" type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            <el-tag v-if="row.status === 4 || row.status === 5" type="info" size="small">不可删除</el-tag>
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

    <!-- 审批对话框 -->
    <el-dialog v-model="dialogVisible" title="入住审批" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="申请人">
          <el-input :value="currentApplication?.studentName + ' (' + currentApplication?.studentNumber + ')'" disabled />
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input 
            :value="currentApplication?.applicationReason || '无'" 
            type="textarea" 
            :rows="2" 
            disabled 
          />
        </el-form-item>
        <el-form-item label="审批结果" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="2">通过</el-radio>
            <el-radio :label="3">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="formData.status === 2">
          <el-form-item label="期望楼宇">
            <el-input :value="currentApplication?.buildingName || '未选择'" disabled />
          </el-form-item>
          <el-form-item label="期望房间类型">
            <el-input :value="currentApplication?.preferredRoomType === 1 ? '4人间' : currentApplication?.preferredRoomType === 2 ? '6人间' : '未选择'" disabled />
          </el-form-item>
          <el-form-item label="分配房间" prop="roomId">
            <el-select v-model="formData.roomId" placeholder="请选择房间" @change="handleRoomChange" style="width: 100%">
              <el-option v-for="r in roomList" :key="r.id" :label="`${r.roomNumber} (剩余${r.availableBeds}个床位)`" :value="r.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="分配床位" prop="bedId">
            <el-select v-model="formData.bedId" placeholder="请选择床位" style="width: 100%" :disabled="!formData.roomId">
              <el-option v-for="bed in bedList" :key="bed.id" :label="bed.bedNumber" :value="bed.id" />
            </el-select>
          </el-form-item>
        </template>
        <el-form-item label="审批意见" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
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
import { Search, Refresh } from '@element-plus/icons-vue'
import { getCheckInPage, approveCheckIn, confirmCheckIn, deleteCheckIn } from '@/api/checkin'
import { getBuildingList } from '@/api/building'
import { getAvailableRooms } from '@/api/room'
import { getAvailableBeds } from '@/api/bed'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const buildingList = ref([])
const roomList = ref([])
const bedList = ref([])
const currentApplication = ref(null)

const searchForm = reactive({
  studentName: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  id: null,
  status: 2,
  roomId: '',
  bedId: '',
  remark: ''
})

const formRules = {
  status: [{ required: true, message: '请选择审批结果', trigger: 'change' }],
  roomId: [{ required: true, message: '请分配房间', trigger: 'change' }],
  bedId: [{ required: true, message: '请分配床位', trigger: 'change' }]
}

const activeTab = ref('all')

const totalCount = ref(0)
const pendingCount = ref(0)
const checkedInCount = ref(0)
const checkedOutCount = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    if (activeTab.value !== 'all') {
      params.status = activeTab.value === 'pending' ? 1 : activeTab.value === 'checked_in' ? 4 : 5
    }
    const res = await getCheckInPage(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
    totalCount.value = res.data.totalCount
    pendingCount.value = res.data.pendingCount
    checkedInCount.value = res.data.checkedInCount
    checkedOutCount.value = res.data.checkedOutCount
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.studentName = ''
  searchForm.status = ''
  loadData()
}

const loadBuildingList = async () => {
  try {
    const res = await getBuildingList()
    buildingList.value = res.data
  } catch (error) {
    ElMessage.error('加载楼宇列表失败')
  }
}

const handleRoomChange = async (roomId) => {
  formData.bedId = ''
  bedList.value = []
  if (roomId) {
    try {
      const res = await getAvailableBeds(roomId)
      bedList.value = res.data
    } catch (error) {
      ElMessage.error('加载床位列表失败')
    }
  }
}

const handleApprove = async (row) => {
  currentApplication.value = row
  formData.id = row.id
  formData.status = 2
  formData.roomId = ''
  formData.bedId = ''
  formData.remark = ''
  bedList.value = []
  
  // 如果学生选择了期望楼宇，自动加载该楼宇对应房间类型的可用房间
  if (row.preferredBuildingId) {
    try {
      const res = await getAvailableRooms(row.preferredBuildingId, row.preferredRoomType)
      roomList.value = res.data
      if (roomList.value.length === 0) {
        const roomTypeText = row.preferredRoomType === 1 ? '4人间' : row.preferredRoomType === 2 ? '6人间' : ''
        ElMessage.warning(`该楼宇暂无可用的${roomTypeText}房间`)
      }
    } catch (error) {
      ElMessage.error('加载房间列表失败')
      roomList.value = []
    }
  } else {
    roomList.value = []
    ElMessage.warning('学生未选择期望楼宇')
  }
  
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const submitData = {
      id: formData.id,
      status: formData.status,
      remark: formData.remark
    }
    if (formData.status === 2) {
      submitData.roomId = formData.roomId
      submitData.bedId = formData.bedId
    }
    await approveCheckIn(submitData)
    ElMessage.success('审批成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleConfirm = async (row) => {
  ElMessageBox.confirm('确认该学生已入住？', '提示', {
    type: 'info'
  }).then(async () => {
    try {
      await confirmCheckIn(row.id)
      ElMessage.success('确认入住成功')
      loadData()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该入住申请吗？${row.status === 2 ? '已分配的床位将被释放。' : ''}`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCheckIn(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

const handleTabChange = (tab) => {
  pagination.current = 1
  searchForm.status = ''
  loadData()
}

onMounted(() => {
  loadData()
  loadBuildingList()
})
</script>

<style scoped>
.checkin-page {
  padding: 0;
}
</style>
