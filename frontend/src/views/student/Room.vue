<template>
  <div class="student-room">
    <div v-if="roomInfo">
      <el-card>
        <template #header>
          <span>我的房间信息</span>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="所属楼宇">{{ roomInfo.buildingName }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ roomInfo.roomNumber }}</el-descriptions-item>
          <el-descriptions-item label="楼层">{{ roomInfo.floor }}</el-descriptions-item>
          <el-descriptions-item label="房间类型">{{ roomInfo.roomType === 1 ? '4人间' : '6人间' }}</el-descriptions-item>
          <el-descriptions-item label="床位号">{{ roomInfo.bedNumber }}</el-descriptions-item>
          <el-descriptions-item label="床位数">{{ roomInfo.bedCount }}</el-descriptions-item>
          <el-descriptions-item label="已入住">{{ roomInfo.occupiedBeds }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card style="margin-top: 20px">
        <template #header>
          <span>我的室友</span>
        </template>
        <el-table :data="roommates" border stripe>
          <el-table-column prop="realName" label="姓名" />
          <el-table-column prop="studentNumber" label="学号" />
          <el-table-column prop="bedNumber" label="床位号" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="email" label="邮箱" />
        </el-table>
      </el-card>
    </div>

    <el-card v-else v-loading="loading">
      <el-empty description="您还未入住宿舍" :image-size="200">
        <el-button type="primary" @click="$router.push('/student/checkin')">申请入住</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCheckInPage, getRoommates } from '@/api/checkin'
import { useUserStore } from '@/stores/user'

const roomInfo = ref(null)
const roommates = ref([])
const loading = ref(false)
const userStore = useUserStore()

const loadRoomInfo = async () => {
  loading.value = true
  try {
    // 查询学生的入住申请记录
    const params = { 
      current: 1, 
      size: 50,  
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
      const totalBeds = checkedInApplication.preferredRoomType === 2 ? 6 : 4
      
      // 获取室友信息
      if (checkedInApplication.assignedRoomId) {
        try {
          const roommatesRes = await getRoommates(checkedInApplication.assignedRoomId)
          roommates.value = roommatesRes.data || []
        } catch (error) {
          console.error('获取室友信息失败', error)
        }
      }
      
      // 已入住，显示房间信息
      roomInfo.value = {
        buildingName: checkedInApplication.buildingName,
        roomNumber: checkedInApplication.roomNumber,
        bedNumber: checkedInApplication.bedNumber,
        floor: checkedInApplication.floor || '未知',
        roomType: checkedInApplication.preferredRoomType || 1,
        bedCount: totalBeds,
        occupiedBeds: roommates.value.length || 0
      }
    }
  } catch (error) {
    console.error('加载房间信息失败', error)
    ElMessage.error('加载房间信息失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRoomInfo()
})
</script>

<style scoped>
.student-room {
  padding: 0;
}
</style>
