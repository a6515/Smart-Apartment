<template>
  <div class="rooms-page">
    <section class="page-header">
      <div class="container">
        <h1>房源信息</h1>
        <p>查找并了解我们的公寓楼栋和房间信息</p>
      </div>
    </section>

    <section class="filter-section">
      <div class="container">
        <el-form :model="filterForm" inline>
          <el-form-item label="楼栋">
            <el-select v-model="filterForm.buildingId" placeholder="选择楼栋" clearable>
              <el-option 
                v-for="building in buildings" 
                :key="building.id" 
                :label="building.name" 
                :value="building.id" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="房间类型">
            <el-select v-model="filterForm.roomType" placeholder="选择类型" clearable>
              <el-option label="单人间" value="1" />
              <el-option label="双人间" value="2" />
              <el-option label="四人间" value="4" />
              <el-option label="六人间" value="6" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleFilter">筛选</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
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
                    <h3>{{ building.name }}</h3>
                    <p class="building-description">{{ building.description || '舒适便捷的学生公寓，配套设施齐全' }}</p>
                    <div class="building-details">
                      <div class="detail-item">
                        <div class="detail-label">位置</div>
                        <div class="detail-value">{{ building.location || '校区内' }}</div>
                      </div>
                      <div class="detail-item">
                        <div class="detail-label">可用房间</div>
                        <div class="detail-value">{{ building.roomCount || '--' }}间</div>
                      </div>
                      <div class="detail-item">
                        <div class="detail-label">房型</div>
                        <div class="detail-value">{{ building.roomTypes || '多种房型' }}</div>
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
                <el-table-column label="设施" min-width="150">
                  <template #default="scope">
                    <el-tag size="small" v-if="scope.row.hasAirCon">空调</el-tag>
                    <el-tag size="small" v-if="scope.row.hasHeating">暖气</el-tag>
                    <el-tag size="small" v-if="scope.row.hasBalcony">阳台</el-tag>
                    <el-tag size="small" v-if="scope.row.hasPrivateBath">独卫</el-tag>
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
  roomType: ''
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

// 过滤后的楼栋列表
const filteredBuildings = computed(() => {
  let result = [...buildings.value]
  
  if (filterForm.buildingId) {
    result = result.filter(b => b.id == filterForm.buildingId)
  }
  
  return result
})

// 获取楼栋状态类
const getBuildingStatusClass = (building) => {
  const count = building.roomCount || 0
  if (count > 20) return 'available'
  if (count > 0) return 'limited'
  return 'full'
}

// 获取楼栋状态文本
const getBuildingStatusText = (building) => {
  const count = building.roomCount || 0
  if (count > 20) return '充足'
  if (count > 0) return '紧张'
  return '已满'
}

// 处理标签页切换
const handleTabChange = (tab) => {
  if (tab === 'rooms') {
    loadRooms()
  }
}

// 筛选处理
const handleFilter = () => {
  if (activeTab.value === 'buildings') {
    // 楼栋筛选已通过计算属性实现
  } else {
    // 房间筛选需要重新加载数据
    currentPage.value = 1
    loadRooms()
  }
}

// 重置筛选
const resetFilter = () => {
  filterForm.buildingId = ''
  filterForm.roomType = ''
  
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
    buildings.value = res.data || []
    
    // 如果没有数据或数据不足，添加一些示例数据
    if (buildings.value.length < 3) {
      const demoBuildings = [
        {
          id: 101,
          name: '明德公寓',
          description: '位于校园东区，环境优美，配套设施完善',
          roomCount: 120,
          location: '校园东区',
          roomTypes: '单人间、双人间',
          imageUrl: getRandomBuildingImage()
        },
        {
          id: 102,
          name: '博雅公寓',
          description: '紧邻图书馆，学习氛围浓厚，安静舒适',
          roomCount: 8,
          location: '校园北区',
          roomTypes: '四人间、六人间',
          imageUrl: getRandomBuildingImage()
        },
        {
          id: 103,
          name: '致远公寓',
          description: '靠近体育场，活动便利，配有共享空间',
          roomCount: 56,
          location: '校园西区',
          roomTypes: '双人间、四人间',
          imageUrl: getRandomBuildingImage()
        },
        {
          id: 104,
          name: '和悦公寓',
          description: '地理位置优越，环境幽静，适合学习',
          roomCount: 0,
          location: '校园南区',
          roomTypes: '单人间',
          imageUrl: getRandomBuildingImage()
        }
      ]
      
      buildings.value = [...buildings.value, ...demoBuildings.slice(0, 4 - buildings.value.length)]
    }
  } catch (error) {
    console.error('获取楼宇列表失败', error)
    ElMessage.error('获取楼宇数据失败')
    // 使用示例数据
    buildings.value = [
      {
        id: 101,
        name: '明德公寓',
        description: '位于校园东区，环境优美，配套设施完善',
        roomCount: 120,
        location: '校园东区',
        roomTypes: '单人间、双人间',
        imageUrl: getRandomBuildingImage()
      },
      {
        id: 102,
        name: '博雅公寓',
        description: '紧邻图书馆，学习氛围浓厚，安静舒适',
        roomCount: 8,
        location: '校园北区',
        roomTypes: '四人间、六人间',
        imageUrl: getRandomBuildingImage()
      },
      {
        id: 103,
        name: '致远公寓',
        description: '靠近体育场，活动便利，配有共享空间',
        roomCount: 56,
        location: '校园西区',
        roomTypes: '双人间、四人间',
        imageUrl: getRandomBuildingImage()
      },
      {
        id: 104,
        name: '和悦公寓',
        description: '地理位置优越，环境幽静，适合学习',
        roomCount: 0,
        location: '校园南区',
        roomTypes: '单人间',
        imageUrl: getRandomBuildingImage()
      }
    ]
  }
}

// 加载房间数据
const loadRooms = async () => {
  loading.value = true
  rooms.value = []
  
  try {
    // 组装API参数
    const params = {
      buildingId: filterForm.buildingId || undefined,
      roomType: filterForm.roomType || undefined
    }
    
    // 调用API
    const res = await getAvailableRooms(params.buildingId, params.roomType)
    const apiRooms = res.data || []
    
    // 转换数据格式
    rooms.value = apiRooms.map(room => {
      const building = buildings.value.find(b => b.id === room.buildingId) || {}
      return {
        id: room.id,
        buildingId: room.buildingId,
        buildingName: building.name || '未知楼栋',
        roomNumber: room.roomNumber,
        floorNumber: room.floorNumber,
        roomTypeName: getRoomTypeName(room.roomType),
        roomType: room.roomType,
        bedsTotal: room.bedsTotal || 0,
        bedsAvailable: room.bedsAvailable || 0,
        hasAirCon: room.hasAirCon,
        hasHeating: room.hasHeating,
        hasBalcony: room.hasBalcony,
        hasPrivateBath: room.hasPrivateBath,
        area: room.area,
        orientation: room.orientation,
        remarks: room.remarks
      }
    })
    
    // 设置总数
    total.value = rooms.value.length
    
    // 如果没有数据，使用示例数据
    if (rooms.value.length === 0) {
      // 示例数据
      const demoRooms = [
        {
          id: 1001,
          buildingId: 101,
          buildingName: '明德公寓',
          roomNumber: '101',
          floorNumber: 1,
          roomTypeName: '单人间',
          roomType: 1,
          bedsTotal: 1,
          bedsAvailable: 1,
          hasAirCon: true,
          hasHeating: true,
          hasBalcony: false,
          hasPrivateBath: true,
          area: 12,
          orientation: '南',
          remarks: '安静舒适，设施齐全'
        },
        {
          id: 1002,
          buildingId: 101,
          buildingName: '明德公寓',
          roomNumber: '102',
          floorNumber: 1,
          roomTypeName: '双人间',
          roomType: 2,
          bedsTotal: 2,
          bedsAvailable: 0,
          hasAirCon: true,
          hasHeating: true,
          hasBalcony: true,
          hasPrivateBath: false,
          area: 20,
          orientation: '南',
          remarks: '采光良好，空间宽敞'
        },
        {
          id: 1003,
          buildingId: 102,
          buildingName: '博雅公寓',
          roomNumber: '201',
          floorNumber: 2,
          roomTypeName: '四人间',
          roomType: 4,
          bedsTotal: 4,
          bedsAvailable: 2,
          hasAirCon: true,
          hasHeating: true,
          hasBalcony: true,
          hasPrivateBath: false,
          area: 30,
          orientation: '东南',
          remarks: '配有独立书桌和储物柜'
        }
      ]
      
      // 根据筛选条件过滤示例数据
      let filteredDemoRooms = demoRooms
      if (params.buildingId) {
        filteredDemoRooms = filteredDemoRooms.filter(r => r.buildingId == params.buildingId)
      }
      if (params.roomType) {
        filteredDemoRooms = filteredDemoRooms.filter(r => r.roomType == params.roomType)
      }
      
      rooms.value = filteredDemoRooms
      total.value = rooms.value.length
    }
  } catch (error) {
    console.error('获取房间列表失败', error)
    ElMessage.error('获取房间数据失败')
  } finally {
    loading.value = false
  }
}

// 根据房型ID获取房型名称
const getRoomTypeName = (roomType) => {
  const types = {
    '1': '单人间',
    '2': '双人间',
    '4': '四人间',
    '6': '六人间'
  }
  return types[roomType] || '未知房型'
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
  padding: 20px 0;
  background-color: white;
  border-bottom: 1px solid #EBEEF5;
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
.rooms-list {
  margin-top: 20px;
}

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
