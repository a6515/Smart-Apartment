<template>
  <div class="checkout-manage">
    <el-card>
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待审核" name="pending"></el-tab-pane>
        <el-tab-pane label="已同意" name="approved"></el-tab-pane>
        <el-tab-pane label="已驳回" name="rejected"></el-tab-pane>
      </el-tabs>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="学生姓名">
          <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="申请状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="待审核" :value="1" />
            <el-option label="已同意" :value="2" />
            <el-option label="已驳回" :value="3" />
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
        <el-table-column prop="buildingName" label="楼宇" />
        <el-table-column prop="roomNumber" label="房间号" />
        <el-table-column prop="bedNumber" label="床位号" />
        <el-table-column prop="reason" label="退宿原因" />
        <el-table-column prop="checkoutDate" label="预计退宿日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 2" type="success">已同意</el-tag>
            <el-tag v-else-if="row.status === 3" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
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
    <el-dialog v-model="approveDialogVisible" :title="approveType === 2 ? '同意退宿' : '驳回退宿'" width="500px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="学生姓名">
          <span>{{ currentRow?.studentName }}</span>
        </el-form-item>
        <el-form-item label="退宿原因">
          <span>{{ currentRow?.reason }}</span>
        </el-form-item>
        <el-form-item label="详细说明">
          <span>{{ currentRow?.description }}</span>
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
    <el-dialog v-model="detailDialogVisible" title="退宿申请详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生姓名">{{ currentRow?.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentRow?.studentNumber }}</el-descriptions-item>
        <el-descriptions-item label="楼宇">{{ currentRow?.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentRow?.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ currentRow?.bedNumber }}</el-descriptions-item>
        <el-descriptions-item label="退宿原因">{{ currentRow?.reason }}</el-descriptions-item>
        <el-descriptions-item label="预计退宿日期" :span="2">{{ currentRow?.checkoutDate }}</el-descriptions-item>
        <el-descriptions-item label="详细说明" :span="2">{{ currentRow?.description }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ currentRow?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="申请状态" :span="2">
          <el-tag v-if="currentRow?.status === 1" type="warning">待审核</el-tag>
          <el-tag v-else-if="currentRow?.status === 2" type="success">已同意</el-tag>
          <el-tag v-else-if="currentRow?.status === 3" type="danger">已驳回</el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRow?.approverName" label="审批人">
          {{ currentRow?.approverName }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRow?.approveTime" label="审批时间">
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
import { getCheckoutPage, approveCheckout } from '@/api/checkout'

const loading = ref(false)
const tableData = ref([])
const approveDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const approveLoading = ref(false)
const currentRow = ref(null)
const approveType = ref(2) // 2-同意 3-驳回
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
      params.status = activeTab.value === 'pending' ? 1 : activeTab.value === 'approved' ? 2 : 3
    }
    const res = await getCheckoutPage(params)
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
  approveForm.remark = ''
  approveDialogVisible.value = true
}

const handleConfirmApprove = async () => {
  try {
    approveLoading.value = true
    await approveCheckout({
      id: currentRow.value.id,
      status: approveType.value,
      remark: approveForm.remark
    })
    ElMessage.success('审批成功')
    approveDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    approveLoading.value = false
  }
}

const handleTabChange = (tab) => {
  pagination.current = 1
  searchForm.status = ''
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.checkout-manage {
  padding: 0;
}
</style>
