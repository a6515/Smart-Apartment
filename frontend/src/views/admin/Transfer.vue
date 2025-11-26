<template>
  <div class="transfer-manage">
    <el-card>
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待审核" name="pending"></el-tab-pane>
        <el-tab-pane label="已驳回" name="rejected"></el-tab-pane>
        <el-tab-pane label="已完成" name="completed"></el-tab-pane>
      </el-tabs>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="学生姓名">
          <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="申请状态" v-if="activeTab === 'all'">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="待审核" :value="1" />
            <el-option label="已驳回" :value="3" />
            <el-option label="已完成" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData" :icon="Search">查询</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="studentNumber" label="学号" />
        <el-table-column label="当前房间" width="200">
          <template #default="{ row }">
            {{ row.currentBuildingName }} - {{ row.currentRoomNumber }} - {{ row.currentBedNumber }}
          </template>
        </el-table-column>
        <el-table-column label="目标房间" width="200">
          <template #default="{ row }">
            {{ row.targetBuildingName }} - {{ row.targetRoomNumber }}
            <el-tag size="small" type="info">{{ row.targetRoomType === 1 ? '4人间' : '6人间' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="换宿原因" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 2" type="success">已同意</el-tag>
            <el-tag v-else-if="row.status === 3" type="danger">已驳回</el-tag>
            <el-tag v-else-if="row.status === 5" type="info">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 1" type="success" size="small" @click="handleApprove(row, 2)">
              同意
            </el-button>
            <el-button v-if="row.status === 1" type="danger" size="small" @click="handleApprove(row, 3)">
              驳回
            </el-button>
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
    <el-dialog v-model="approveDialogVisible" :title="approveType === 2 ? '同意换宿' : '驳回换宿'" width="600px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="学生姓名">
          <span>{{ currentRow?.studentName }}</span>
        </el-form-item>
        <el-form-item label="当前房间">
          <span>{{ currentRow?.currentBuildingName }} - {{ currentRow?.currentRoomNumber }} - {{ currentRow?.currentBedNumber }}</span>
        </el-form-item>
        <el-form-item label="目标房间">
          <span>{{ currentRow?.targetBuildingName }} - {{ currentRow?.targetRoomNumber }}</span>
        </el-form-item>
        <el-form-item label="换宿原因">
          <span>{{ currentRow?.reason }}</span>
        </el-form-item>
        <el-form-item label="详细说明">
          <span>{{ currentRow?.description }}</span>
        </el-form-item>
        <el-form-item v-if="approveType === 2" label="分配床位" required>
          <el-select v-model="approveForm.bedId" placeholder="请选择床位" style="width: 100%" @focus="loadAvailableBeds">
            <el-option v-for="bed in availableBeds" :key="bed.id" 
                      :label="`床位 ${bed.bedNumber}`" :value="bed.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批意见" required>
          <el-input v-model="approveForm.remark" type="textarea" :rows="4" 
                    placeholder="请输入审批意见" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmApprove" :loading="approveLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="换宿申请详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生姓名">{{ currentRow?.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentRow?.studentNumber }}</el-descriptions-item>
        <el-descriptions-item label="当前楼宇">{{ currentRow?.currentBuildingName }}</el-descriptions-item>
        <el-descriptions-item label="当前房间">{{ currentRow?.currentRoomNumber }}</el-descriptions-item>
        <el-descriptions-item label="当前床位">{{ currentRow?.currentBedNumber }}</el-descriptions-item>
        <el-descriptions-item label="目标楼宇">{{ currentRow?.targetBuildingName }}</el-descriptions-item>
        <el-descriptions-item label="目标房间">{{ currentRow?.targetRoomNumber }}</el-descriptions-item>
        <el-descriptions-item label="房间类型">
          {{ currentRow?.targetRoomType === 1 ? '4人间' : '6人间' }}
        </el-descriptions-item>
        <el-descriptions-item label="换宿原因">{{ currentRow?.reason }}</el-descriptions-item>
        <el-descriptions-item label="详细说明" :span="2">{{ currentRow?.description }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ currentRow?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="申请状态" :span="2">
          <el-tag v-if="currentRow?.status === 1" type="warning">待审核</el-tag>
          <el-tag v-else-if="currentRow?.status === 2" type="success">已同意</el-tag>
          <el-tag v-else-if="currentRow?.status === 3" type="danger">已驳回</el-tag>
          <el-tag v-else-if="currentRow?.status === 5" type="info">已完成</el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRow?.assignedBedNumber" label="分配床位">
          {{ currentRow?.assignedBedNumber }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRow?.approverName" label="审批人">
          {{ currentRow?.approverName }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRow?.approveTime" label="审批时间" :span="2">
          {{ currentRow?.approveTime }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRow?.remark" label="审批意见" :span="2">
          {{ currentRow?.remark }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getTransferPage, approveTransfer } from '@/api/transfer'
import { getAvailableBeds } from '@/api/bed'

const loading = ref(false)
const tableData = ref([])
const approveDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const approveLoading = ref(false)
const currentRow = ref(null)
const approveType = ref(2) // 2-同意 3-驳回
const availableBeds = ref([])
const activeTab = ref('all')

const searchForm = reactive({
  studentName: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const approveForm = reactive({
  bedId: '',
  remark: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    if (activeTab.value !== 'all') {
      params.status = activeTab.value === 'pending' ? 1 : activeTab.value === 'rejected' ? 3 : 5
    }
    const res = await getTransferPage(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  Object.assign(searchForm, {
    studentName: '',
    status: ''
  })
  loadData()
}

const handleView = (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

const handleApprove = (row, type) => {
  currentRow.value = row
  approveType.value = type
  approveForm.bedId = ''
  approveForm.remark = ''
  approveDialogVisible.value = true
}

const loadAvailableBeds = async () => {
  if (!currentRow.value?.targetRoomId) return
  
  try {
    const res = await getAvailableBeds(currentRow.value.targetRoomId)
    availableBeds.value = res.data
  } catch (error) {
    ElMessage.error('加载可用床位失败')
    availableBeds.value = []
  }
}

const handleConfirmApprove = async () => {
  if (!approveForm.remark) {
    ElMessage.warning('请输入审批意见')
    return
  }

  if (approveType.value === 2 && !approveForm.bedId) {
    ElMessage.warning('请选择分配的床位')
    return
  }

  approveLoading.value = true
  try {
    await approveTransfer({
      id: currentRow.value.id,
      status: approveType.value,
      remark: approveForm.remark,
      bedId: approveForm.bedId
    })
    ElMessage.success('审批成功')
    approveDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '审批失败')
  } finally {
    approveLoading.value = false
  }
}

const handleTabChange = () => {
  pagination.current = 1
  searchForm.status = ''
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.transfer-manage {
  padding: 0;
}
</style>
