<template>
  <div class="transfer-page">
    <el-card v-if="!currentRoom">
      <el-empty description="您还未入住宿舍，无需申请换宿" :image-size="200">
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

      <!-- 换宿申请表单 -->
      <el-card style="margin-top: 20px" v-if="!hasTransferApplication">
        <template #header>
          <span>换宿申请</span>
        </template>
        <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
          <el-form-item label="换宿原因" prop="reason">
            <el-select v-model="formData.reason" placeholder="请选择换宿原因" style="width: 100%">
              <el-option label="室友关系不和" value="室友关系不和" />
              <el-option label="房间设施问题" value="房间设施问题" />
              <el-option label="靠近学习区域" value="靠近学习区域" />
              <el-option label="健康原因" value="健康原因" />
              <el-option label="其他原因" value="其他原因" />
            </el-select>
          </el-form-item>
          <el-form-item label="详细说明" prop="description">
            <el-input v-model="formData.description" type="textarea" :rows="4" 
                      placeholder="请详细说明换宿原因（必填）" maxlength="500" show-word-limit />
          </el-form-item>
          <el-form-item label="目标楼宇" prop="targetBuildingId">
            <el-select v-model="formData.targetBuildingId" placeholder="请选择楼宇" 
                      style="width: 100%" @change="handleBuildingChange">
              <el-option v-for="b in buildingList" :key="b.id" 
                        :label="b.buildingName" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="目标房间类型" prop="targetRoomType">
            <el-radio-group v-model="formData.targetRoomType" @change="loadAvailableRooms">
              <el-radio :label="1">4人间</el-radio>
              <el-radio :label="2">6人间</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="目标房间" prop="targetRoomId">
            <el-select v-model="formData.targetRoomId" placeholder="请选择目标房间" 
                      style="width: 100%" :loading="roomLoading">
              <el-option v-for="r in availableRooms" :key="r.id" 
                        :label="`${r.roomNumber} (剩余${r.availableBeds}个床位)`" :value="r.id" />
            </el-select>
            <div v-if="formData.targetBuildingId && formData.targetRoomType && availableRooms.length === 0" 
                 style="color: #E6A23C; margin-top: 5px">
              该楼宇暂无符合条件的空余房间
            </div>
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
                <el-tag v-else-if="item.status === 4" type="info">已撤销</el-tag>
                <el-tag v-else-if="item.status === 5" type="success">已完成</el-tag>
                换宿申请
              </h4>
              <p style="margin-top: 10px">换宿原因：{{ item.reason }}</p>
              <p>详细说明：{{ item.description }}</p>
              <p>目标房间：{{ item.targetBuildingName || '-' }} - {{ item.targetRoomNumber || '-' }}</p>
              <p v-if="item.remark" style="color: #F56C6C">审核意见：{{ item.remark }}</p>
              <p v-if="item.status === 2 || item.status === 5">
                分配床位：{{ item.assignedBedNumber || '-' }}
              </p>
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
import { getBuildingList } from '@/api/building'
import { getAvailableRooms } from '@/api/room'
import { getTransferPage, applyTransfer } from '@/api/transfer'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const formRef = ref(null)
const submitLoading = ref(false)
const roomLoading = ref(false)
const currentRoom = ref(null)
const buildingList = ref([])
const availableRooms = ref([])
const applicationHistory = ref([])

const formData = reactive({
  reason: '',
  description: '',
  targetBuildingId: '',
  targetRoomType: 1,
  targetRoomId: ''
})

const formRules = {
  reason: [{ required: true, message: '请选择换宿原因', trigger: 'change' }],
  description: [{ required: true, message: '请填写详细说明', trigger: 'blur' }],
  targetBuildingId: [{ required: true, message: '请选择目标楼宇', trigger: 'change' }],
  targetRoomType: [{ required: true, message: '请选择目标房间类型', trigger: 'change' }],
  targetRoomId: [{ required: true, message: '请选择目标房间', trigger: 'change' }]
}

const hasTransferApplication = computed(() => {
  return applicationHistory.value.some(item => item.status === 1 || item.status === 2)
})

const loadBuildingList = async () => {
  try {
    const res = await getBuildingList()
    buildingList.value = res.data
  } catch (error) {
    console.error('加载楼宇列表失败', error)
  }
}

const handleBuildingChange = () => {
  formData.targetRoomId = ''
  loadAvailableRooms()
}

const loadAvailableRooms = async () => {
  if (!formData.targetBuildingId || !formData.targetRoomType) return
  
  roomLoading.value = true
  try {
    const res = await getAvailableRooms(formData.targetBuildingId, formData.targetRoomType)
    availableRooms.value = res.data
  } catch (error) {
    console.error('加载可用房间失败', error)
    availableRooms.value = []
  } finally {
    roomLoading.value = false
  }
}

const loadRoomInfo = async () => {
  try {
    const params = { 
      current: 1, 
      size: 10, 
      studentId: userStore.userInfo.id 
    }
    const res = await getCheckInPage(params)
    const records = res.data.records
    
    // 查找已确认入住的申请（状态为4）
    const checkedInApplication = records.find(record => record.status === 4)
    
    if (checkedInApplication) {
      currentRoom.value = {
        buildingName: checkedInApplication.buildingName,
        roomNumber: checkedInApplication.roomNumber,
        bedNumber: checkedInApplication.bedNumber,
        roomType: checkedInApplication.preferredRoomType || 1,
        applicationId: checkedInApplication.id
      }
    }
    
    // 加载换宿申请记录
    try {
      const transferRes = await getTransferPage({
        current: 1,
        size: 10,
        studentId: userStore.userInfo.id
      })
      applicationHistory.value = transferRes.data.records || []
    } catch (error) {
      console.error('加载换宿申请记录失败', error)
    }
  } catch (error) {
    console.error('加载房间信息失败', error)
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await applyTransfer({
      checkInApplicationId: currentRoom.value.applicationId,
      targetBuildingId: formData.targetBuildingId,
      targetRoomId: formData.targetRoomId,
      targetRoomType: formData.targetRoomType,
      reason: formData.reason,
      description: formData.description
    })
    ElMessage.success('换宿申请提交成功，请等待审核')
    // 重置表单
    Object.assign(formData, {
      reason: '',
      description: '',
      targetBuildingId: '',
      targetRoomType: 1,
      targetRoomId: ''
    })
    availableRooms.value = []
    await loadRoomInfo()
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadRoomInfo()
  loadBuildingList()
})
</script>

<style scoped>
.transfer-page {
  padding: 0;
}
</style>
