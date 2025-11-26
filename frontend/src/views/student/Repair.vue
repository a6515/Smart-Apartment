<template>
  <div class="student-repair">
    <el-row :gutter="20">
      <!-- 提交报修 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>提交报修</span>
          </template>
          
          <div v-if="!isCheckedIn">
            <el-alert
              title="您尚未入住或已办理退宿，无法提交报修申请"
              type="warning"
              :closable="false"
              show-icon
            />
            <el-empty description="请先办理入住手续" :image-size="200">
              <el-button type="primary" @click="$router.push('/student/checkin')">申请入住</el-button>
            </el-empty>
          </div>
          
          <el-form v-else :model="formData" :rules="formRules" ref="formRef" label-width="80px">
            <el-form-item label="报修类型" prop="repairType">
              <el-select v-model="formData.repairType" placeholder="请选择报修类型" style="width: 100%">
                <el-option label="水电维修" :value="1" />
                <el-option label="家具维修" :value="2" />
                <el-option label="网络故障" :value="3" />
                <el-option label="其他" :value="4" />
              </el-select>
            </el-form-item>
            <el-form-item label="报修标题" prop="repairTitle">
              <el-input v-model="formData.repairTitle" placeholder="请输入报修标题，如：水龙头漏水" />
            </el-form-item>
            <el-form-item label="报修描述" prop="repairContent">
              <el-input v-model="formData.repairContent" type="textarea" :rows="5" placeholder="请详细描述报修问题" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit" :loading="submitLoading">提交</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 报修历史 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>我的报修历史</span>
          </template>
          <el-table :data="tableData" border stripe v-loading="loading">
            <el-table-column prop="repairType" label="报修类型" width="120">
              <template #default="{ row }">
                <span v-if="row.repairType === 1">水电维修</span>
                <span v-else-if="row.repairType === 2">家具维修</span>
                <span v-else-if="row.repairType === 3">网络故障</span>
                <span v-else>其他</span>
              </template>
            </el-table-column>
            <el-table-column prop="repairTitle" label="标题" show-overflow-tooltip width="150" />
            <el-table-column prop="repairContent" label="描述" show-overflow-tooltip />
            <el-table-column prop="repairStatus" label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.repairStatus === 1">待接单</el-tag>
                <el-tag v-else-if="row.repairStatus === 2" type="warning">已接单</el-tag>
                <el-tag v-else-if="row.repairStatus === 3" type="">处理中</el-tag>
                <el-tag v-else-if="row.repairStatus === 4" type="success">已完成</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button v-if="row.repairStatus === 4 && !row.satisfactionRating" type="primary" size="small" @click="handleRate(row)">评价</el-button>
                <el-tag v-else-if="row.repairStatus === 4 && row.satisfactionRating" type="success">已评价</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            layout="total, prev, pager, next"
            @current-change="loadData"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 评价对话框 -->
    <el-dialog v-model="rateDialogVisible" title="服务评价" width="500px">
      <el-form :model="rateForm" label-width="80px">
        <el-form-item label="满意度">
          <el-rate v-model="rateForm.rating" :texts="['很差', '差', '一般', '满意', '非常满意']" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="rateForm.comment" type="textarea" :rows="4" placeholder="请输入您的评价意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { submitRepair, getRepairPage, rateRepair } from '@/api/repair'
import { getCheckInPage } from '@/api/checkin'
import { useUserStore } from '@/stores/user'

const formRef = ref(null)
const submitLoading = ref(false)
const loading = ref(false)
const tableData = ref([])
const userStore = useUserStore()
const rateDialogVisible = ref(false)
const currentRoomInfo = ref(null)
const checkingRoomStatus = ref(false)

const rateForm = reactive({
  id: null,
  rating: 5,
  comment: ''
})

const formData = reactive({
  repairType: '',
  repairTitle: '',
  repairContent: ''
})

const formRules = {
  repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  repairTitle: [{ required: true, message: '请输入报修标题', trigger: 'blur' }],
  repairContent: [{ required: true, message: '请输入报修描述', trigger: 'blur' }]
}

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 计算学生是否已入住
const isCheckedIn = computed(() => {
  return currentRoomInfo.value !== null;
})

// 获取学生的入住状态
const checkRoomStatus = async () => {
  checkingRoomStatus.value = true;
  try {
    const params = {
      current: 1,
      size: 50,
      studentId: userStore.userInfo.id
    };
    const res = await getCheckInPage(params);
    const records = res.data.records || [];
    
    // 查找最新的已确认入住的申请（状态为4）
    const checkedInApplications = records
      .filter(record => record.status === 4)
      .sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
    
    if (checkedInApplications.length > 0) {
      currentRoomInfo.value = {
        buildingName: checkedInApplications[0].buildingName,
        roomNumber: checkedInApplications[0].roomNumber,
        bedNumber: checkedInApplications[0].bedNumber,
        applicationId: checkedInApplications[0].id
      };
    } else {
      currentRoomInfo.value = null;
    }
  } catch (error) {
    console.error('获取入住状态失败:', error);
    currentRoomInfo.value = null;
  } finally {
    checkingRoomStatus.value = false;
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...pagination, studentId: userStore.userInfo.id }
    const res = await getRepairPage(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载报修历史失败', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }
  
  submitLoading.value = true
  try {
    await submitRepair(formData)
    ElMessage.success('报修提交成功')
    formRef.value.resetFields()
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '提交失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

const handleRate = (row) => {
  rateForm.id = row.id
  rateForm.rating = 5
  rateForm.comment = ''
  rateDialogVisible.value = true
}

const submitRate = async () => {
  if (!rateForm.comment || rateForm.comment.trim() === '') {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  try {
    await rateRepair({ 
      id: rateForm.id, 
      rating: rateForm.rating, 
      comment: rateForm.comment 
    })
    ElMessage.success('评价成功')
    rateDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '评价失败')
  }
}

onMounted(() => {
  checkRoomStatus()
  loadData()
})
</script>

<style scoped>
.student-repair {
  padding: 0;
}
</style>
