<template>
  <div class="rooms-page">
    <section class="page-header">
      <div class="container">
        <h1>房源信息</h1>
        <p>查找并了解我们的公寓楼栋和房间信息</p>
      </div>
    </section>
    
    <section class="buildings-section">
      <div class="container">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="楼栋信息" name="buildings">
            <el-row :gutter="24">
              <el-col :xs="24" :sm="12" :md="8" v-for="building in filteredBuildings" :key="building.id">
                <el-card class="building-card" shadow="hover">
                  <div class="building-image" :style="{ backgroundImage: `url(${building.imageUrl || getRandomBuildingImage()})` }">
                    <div class="building-badge" :class="getBuildingStatusClass(building)">
                      {{ getBuildingStatusText(building) }}
                    </div>
                  </div>
                  <div class="building-info">
                    <h3>{{ building.buildingName }}</h3>
                    <p class="building-description">{{ building.description || '舒适便捷的学生公寓，配套设施齐全' }}</p>
                    <div class="building-details">
                      <div class="detail-item">
                        <div class="detail-label">位置</div>
                        <div class="detail-value">{{ building.address || '校区内' }}</div>
                      </div>
                      <div class="detail-item">
                        <div class="detail-label">可用房间</div>
                        <div class="detail-value">{{ building.totalRooms || '--' }}间</div>
                      </div>
                      <div class="detail-item">
                        <div class="detail-label">楼层</div>
                        <div class="detail-value">{{ building.floors || '--' }}层</div>
                      </div>
                    </div>
                    <div class="card-actions">
                      <el-button type="primary" plain @click="viewRooms(building.id)">查看房间</el-button>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <el-empty v-if="filteredBuildings.length === 0" description="暂无符合条件的楼栋"></el-empty>
          </el-tab-pane>
          
          <el-tab-pane label="房间列表" name="rooms">
            <!-- 房间列表筛选条件 -->
            <div class="filter-section">
              <el-form :model="filterForm" inline class="filter-form">
                <el-form-item label="楼栋">
                  <el-select 
                    v-model="filterForm.buildingId" 
                    placeholder="选择楼栋" 
                    clearable
                    style="width: 180px;"
                  >
                    <el-option 
                      v-for="building in buildings" 
                      :key="building.id" 
                      :label="building.buildingName" 
                      :value="building.id" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="房间类型">
                  <el-select 
                    v-model="filterForm.roomType" 
                    placeholder="选择类型" 
                    clearable
                    style="width: 180px;"
                  >
                    <el-option label="单人间" value="1" />
                    <el-option label="双人间" value="2" />
                    <el-option label="四人间" value="4" />
                    <el-option label="六人间" value="6" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="楼层">
                  <el-select 
                    v-model="filterForm.floorNumber" 
                    placeholder="选择楼层" 
                    clearable
                    style="width: 180px;"
                  >
                    <el-option label="1层" value="1" />
                    <el-option label="2层" value="2" />
                    <el-option label="3层" value="3" />
                    <el-option label="4层" value="4" />
                    <el-option label="5层" value="5" />
                    <el-option label="6层" value="6" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="床位状态">
                  <el-select 
                    v-model="filterForm.bedStatus" 
                    placeholder="床位状态" 
                    clearable
                    style="width: 180px;"
                  >
                    <el-option label="有空床位" value="available" />
                    <el-option label="无空床位" value="full" />
                  </el-select>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="handleFilter">筛选</el-button>
                  <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <div class="rooms-list">
              <el-table 
                :data="rooms" 
                style="width: 100%" 
                v-loading="loading">
                <el-table-column prop="buildingName" label="楼栋" min-width="120" />
                <el-table-column prop="roomNumber" label="房间号" min-width="100" />
                <el-table-column prop="roomTypeName" label="房型" min-width="100" />
                <el-table-column prop="floorNumber" label="楼层" min-width="80" />
                <el-table-column prop="bedsTotal" label="床位数" min-width="80" />
                <el-table-column prop="bedsAvailable" label="可用床位" min-width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.bedsAvailable > 0 ? 'success' : 'info'">
                      {{ scope.row.bedsAvailable }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column fixed="right" label="操作" width="120">
                  <template #default="scope">
                    <el-button 
                      link 
                      type="primary" 
                      size="small"
                      @click="viewRoomDetail(scope.row)"
                    >
                      详情
                    </el-button>
                    <el-button 
                      link 
                      type="primary" 
                      size="small"
                      :disabled="!isStudent || scope.row.bedsAvailable === 0"
                      @click="applyRoom(scope.row)"
                    >
                      申请
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              
              <el-pagination
                v-if="rooms.length > 0"
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                background
                layout="total, sizes, prev, pager, next"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                class="pagination"
              />
              
              <el-empty v-if="rooms.length === 0 && !loading" description="暂无房间数据"></el-empty>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </section>
    
    <!-- 房间详情对话框 -->
    <el-dialog
      v-model="roomDialogVisible"
      title="房间详情"
      width="600px">
      <div class="room-detail" v-if="selectedRoom">
        <div class="room-header">
          <h2>{{ selectedRoom.buildingName }} - {{ selectedRoom.roomNumber }}</h2>
          <el-tag :type="selectedRoom.bedsAvailable > 0 ? 'success' : 'info'">
            {{ selectedRoom.bedsAvailable > 0 ? '可申请' : '已满' }}
          </el-tag>
        </div>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="房间类型">{{ selectedRoom.roomTypeName }}</el-descriptions-item>
          <el-descriptions-item label="楼层">{{ selectedRoom.floorNumber }}层</el-descriptions-item>
          <el-descriptions-item label="总床位">{{ selectedRoom.bedsTotal }}个</el-descriptions-item>
          <el-descriptions-item label="可用床位">{{ selectedRoom.bedsAvailable }}个</el-descriptions-item>
          <el-descriptions-item label="面积">{{ selectedRoom.area || '--' }} 平米</el-descriptions-item>
          <el-descriptions-item label="朝向">{{ selectedRoom.orientation || '--' }}</el-descriptions-item>
          <el-descriptions-item label="设施" :span="2">
            <el-tag size="small" v-if="selectedRoom.hasAirCon">空调</el-tag>
            <el-tag size="small" v-if="selectedRoom.hasHeating">暖气</el-tag>
            <el-tag size="small" v-if="selectedRoom.hasBalcony">阳台</el-tag>
            <el-tag size="small" v-if="selectedRoom.hasPrivateBath">独卫</el-tag>
            <el-tag size="small" v-if="true">公共卫浴</el-tag>
            <el-tag size="small" v-if="true">热水器</el-tag>
            <el-tag size="small" v-if="true">书桌</el-tag>
            <el-tag size="small" v-if="true">衣柜</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ selectedRoom.remarks || '暂无备注信息' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roomDialogVisible = false">关闭</el-button>
          <el-button
            type="primary"
            :disabled="!isStudent || !selectedRoom || selectedRoom.bedsAvailable === 0"
            @click="applyRoom(selectedRoom)"
          >
            申请入住
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBuildingList } from '@/api/building'
import { getAvailableRooms } from '@/api/room'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 判断用户是否为学生
const isStudent = computed(() => {
  return !!userStore.token && userStore.userInfo?.userType === 3
})

// 标签页控制
const activeTab = ref('buildings')

// 楼栋和房间数据
const buildings = ref([])
const rooms = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 筛选表单
const filterForm = reactive({
  buildingId: route.query.buildingId || '',
  roomType: '',
  floorNumber: '',
  bedStatus: ''
})

// 房间详情
const selectedRoom = ref(null)
const roomDialogVisible = ref(false)

// 随机楼栋图片
const buildingImages = [
  'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600',
  'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600',
  'https://images.unsplash.com/photo-1556742533-2b2a6ee50906?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600'
]

const getRandomBuildingImage = () => {
  return buildingImages[Math.floor(Math.random() * buildingImages.length)]
}

// 过滤后的楼栋列表 - 不再通过筛选表单过滤楼栋
const filteredBuildings = computed(() => {
  // 所有楼栋都显示，不再过滤
  return buildings.value
})

// 获取楼栋状态类
const getBuildingStatusClass = (building) => {
  // 可以根据实际字段调整逻辑
  if (building.hasOwnProperty('roomCount')) {
    // 兼容旧数据
    const count = building.roomCount || 0
    if (count > 20) return 'available'
    if (count > 0) return 'limited'
    return 'full'
  } else {
    // 根据新字段计算
    const totalRooms = building.totalRooms || 0
    if (totalRooms > 20) return 'available'
    if (totalRooms > 0) return 'limited'
    return 'full'
  }
}

// 获取楼栋状态文本
const getBuildingStatusText = (building) => {
  // 可以根据实际字段调整逻辑
  if (building.hasOwnProperty('roomCount')) {
    // 兼容旧数据
    const count = building.roomCount || 0
    if (count > 20) return '充足'
    if (count > 0) return '紧张'
    return '已满'
  } else {
    // 根据新字段计算
    const totalRooms = building.totalRooms || 0
    if (totalRooms > 20) return '充足'
    if (totalRooms > 0) return '紧张'
    return '已满'
  }
}

// 处理标签页切换
const handleTabChange = (tab) => {
  if (tab === 'rooms') {
    loadRooms()
  }
}

// 筛选处理
const handleFilter = () => {
  // 只对房间列表进行筛选
  currentPage.value = 1
  loadRooms()
}

// 重置筛选
const resetFilter = () => {
  filterForm.buildingId = ''
  filterForm.roomType = ''
  filterForm.floorNumber = ''
  filterForm.bedStatus = ''
  
  if (activeTab.value === 'rooms') {
    loadRooms()
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadRooms()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadRooms()
}

// 查看楼栋下的房间
const viewRooms = (buildingId) => {
  filterForm.buildingId = buildingId
  activeTab.value = 'rooms'
  loadRooms()
}

// 查看房间详情
const viewRoomDetail = (room) => {
  selectedRoom.value = room
  roomDialogVisible.value = true
}

// 申请房间
const applyRoom = (room) => {
  if (!isStudent.value) {
    ElMessage.warning('请先登录学生账号')
    router.push('/student/login')
    return
  }
  
  // 跳转到入住申请页面，并传递房间信息
  router.push({
    path: '/student/checkin',
    query: { 
      buildingId: room.buildingId,
      roomId: room.id
    }
  })
}

// 加载楼栋数据
const loadBuildings = async () => {
  try {
    const res = await getBuildingList()
    
    // 确保有数据并处理图片
    if (res.data && res.data.length > 0) {
      buildings.value = res.data.map(building => {
        return {
          ...building,
          imageUrl: building.imageUrl || getRandomBuildingImage()
        }
      })
    } else {
      buildings.value = []
      ElMessage.warning('暂无楼栋数据，请联系管理员添加楼栋信息')
    }
  } catch (error) {
    console.error('获取楼宇列表失败', error)
    ElMessage.error('获取楼宇数据失败：' + (error.message || '未知错误'))
    buildings.value = []
  }
}

// 加载房间数据
const loadRooms = async () => {
  loading.value = true
  rooms.value = []
  
  try {
    // 准备发送到后端的所有筛选参数
    const params = {
      buildingId: filterForm.buildingId ? parseInt(filterForm.buildingId) : undefined,
      roomType: filterForm.roomType ? parseInt(filterForm.roomType) : undefined,
      floorNumber: filterForm.floorNumber ? parseInt(filterForm.floorNumber) : undefined,
      bedStatus: filterForm.bedStatus || undefined
    }
    
    console.log('【前端】发送筛选条件:', JSON.stringify(params))
    
    // 调用API - 将所有筛选条件传递给后端
    const res = await getAvailableRooms(
      params.buildingId, 
      params.roomType, 
      params.floorNumber, 
      params.bedStatus
    )
    console.log('【前端】API返回结果条数:', res.data ? res.data.length : 0)
    
    if (res && res.data && res.data.length > 0) {
      // 使用后端筛选后的数据
      rooms.value = res.data.map(room => {
        // 计算房型名称，优先使用bedsTotal
        let roomTypeName = '未知房型';
        const bedsTotal = room.totalBeds || 0;
        
        if (bedsTotal === 1) roomTypeName = '单人间';
        else if (bedsTotal === 2) roomTypeName = '双人间';
        else if (bedsTotal === 4) roomTypeName = '四人间';
        else if (bedsTotal === 6) roomTypeName = '六人间';
        else if (bedsTotal > 0) roomTypeName = `${bedsTotal}人间`;
        
        // 转换属性名以匹配前端展示需要
        return {
          id: room.id,
          buildingId: room.buildingId,
          buildingName: room.buildingName || '未知楼栋',
          roomNumber: room.roomNumber,
          floorNumber: room.floor, // 后端字段是floor
          roomTypeName: roomTypeName, // 直接使用计算好的房型名称
          roomType: room.roomType,
          bedsTotal: bedsTotal,
          bedsAvailable: room.availableBeds || 0, // 后端字段是availableBeds
          hasAirCon: room.facilities && room.facilities.includes('空调'),
          hasHeating: room.facilities && room.facilities.includes('暖气'),
          hasBalcony: room.facilities && room.facilities.includes('阳台'),
          hasPrivateBath: room.facilities && room.facilities.includes('独卫'),
          area: room.area,
          orientation: room.orientation || '未知',
          remarks: room.description || '' // 后端字段是description
        }
      })
      
      total.value = rooms.value.length
    } else {
      // 如果没有数据，显示空结果
      rooms.value = []
      total.value = 0
      ElMessage.info('未找到符合条件的房间')
    }
  } catch (error) {
    console.error('获取房间列表失败', error)
    ElMessage.error('获取房间数据失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 根据房型ID或床位数获取房型名称
const getRoomTypeName = (roomType, bedsTotal) => {
  if (roomType === 1) return '单人间'
  if (roomType === 2) return '双人间'
  if (roomType === 4) return '四人间'
  if (roomType === 6) return '六人间'
  return `${bedsTotal}人间`
}

onMounted(() => {
  // 加载楼栋数据
  loadBuildings()
  
  // 如果URL中有buildingId参数，自动切换到房间标签页
  if (route.query.buildingId) {
    filterForm.buildingId = route.query.buildingId
    activeTab.value = 'rooms'
    loadRooms()
  }
})
</script>

<style scoped>
/* 页面头部 */
.page-header {
  background-color: #409EFF;
  color: white;
  padding: 40px 0;
  text-align: center;
}

.page-header h1 {
  font-size: 36px;
  margin: 0 0 12px;
}

.page-header p {
  font-size: 16px;
  opacity: 0.8;
  max-width: 600px;
  margin: 0 auto;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 筛选区域 */
.filter-section {
  background-color: #f9fafb;
  padding: 16px;
  margin-bottom: 20px;
  border-radius: 8px;
  border: 1px solid #eaeaea;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 0;
}

.filter-form .el-form-item {
  margin-right: 20px;
  margin-bottom: 12px;
}

.rooms-list {
  margin-top: 16px;
}

/* 楼栋卡片 */
.buildings-section {
  padding: 30px 0 60px;
  background-color: #f9fafb;
}

.building-card {
  margin-bottom: 24px;
  height: 100%;
  border: none;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.building-card:hover {
  transform: translateY(-5px);
}

.building-image {
  height: 200px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.building-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  color: white;
}

.building-badge.available {
  background-color: #67C23A;
}

.building-badge.limited {
  background-color: #E6A23C;
}

.building-badge.full {
  background-color: #909399;
}

.building-info {
  padding: 20px;
}

.building-info h3 {
  margin: 0 0 10px;
  font-size: 20px;
}

.building-description {
  color: #606266;
  margin-bottom: 16px;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.building-details {
  margin-bottom: 20px;
  border-top: 1px solid #EBEEF5;
  padding-top: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-label {
  color: #909399;
}

.detail-value {
  font-weight: 500;
  color: #303133;
}

.card-actions {
  text-align: center;
}

/* 房间列表 */
.pagination {
  margin-top: 24px;
  text-align: center;
}

/* 房间详情 */
.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.room-header h2 {
  margin: 0;
  font-size: 22px;
}

@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }
  
  .page-header h1 {
    font-size: 28px;
  }
  
  .el-form-item {
    margin-bottom: 15px;
  }
}
</style>
