<template>
  <div class="home-page">
    <!-- 英雄区 - 简约风格 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1>智慧校园公寓</h1>
        <h2>简单舒适的学生之家</h2>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="scrollToRooms">查看房源</el-button>
          <el-button size="large" @click="$router.push('/about')">了解更多</el-button>
        </div>
      </div>
    </section>

    <!-- 数据统计区 -->
    <section class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">{{ stats.buildingCount }}</div>
            <div class="stat-label">公寓楼栋</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.roomCount }}</div>
            <div class="stat-label">可用房间</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.studentCount }}</div>
            <div class="stat-label">入住学生</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.satisfaction }}%</div>
            <div class="stat-label">满意度</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 房源预览 -->
    <section class="rooms-section" id="rooms-section">
      <div class="container">
        <h2 class="section-title">热门房源</h2>
        <p class="section-subtitle">为您精选舒适便捷的公寓房间</p>

        <el-row :gutter="24">
          <el-col :xs="24" :sm="12" :md="8" v-for="building in buildings" :key="building.id">
            <el-card class="building-card" shadow="hover">
              <div class="building-image" :style="{ backgroundImage: `url(${building.imageUrl || getRandomBuildingImage()})` }">
                <div class="building-badge" :class="building.roomCount > 10 ? 'available' : 'limited'">
                  {{ building.roomCount > 10 ? '充足' : '紧张' }}
                </div>
              </div>
              <div class="building-info">
                <h3>{{ building.name }}</h3>
                <p class="building-description">{{ building.description || '舒适便捷的学生公寓，配套设施齐全' }}</p>
                <div class="building-meta">
                  <span><el-icon><HomeFilled /></el-icon> {{ building.roomCount || '--' }}间可用</span>
                  <span><el-icon><Location /></el-icon> {{ building.location || '校区内' }}</span>
                </div>
                <el-button type="primary" plain class="view-button" @click="viewBuilding(building.id)">
                  查看详情
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <div class="view-all">
          <el-button type="primary" plain @click="$router.push('/rooms')">查看全部房源</el-button>
        </div>
      </div>
    </section>
    
    <!-- 公告与信息 -->
    <section class="announcements-section">
      <div class="container">
        <el-row :gutter="24">
          <el-col :xs="24" :md="16">
            <div class="section-header">
              <h2 class="section-title">最新公告</h2>
              <el-button text @click="$router.push('/news')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
            </div>
            
            <el-timeline>
              <el-timeline-item
                v-for="announcement in announcements"
                :key="announcement.id"
                :timestamp="announcement.date"
                :type="announcement.type">
                <h4>{{ announcement.title }}</h4>
                <p class="announcement-preview">{{ announcement.content }}</p>
              </el-timeline-item>
            </el-timeline>
          </el-col>
          
          <el-col :xs="24" :md="8">
            <el-card class="quick-links-card">
              <template #header>
                <div class="card-header">
                  <h3>快速入口</h3>
                </div>
              </template>
              
              <div class="quick-links">
                <el-button v-if="isStudent" @click="$router.push('/student/checkin')" class="quick-link">
                  <el-icon><Key /></el-icon> 申请入住
                </el-button>
                <el-button v-if="isStudent" @click="$router.push('/student/repair')" class="quick-link">
                  <el-icon><Tools /></el-icon> 报修服务
                </el-button>
                <el-button @click="$router.push(isLoggedIn ? '/student/profile' : '/student/login')" class="quick-link">
                  <el-icon><User /></el-icon> {{ isLoggedIn ? '个人中心' : '学生登录' }}
                </el-button>
                <el-button @click="$router.push('/contact')" class="quick-link">
                  <el-icon><Message /></el-icon> 联系我们
                </el-button>
              </div>
            </el-card>
            
            <el-card class="contact-card" shadow="hover">
              <h3><el-icon><Service /></el-icon> 服务热线</h3>
              <div class="contact-phone">400-123-4567</div>
              <p class="contact-hours">周一至周日 8:00-22:00</p>
              <el-divider />
              <div class="social-links">
                <el-button circle><el-icon><Message /></el-icon></el-button>
                <el-button circle><el-icon><ChatDotRound /></el-icon></el-button>
                <el-button circle><el-icon><Promotion /></el-icon></el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBuildingList } from '@/api/building'
import { ElMessage } from 'element-plus'
import { 
  HomeFilled, Location, ArrowRight, Tools, 
  Key, User, Message, Service, ChatDotRound, 
  Promotion
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 判断用户是否登录及是否为学生
const isLoggedIn = computed(() => !!userStore.token && !!userStore.userInfo?.id)
const isStudent = computed(() => isLoggedIn.value && userStore.userInfo?.userType === 3)

// 数据统计
const stats = ref({
  buildingCount: 8,
  roomCount: 450,
  studentCount: 1200,
  satisfaction: 98
})

// 楼宇列表
const buildings = ref([])

// 公告
const announcements = ref([
  { 
    id: 1, 
    title: '关于2025年秋季学期入住申请的通知', 
    content: '2025年秋季学期入住申请即将开始，请同学们及时关注系统通知，按时完成申请。',
    date: '2025-09-01', 
    type: 'primary'
  },
  { 
    id: 2, 
    title: '国庆节期间公寓安全注意事项', 
    content: '为确保国庆期间公寓安全，请留校同学注意以下事项...',
    date: '2025-09-28',
    type: 'warning'
  },
  { 
    id: 3, 
    title: '公寓网络升级维护公告', 
    content: '为提升网络服务质量，公寓网络将于10月10日进行升级维护，期间网络可能不稳定。',
    date: '2025-10-10',
    type: 'info'
  },
  { 
    id: 4, 
    title: '冬季供暖通知', 
    content: '根据气象预报，校区将于11月1日开始供暖，请各位同学做好准备。',
    date: '2025-11-01',
    type: 'success'
  }
])

// 随机楼栋图片
const buildingImages = [
  'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600',
  'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600',
  'https://images.unsplash.com/photo-1556742533-2b2a6ee50906?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600'
]

const getRandomBuildingImage = () => {
  return buildingImages[Math.floor(Math.random() * buildingImages.length)]
}

// 查看楼栋详情
const viewBuilding = (id) => {
  router.push(`/rooms?buildingId=${id}`)
}

// 滚动到房源区
const scrollToRooms = () => {
  document.getElementById('rooms-section').scrollIntoView({ behavior: 'smooth' })
}

// 加载楼宇数据
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
          imageUrl: getRandomBuildingImage()
        },
        {
          id: 102,
          name: '博雅公寓',
          description: '紧邻图书馆，学习氛围浓厚，安静舒适',
          roomCount: 8,
          location: '校园北区',
          imageUrl: getRandomBuildingImage()
        },
        {
          id: 103,
          name: '致远公寓',
          description: '靠近体育场，活动便利，配有共享空间',
          roomCount: 56,
          location: '校园西区',
          imageUrl: getRandomBuildingImage()
        }
      ]
      
      buildings.value = [...buildings.value, ...demoBuildings.slice(0, 3 - buildings.value.length)]
    }
  } catch (error) {
    console.error('获取楼宇列表失败', error)
    // 使用示例数据
    buildings.value = [
      {
        id: 101,
        name: '明德公寓',
        description: '位于校园东区，环境优美，配套设施完善',
        roomCount: 120,
        location: '校园东区',
        imageUrl: getRandomBuildingImage()
      },
      {
        id: 102,
        name: '博雅公寓',
        description: '紧邻图书馆，学习氛围浓厚，安静舒适',
        roomCount: 8,
        location: '校园北区',
        imageUrl: getRandomBuildingImage()
      },
      {
        id: 103,
        name: '致远公寓',
        description: '靠近体育场，活动便利，配有共享空间',
        roomCount: 56,
        location: '校园西区',
        imageUrl: getRandomBuildingImage()
      }
    ]
  }
}

onMounted(() => {
  loadBuildings()
  
  // 加载统计数据
  // 在实际项目中，这里应该从后端API获取真实的统计数据
  // 此处使用模拟数据
  
  // 如果登录了显示欢迎消息
  if (isLoggedIn.value) {
    const userName = userStore.userInfo.realName || userStore.userInfo.username
    ElMessage({
      message: `欢迎回来，${userName}！`,
      type: 'success'
    })
  }
})
</script>

<style scoped>
/* 整体样式 */
.home-page {
  background-color: #f9fafb;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1a1a1a;
}

.section-subtitle {
  font-size: 16px;
  color: #606266;
  margin-bottom: 32px;
}

/* 英雄区样式 */
.hero-section {
  height: 70vh;
  min-height: 500px;
  max-height: 700px;
  background-image: linear-gradient(rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.3)), url('https://images.unsplash.com/photo-1513694203232-719a280e022f?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=1600');
  background-size: cover;
  background-position: center;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  margin-bottom: 0;
}

.hero-content {
  padding: 20px;
  max-width: 800px;
}

.hero-content h1 {
  font-size: 3.5rem;
  margin-bottom: 16px;
  font-weight: 700;
}

.hero-content h2 {
  font-size: 1.5rem;
  margin-bottom: 32px;
  font-weight: 400;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

/* 数据统计区 */
.stats-section {
  padding: 40px 0;
  background-color: white;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 24px;
  text-align: center;
}

.stat-item {
  padding: 24px 16px;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: #409EFF;
  line-height: 1.2;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  color: #606266;
}

/* 房源预览区 */
.rooms-section {
  padding: 60px 0;
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

.building-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  color: #909399;
  font-size: 14px;
}

.building-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.view-button {
  width: 100%;
}

.view-all {
  text-align: center;
  margin-top: 32px;
}

/* 公告区 */
.announcements-section {
  padding: 60px 0;
  background-color: #f9fafb;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.announcement-preview {
  color: #606266;
  margin: 8px 0;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 快速链接卡片 */
.quick-links-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.quick-link {
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
  align-items: center;
  height: 80px;
  font-size: 14px;
}

.quick-link .el-icon {
  font-size: 24px;
}

/* 联系卡片 */
.contact-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  padding: 20px;
}

.contact-card h3 {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 18px;
  margin-bottom: 12px;
}

.contact-phone {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.contact-hours {
  font-size: 14px;
  opacity: 0.8;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 16px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 2.5rem;
  }

  .hero-content h2 {
    font-size: 1.2rem;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .quick-links {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
