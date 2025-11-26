<template>
  <div class="checkout-page">
    <el-card v-if="!currentRoom">
      <el-empty description="您还未入住宿舍，无需申请退宿" :image-size="200">
        <el-button type="primary" @click="$router.push('/student/room')">查看房间信息</el-button>
      </el-empty>
    </el-card>

    <template v-else>
      <!-- 当前房间信息 -->
      <el-card>
        <template #header>
          <span>当前房间信息</span>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="楼宇">{{ currentRoom.buildingName }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ currentRoom.roomNumber }}</el-descriptions-item>
          <el-descriptions-item label="床位号">{{ currentRoom.bedNumber }}</el-descriptions-item>
          <el-descriptions-item label="房间类型">{{ currentRoom.roomType === 1 ? '4人间' : '6人间' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 退宿申请表单 -->
      <el-card style="margin-top: 20px" v-if="!hasCheckoutApplication">
        <template #header>
          <span>退宿申请</span>
        </template>
        <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
          <el-form-item label="退宿原因" prop="reason">
            <el-select v-model="formData.reason" placeholder="请选择退宿原因" style="width: 100%">
              <el-option label="毕业离校" value="毕业离校" />
              <el-option label="退学/转学" value="退学/转学" />
              <el-option label="校外租房" value="校外租房" />
              <el-option label="其他原因" value="其他原因" />
            </el-select>
          </el-form-item>
          <el-form-item label="详细说明" prop="description">
            <el-input v-model="formData.description" type="textarea" :rows="4" 
                      placeholder="请详细说明退宿原因（必填）" maxlength="500" show-word-limit />
          </el-form-item>
          <el-form-item label="预计退宿日期" prop="checkoutDate">
            <el-date-picker v-model="formData.checkoutDate" type="date" placeholder="选择日期" 
                           :disabled-date="disabledDate" style="width: 100%" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitLoading">提交申请</el-button>
            <el-button @click="$router.push('/student/room')">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 申请记录 -->
      <el-card style="margin-top: 20px" v-if="applicationHistory.length > 0">
        <template #header>
          <span>申请记录</span>
        </template>
        <el-timeline>
          <el-timeline-item v-for="item in applicationHistory" :key="item.id"
                           :timestamp="item.createTime" placement="top">
            <el-card>
              <h4>
                <el-tag v-if="item.status === 1" type="warning">待审核</el-tag>
                <el-tag v-else-if="item.status === 2" type="success">已同意</el-tag>
                <el-tag v-else-if="item.status === 3" type="danger">已驳回</el-tag>
                <el-tag v-else type="info">已撤销</el-tag>
                退宿申请
              </h4>
              <p style="margin-top: 10px">退宿原因：{{ item.reason }}</p>
              <p>详细说明：{{ item.description }}</p>
              <p>预计退宿日期：{{ item.checkoutDate }}</p>
              <p v-if="item.remark" style="color: #F56C6C">审核意见：{{ item.remark }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getCheckInPage } from '@/api/checkin'
import { getCheckoutPage, applyCheckout } from '@/api/checkout'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const formRef = ref(null)
const submitLoading = ref(false)
const currentRoom = ref(null)
const applicationHistory = ref([])

const formData = reactive({
  reason: '',
  description: '',
  checkoutDate: ''
})

const formRules = {
  reason: [{ required: true, message: '请选择退宿原因', trigger: 'change' }],
  description: [{ required: true, message: '请填写详细说明', trigger: 'blur' }],
  checkoutDate: [{ required: true, message: '请选择预计退宿日期', trigger: 'change' }]
}

const hasCheckoutApplication = computed(() => {
  if (!currentRoom.value || !currentRoom.value.applicationId) return false;
  
  // 仅检查与当前入住记录关联的退宿申请
  return applicationHistory.value.some(item => 
    item.checkInApplicationId === currentRoom.value.applicationId && 
    (item.status === 1 || item.status === 2)
  );
})

const disabledDate = (time) => {
  // 不能选择今天之前的日期
  return time.getTime() < Date.now() - 8.64e7
}

const loadRoomInfo = async () => {
  try {
    const params = { 
      current: 1, 
      size: 50,  // 增大size以获取更多记录
      studentId: userStore.userInfo.id 
    }
    const res = await getCheckInPage(params)
    const records = res.data.records
    
    // 查找最新的已确认入住的申请（状态为4）
    // 按创建时间降序排序，然后找第一个状态为4的
    const checkedInApplications = records
      .filter(record => record.status === 4)
      .sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
    
    const checkedInApplication = checkedInApplications[0];
    
    if (checkedInApplication) {
      currentRoom.value = {
        buildingName: checkedInApplication.buildingName,
        roomNumber: checkedInApplication.roomNumber,
        bedNumber: checkedInApplication.bedNumber,
        roomType: checkedInApplication.preferredRoomType || 1,
        applicationId: checkedInApplication.id
      }
    }
    
    // 加载退宿申请记录
    try {
      const checkoutRes = await getCheckoutPage({
        current: 1,
        size: 10,
        studentId: userStore.userInfo.id
      })
      applicationHistory.value = checkoutRes.data.records || []
    } catch (error) {
      console.error('加载退宿申请记录失败', error)
    }
  } catch (error) {
    console.error('加载房间信息失败', error)
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await applyCheckout({
      checkInApplicationId: currentRoom.value.applicationId,
      reason: formData.reason,
      description: formData.description,
      checkoutDate: formData.checkoutDate
    })
    ElMessage.success('退宿申请提交成功，请等待审核')
    // 重置表单
    Object.assign(formData, {
      reason: '',
      description: '',
      checkoutDate: ''
    })
    await loadRoomInfo()
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadRoomInfo()
})
</script>

<style scoped>
.checkout-page {
  padding: 0;
}
</style>
