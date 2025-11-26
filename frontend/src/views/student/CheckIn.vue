<template>
  <div class="student-checkin">
    <el-card>
      <template #header>
        <span>我的入住申请</span>
      </template>

      <!-- 申请表单 -->
      <div v-if="canSubmitNewApplication">
        <el-alert 
          :title="lastRejectedApplication ? '您的申请已被驳回，可以重新提交' : '您还未提交入住申请'" 
          :type="lastRejectedApplication ? 'warning' : 'info'" 
          :closable="false" 
          style="margin-bottom: 20px" 
        />
        <el-alert 
          v-if="lastRejectedApplication && lastRejectedApplication.approveRemark" 
          :title="`驳回原因：${lastRejectedApplication.approveRemark}`" 
          type="error" 
          :closable="false" 
          style="margin-bottom: 20px" 
        />
        <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
          <el-form-item label="期望楼宇" prop="buildingId">
            <el-select v-model="formData.buildingId" placeholder="请选择期望楼宇" style="width: 100%">
              <el-option v-for="b in buildingList" :key="b.id" :label="b.buildingName" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="期望房间类型" prop="roomType">
            <el-radio-group v-model="formData.roomType">
              <el-radio :label="1">4人间</el-radio>
              <el-radio :label="2">6人间</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="入住时间" prop="checkInDate">
            <el-date-picker
              v-model="formData.checkInDate"
              type="date"
              placeholder="选择入住日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="备注">
            <el-input 
              v-model="formData.remark" 
              type="textarea" 
              :rows="3" 
              placeholder="请填写申请理由（此信息将提交给管理员）"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitLoading">提交申请</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 申请历史 -->
      <div v-else>
        <el-alert 
          :title="currentApplicationStatusText" 
          :type="currentApplicationAlertType" 
          :closable="false" 
          style="margin-bottom: 20px" 
        />
        <el-timeline>
          <el-timeline-item
            v-for="item in applicationHistory"
            :key="item.id"
            :timestamp="item.createTime"
            :type="getTimelineType(item.status)"
          >
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <div>
                <h4>{{ getStatusText(item.status) }}</h4>
                <p v-if="item.applicationReason">申请理由：{{ item.applicationReason }}</p>
                <p v-if="item.approveRemark">审批意见：{{ item.approveRemark }}</p>
                <p v-if="item.roomNumber">分配房间：{{ item.buildingName }} - {{ item.roomNumber }} - {{ item.bedNumber }}</p>
              </div>
              <el-button 
                v-if="item.status === 1" 
                type="danger" 
                size="small" 
                @click="handleCancel(item.id)"
              >
                撤销申请
              </el-button>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuildingList } from '@/api/building'
import { submitCheckIn, getCheckInPage, cancelCheckIn } from '@/api/checkin'
import { useUserStore } from '@/stores/user'

const formRef = ref(null)
const submitLoading = ref(false)
const buildingList = ref([])
const applicationHistory = ref([])
const userStore = useUserStore()

const formData = reactive({
  buildingId: '',
  roomType: 1,
  checkInDate: '',
  remark: ''
})

const formRules = {
  buildingId: [{ required: true, message: '请选择期望楼宇', trigger: 'change' }],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }]
}

// 计算属性：是否可以提交新申请
const canSubmitNewApplication = computed(() => {
  if (!applicationHistory.value || applicationHistory.value.length === 0) {
    return true // 没有申请记录，可以提交
  }
  
  // 获取最新的申请
  const latestApplication = applicationHistory.value[0]
  
  // 只有当最新申请是"审批驳回"或"已退宿"时，才允许提交新申请
  return latestApplication.status === 3 || latestApplication.status === 5
})

// 计算属性：最后一次被驳回的申请
const lastRejectedApplication = computed(() => {
  if (!applicationHistory.value || applicationHistory.value.length === 0) {
    return null
  }
  const latestApplication = applicationHistory.value[0]
  return latestApplication.status === 3 ? latestApplication : null
})

// 计算属性：当前申请状态文本
const currentApplicationStatusText = computed(() => {
  if (!applicationHistory.value || applicationHistory.value.length === 0) {
    return ''
  }
  const latestApplication = applicationHistory.value[0]
  switch (latestApplication.status) {
    case 1: return '您已提交入住申请，请等待审批'
    case 2: return '您的申请已通过审批'
    case 4: return '您已确认入住'
    case 5: return '您已退宿，可以重新提交入住申请'
    default: return ''
  }
})

// 计算属性：提示框类型
const currentApplicationAlertType = computed(() => {
  if (!applicationHistory.value || applicationHistory.value.length === 0) {
    return 'info'
  }
  const latestApplication = applicationHistory.value[0]
  switch (latestApplication.status) {
    case 1: return 'info'
    case 2: return 'success'
    case 4: return 'success'
    case 5: return 'warning'
    default: return 'info'
  }
})

const loadBuildingList = async () => {
  try {
    const res = await getBuildingList()
    buildingList.value = res.data
  } catch (error) {
    ElMessage.error('加载楼宇列表失败')
  }
}

const loadApplicationHistory = async () => {
  try {
    const params = { current: 1, size: 10, studentId: userStore.userInfo.id }
    const res = await getCheckInPage(params)
    applicationHistory.value = res.data.records
  } catch (error) {
    console.error('加载申请历史失败', error)
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    // 构建提交数据，字段名需要匹配后端实体类
    const submitData = {
      preferredBuildingId: formData.buildingId,
      preferredRoomType: formData.roomType,
      applicationReason: formData.remark
    }
    await submitCheckIn(submitData)
    ElMessage.success('申请提交成功')
    loadApplicationHistory()
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 1: return '待审批'
    case 2: return '审批通过'
    case 3: return '审批驳回'
    case 4: return '已确认入住'
    case 5: return '已退宿'
    default: return '未知状态'
  }
}

const getTimelineType = (status) => {
  switch (status) {
    case 1: return 'primary'
    case 2: return 'success'
    case 3: return 'danger'
    case 4: return 'info'
    case 5: return 'warning'
    default: return ''
  }
}

const handleCancel = (id) => {
  ElMessageBox.confirm('确定要撤销该入住申请吗？撤销后可以重新提交。', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await cancelCheckIn(id)
      ElMessage.success('撤销成功')
      applicationHistory.value = []
      loadApplicationHistory()
    } catch (error) {
      ElMessage.error(error.message || '撤销失败')
    }
  })
}

onMounted(() => {
  loadBuildingList()
  loadApplicationHistory()
})
</script>

<style scoped>
.student-checkin {
  padding: 0;
}
</style>
