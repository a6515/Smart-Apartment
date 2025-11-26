<template>
  <div class="repair-page">
    <el-card>
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待接单" name="pending"></el-tab-pane>
        <el-tab-pane label="已接单" name="accepted"></el-tab-pane>
        <el-tab-pane label="处理中" name="processing"></el-tab-pane>
        <el-tab-pane label="已完成" name="completed"></el-tab-pane>
      </el-tabs>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="报修类型">
          <el-select 
            v-model="searchForm.repairTypes" 
            placeholder="请选择报修类型" 
            clearable 
            multiple
            collapse-tags
            collapse-tags-tooltip
            style="width: 240px">
            <el-option label="水电维修" :value="1" />
            <el-option label="家具维修" :value="2" />
            <el-option label="网络故障" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修状态" v-if="activeTab === 'all'">
          <el-select 
            v-model="searchForm.statuses" 
            placeholder="请选择报修状态" 
            clearable 
            multiple
            collapse-tags
            collapse-tags-tooltip
            style="width: 240px">
            <el-option label="待接单" :value="1" />
            <el-option label="已接单" :value="2" />
            <el-option label="处理中" :value="3" />
            <el-option label="已完成" :value="4" />
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
        <el-table-column prop="studentName" label="报修人" />
        <el-table-column prop="buildingName" label="楼宇" />
        <el-table-column prop="roomNumber" label="房间号" />
        <el-table-column prop="repairType" label="报修类型">
          <template #default="{ row }">
            <el-tag v-if="row.repairType === 1">水电维修</el-tag>
            <el-tag v-else-if="row.repairType === 2">家具维修</el-tag>
            <el-tag v-else-if="row.repairType === 3">网络故障</el-tag>
            <el-tag v-else>其他</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="repairTitle" label="报修标题" width="150" show-overflow-tooltip />
        <el-table-column prop="repairContent" label="报修描述" show-overflow-tooltip />
        <el-table-column prop="repairStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.repairStatus === 1">待接单</el-tag>
            <el-tag v-else-if="row.repairStatus === 2" type="warning">已接单</el-tag>
            <el-tag v-else-if="row.repairStatus === 3" type="primary">处理中</el-tag>
            <el-tag v-else-if="row.repairStatus === 4" type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评价" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.satisfactionRating" type="success">已评价</el-tag>
            <span v-else style="color: #999">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.repairStatus === 1" type="primary" size="small" @click="handleAccept(row)">接单</el-button>
            <el-button v-if="row.repairStatus === 2" type="warning" size="small" @click="handleStartProcess(row)">开始处理</el-button>
            <el-button v-if="row.repairStatus === 3" type="success" size="small" @click="handleFinish(row)">完成</el-button>
            <el-button type="info" size="small" @click="handleViewDetail(row)">详情</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="报修详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报修人">{{ detailData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="楼宇">{{ detailData.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ detailData.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">
          <el-tag v-if="detailData.repairType === 1">水电维修</el-tag>
          <el-tag v-else-if="detailData.repairType === 2">家具维修</el-tag>
          <el-tag v-else-if="detailData.repairType === 3">网络故障</el-tag>
          <el-tag v-else>其他</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag v-if="detailData.urgencyLevel === 1" type="danger">紧急</el-tag>
          <el-tag v-else-if="detailData.urgencyLevel === 2" type="warning">普通</el-tag>
          <el-tag v-else type="info">不急</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修标题" :span="2">{{ detailData.repairTitle }}</el-descriptions-item>
        <el-descriptions-item label="报修描述" :span="2">{{ detailData.repairContent }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.repairStatus === 1">待接单</el-tag>
          <el-tag v-else-if="detailData.repairStatus === 2" type="warning">已接单</el-tag>
          <el-tag v-else-if="detailData.repairStatus === 3" type="primary">处理中</el-tag>
          <el-tag v-else-if="detailData.repairStatus === 4" type="success">已完成</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailData.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="接单时间" :span="2">{{ detailData.acceptTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间" :span="2">{{ detailData.finishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修备注" :span="2">{{ detailData.repairRemark || '-' }}</el-descriptions-item>
        
        <!-- 评价信息 -->
        <el-descriptions-item label="满意度评分" v-if="detailData.satisfactionRating">
          <el-rate v-model="detailData.satisfactionRating" disabled />
        </el-descriptions-item>
        <el-descriptions-item label="满意度评分" v-else :span="2">
          <span style="color: #999">暂未评价</span>
        </el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2" v-if="detailData.satisfactionComment">
          {{ detailData.satisfactionComment }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getRepairPage, acceptRepair, updateRepairStatus } from '@/api/repair'

const loading = ref(false)
const tableData = ref([])
const detailDialogVisible = ref(false)
const detailData = ref({})
const activeTab = ref('all')

const searchForm = reactive({
  repairTypes: [],
  statuses: []
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
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
      params.status = activeTab.value === 'pending' ? 1 : activeTab.value === 'accepted' ? 2 : activeTab.value === 'processing' ? 3 : 4
    }
    const res = await getRepairPage(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.repairTypes = []
  searchForm.statuses = []
  pagination.current = 1
  loadData()
}

const handleAccept = async (row) => {
  try {
    await acceptRepair({ id: row.id })
    ElMessage.success('接单成功')
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleStartProcess = async (row) => {
  try {
    await updateRepairStatus({ id: row.id, status: 3, remark: '正在处理中...' })
    ElMessage.success('状态已更新为处理中')
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleFinish = async (row) => {
  try {
    await updateRepairStatus({ id: row.id, status: 4, remark: '维修已完成' })
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleViewDetail = (row) => {
  detailData.value = { ...row }
  detailDialogVisible.value = true
}

const handleTabChange = () => {
  pagination.current = 1
  searchForm.statuses = []
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.repair-page {
  padding: 0;
}
</style>
