<template>
  <div class="news-page">
    <section class="page-header">
      <div class="container">
        <h1>新闻动态</h1>
        <p>了解智慧公寓的最新动态和重要通知</p>
      </div>
    </section>

    <section class="news-section">
      <div class="container">
        <el-row :gutter="24">
          <el-col :md="16" :sm="24">
            <!-- 新闻列表 -->
            <div class="news-list">
              <div v-for="news in newsList" :key="news.id" class="news-item">
                <el-card shadow="hover">
                  <div class="news-content">
                    <div class="news-image" v-if="news.image">
                      <img :src="news.image" :alt="news.title">
                    </div>
                    <div class="news-body">
                      <div class="news-meta">
                        <el-tag size="small" :type="getTagType(news.category)">{{ news.category }}</el-tag>
                        <span class="news-date">{{ news.date }}</span>
                      </div>
                      <h3 class="news-title" @click="viewNewsDetail(news)">{{ news.title }}</h3>
                      <p class="news-summary">{{ news.summary }}</p>
                      <div class="news-footer">
                        <el-button text @click="viewNewsDetail(news)">阅读更多</el-button>
                      </div>
                    </div>
                  </div>
                </el-card>
              </div>
              
              <el-pagination
                v-if="newsList.length > 0"
                background
                layout="prev, pager, next"
                :total="total"
                :page-size="6"
                :current-page="currentPage"
                @current-change="handlePageChange"
                class="pagination"
              />
            </div>
          </el-col>
          
          <el-col :md="8" :sm="24">
            <!-- 侧边栏 -->
            <div class="sidebar">
              <!-- 分类 -->
              <el-card class="sidebar-card">
                <template #header>
                  <div class="card-header">
                    <h3>新闻分类</h3>
                  </div>
                </template>
                <div class="category-list">
                  <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
                    <el-radio-button label="">全部</el-radio-button>
                    <el-radio-button label="通知">通知</el-radio-button>
                    <el-radio-button label="活动">活动</el-radio-button>
                    <el-radio-button label="公告">公告</el-radio-button>
                  </el-radio-group>
                </div>
              </el-card>
              
              <!-- 热门新闻 -->
              <el-card class="sidebar-card">
                <template #header>
                  <div class="card-header">
                    <h3>热门新闻</h3>
                  </div>
                </template>
                <div class="hot-news-list">
                  <div v-for="(item, index) in hotNews" :key="item.id" class="hot-news-item" @click="viewNewsDetail(item)">
                    <span class="hot-index">{{ index + 1 }}</span>
                    <span class="hot-title">{{ item.title }}</span>
                  </div>
                </div>
              </el-card>
              
              <!-- 联系卡片 -->
              <el-card class="contact-card">
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
            </div>
          </el-col>
        </el-row>
      </div>
    </section>
    
    <!-- 新闻详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="selectedNews?.title || '新闻详情'"
      width="70%"
      class="news-dialog">
      <div v-if="selectedNews" class="news-detail">
        <div class="news-detail-header">
          <h2>{{ selectedNews.title }}</h2>
          <div class="news-detail-meta">
            <el-tag size="small" :type="getTagType(selectedNews.category)">{{ selectedNews.category }}</el-tag>
            <span class="news-date">发布时间：{{ selectedNews.date }}</span>
            <span class="news-views">浏览量：{{ selectedNews.views || 0 }}</span>
          </div>
        </div>
        
        <el-divider />
        
        <div class="news-detail-content">
          <div v-if="selectedNews.image" class="news-detail-image">
            <img :src="selectedNews.image" :alt="selectedNews.title">
          </div>
          <div class="news-detail-text" v-html="selectedNews.content"></div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Service, Message, ChatDotRound, Promotion
} from '@element-plus/icons-vue'

const router = useRouter()

// 分页
const currentPage = ref(1)
const total = ref(24) // 假设有24条新闻
const pageSize = ref(6)

// 分类过滤
const selectedCategory = ref('')

// 新闻详情
const selectedNews = ref(null)
const detailDialogVisible = ref(false)

// 模拟新闻数据
const allNews = ref([
  {
    id: 1,
    title: '关于2025年秋季学期入住申请的通知',
    summary: '2025年秋季学期入住申请即将开始，请同学们及时关注系统通知，按时完成申请。申请时间为8月15日至8月30日，请各位同学务必在规定时间内提交申请。',
    content: `<p>各位同学：</p>
      <p>2025年秋季学期入住申请即将开始，现将有关事项通知如下：</p>
      <h3>一、申请时间</h3>
      <p>2025年8月15日至8月30日</p>
      <h3>二、申请条件</h3>
      <p>1. 已完成本学期注册缴费</p>
      <p>2. 无违纪记录</p>
      <p>3. 上学期无不良住宿行为记录</p>
      <h3>三、申请方式</h3>
      <p>登录智慧公寓管理系统，进入"学生端"—"入住申请"页面完成申请。</p>
      <h3>四、注意事项</h3>
      <p>1. 请及时提交申请，逾期不予受理</p>
      <p>2. 申请提交后请密切关注审批状态</p>
      <p>3. 如有特殊情况，请联系公寓管理办公室</p>
      <p>特此通知</p>
      <p>公寓管理办公室</p>
      <p>2025年8月10日</p>`,
    category: '通知',
    date: '2025-08-10',
    image: 'https://images.unsplash.com/photo-1555854877-bab0e564b8d5?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600',
    views: 325
  },
  {
    id: 2,
    title: '国庆节期间公寓安全注意事项',
    summary: '为确保国庆期间公寓安全，请留校同学注意防火、防盗等安全事项，外出同学请务必关闭电源、锁好门窗。假期期间公寓将进行安全检查。',
    content: `<p>亲爱的同学们：</p>
      <p>国庆节假期即将来临，为确保假期期间公寓安全，请大家注意以下事项：</p>
      <h3>一、留校同学注意事项</h3>
      <p>1. 注意用电安全，离开房间请关闭电源</p>
      <p>2. 保管好个人贵重物品</p>
      <p>3. 夜间请锁好房门</p>
      <p>4. 发现可疑情况请立即联系宿管或保安</p>
      <h3>二、离校同学注意事项</h3>
      <p>1. 清理室内垃圾</p>
      <p>2. 关闭所有电源</p>
      <p>3. 锁好门窗</p>
      <p>4. 贵重物品请随身携带或妥善保管</p>
      <h3>三、假期值班安排</h3>
      <p>假期期间公寓管理办公室每天8:00-20:00有值班人员</p>
      <p>紧急情况联系电话：400-123-4567</p>
      <p>祝大家国庆节快乐！</p>
      <p>公寓管理办公室</p>
      <p>2025年9月28日</p>`,
    category: '通知',
    date: '2025-09-28',
    image: 'https://images.unsplash.com/photo-1569836380424-7c8504b395d0?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600',
    views: 218
  },
  {
    id: 3,
    title: '公寓网络升级维护公告',
    summary: '为提升网络服务质量，公寓网络将于10月10日进行升级维护，期间网络可能不稳定。维护时间为上午10:00-下午16:00，请同学们合理安排时间。',
    content: `<p>各位同学：</p>
      <p>为了提升公寓网络服务质量，满足同学们日常学习和生活的需要，我们计划于10月10日进行网络设备升级维护工作，具体安排如下：</p>
      <h3>一、维护时间</h3>
      <p>2025年10月10日 10:00-16:00</p>
      <h3>二、影响范围</h3>
      <p>明德公寓、博雅公寓、致远公寓、和悦公寓</p>
      <h3>三、维护内容</h3>
      <p>1. 更换核心交换设备</p>
      <p>2. 升级网络带宽</p>
      <p>3. 优化网络架构</p>
      <h3>四、注意事项</h3>
      <p>1. 维护期间网络可能不稳定，请合理安排学习和工作</p>
      <p>2. 如遇特殊情况，维护时间可能会有所调整</p>
      <p>3. 维护结束后，如发现网络异常，请及时反馈</p>
      <p>感谢大家的理解和配合！</p>
      <p>网络管理中心</p>
      <p>2025年10月8日</p>`,
    category: '公告',
    date: '2025-10-08',
    image: null,
    views: 187
  },
  {
    id: 4,
    title: '冬季供暖通知',
    summary: '根据气象预报，校区将于11月1日开始供暖，请各位同学做好准备。如有暖气片漏水等问题，请及时联系维修人员处理。',
    content: `<p>各位同学：</p>
      <p>根据气象部门预报，今年气温下降较快，校区将于11月1日开始供暖，现将有关事项通知如下：</p>
      <h3>一、供暖时间</h3>
      <p>2025年11月1日 - 2026年3月15日</p>
      <h3>二、供暖标准</h3>
      <p>室内温度保持在18℃以上</p>
      <h3>三、注意事项</h3>
      <p>1. 供暖初期，请检查暖气片是否正常工作</p>
      <p>2. 如发现暖气片漏水或不热，请及时报修</p>
      <p>3. 为保证室内温度，请勿长时间开窗通风</p>
      <p>4. 节约能源，离开房间请关闭门窗</p>
      <h3>四、报修方式</h3>
      <p>1. 智慧公寓管理系统在线报修</p>
      <p>2. 公寓管理办公室电话：400-123-4567</p>
      <p>特此通知</p>
      <p>后勤保障部</p>
      <p>2025年10月25日</p>`,
    category: '通知',
    date: '2025-10-25',
    image: 'https://images.unsplash.com/photo-1603236205516-1dec1ae38304?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600',
    views: 156
  },
  {
    id: 5,
    title: '"温馨宿舍 和谐家园"主题宿舍文化节活动预告',
    summary: '第十二届宿舍文化节将于11月15日至12月15日举行，活动包括宿舍装饰大赛、才艺展示、知识竞赛等多项内容，欢迎同学们积极参与。',
    content: `<p>亲爱的同学们：</p>
      <p>为丰富校园文化生活，培养团队协作精神，促进宿舍和谐发展，我校第十二届"温馨宿舍 和谐家园"主题宿舍文化节将于11月15日至12月15日举行。现将活动安排公告如下：</p>
      <h3>一、活动时间</h3>
      <p>2025年11月15日 - 2025年12月15日</p>
      <h3>二、活动内容</h3>
      <h4>1. 宿舍装饰大赛</h4>
      <p>主题：创意生活，美化家园</p>
      <p>评选标准：创意性、美观性、实用性、环保性</p>
      <p>报名时间：11月10日-11月14日</p>
      <h4>2. 宿舍才艺展示</h4>
      <p>形式：歌曲、舞蹈、乐器、相声、小品等</p>
      <p>时间：11月25日</p>
      <p>地点：学生活动中心</p>
      <h4>3. 宿舍知识竞赛</h4>
      <p>内容：公寓管理规定、安全知识、文明礼仪等</p>
      <p>时间：12月5日</p>
      <p>地点：图书馆报告厅</p>
      <h4>4. 优秀宿舍评选</h4>
      <p>评选标准：卫生、纪律、学习氛围、团队精神</p>
      <p>评选时间：12月10日-12月15日</p>
      <h3>三、奖项设置</h3>
      <p>各项活动设一、二、三等奖及优胜奖</p>
      <p>最终评出"最佳宿舍"、"最具创意宿舍"、"最和谐宿舍"等荣誉称号</p>
      <h3>四、报名方式</h3>
      <p>登录智慧公寓管理系统，进入"活动报名"页面</p>
      <p>欢迎广大同学踊跃参与！</p>
      <p>学生工作处</p>
      <p>2025年11月5日</p>`,
    category: '活动',
    date: '2025-11-05',
    image: 'https://images.unsplash.com/photo-1523580494863-6f3031224c94?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600',
    views: 285
  },
  {
    id: 6,
    title: '2026年寒假离校安排',
    summary: '2026年寒假将于1月15日开始，请同学们按照规定时间办理离校手续。需要留校的同学请提前申请，并遵守相关规定。',
    content: `<p>各位同学：</p>
      <p>2026年寒假即将来临，现将离校相关事项通知如下：</p>
      <h3>一、放假时间</h3>
      <p>2026年1月15日 - 2026年2月28日</p>
      <h3>二、离校手续</h3>
      <h4>1. 离校前准备</h4>
      <p>- 清理个人物品和垃圾</p>
      <p>- 断开所有电源</p>
      <p>- 关闭水龙头</p>
      <p>- 锁好门窗</p>
      <h4>2. 办理手续</h4>
      <p>- 宿舍长检查宿舍卫生并签字确认</p>
      <p>- 到公寓管理办公室登记离校时间</p>
      <p>- 归还公共物品（如有借用）</p>
      <h3>三、留校申请</h3>
      <p>需要留校的同学请于1月5日前在智慧公寓管理系统提交申请</p>
      <p>审批通过后，将安排集中住宿</p>
      <h3>四、返校时间</h3>
      <p>2026年2月28日 - 2026年3月1日</p>
      <p>具体安排请关注后续通知</p>
      <p>特此通知</p>
      <p>公寓管理办公室</p>
      <p>2025年12月20日</p>`,
    category: '通知',
    date: '2025-12-20',
    image: null,
    views: 132
  }
])

// 新闻列表 - 根据分页和筛选条件
const newsList = computed(() => {
  let filtered = allNews.value
  
  // 分类过滤
  if (selectedCategory.value) {
    filtered = filtered.filter(news => news.category === selectedCategory.value)
  }
  
  // 计算总数
  total.value = filtered.length
  
  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  
  return filtered.slice(start, end)
})

// 热门新闻 - 按浏览量排序
const hotNews = computed(() => {
  return [...allNews.value].sort((a, b) => (b.views || 0) - (a.views || 0)).slice(0, 5)
})

// 根据分类获取标签类型
const getTagType = (category) => {
  const types = {
    '通知': 'primary',
    '活动': 'success',
    '公告': 'warning'
  }
  return types[category] || 'info'
}

// 查看新闻详情
const viewNewsDetail = (news) => {
  selectedNews.value = news
  // 增加浏览量
  news.views = (news.views || 0) + 1
  detailDialogVisible.value = true
}

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page
}

// 处理分类变化
const handleCategoryChange = () => {
  currentPage.value = 1
}

onMounted(() => {
  // 在实际项目中，这里应该调用API获取新闻列表
  // 此处使用模拟数据
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

/* 新闻部分 */
.news-section {
  padding: 40px 0;
  background-color: #f9fafb;
  min-height: calc(100vh - 220px);
}

/* 新闻列表 */
.news-item {
  margin-bottom: 24px;
}

.news-content {
  display: flex;
  flex-direction: column;
}

.news-image {
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 16px;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.news-image img:hover {
  transform: scale(1.05);
}

.news-meta {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.news-date {
  margin-left: 12px;
  font-size: 14px;
  color: #909399;
}

.news-title {
  margin: 0 0 12px;
  font-size: 20px;
  cursor: pointer;
  color: #303133;
  transition: color 0.3s;
}

.news-title:hover {
  color: #409EFF;
}

.news-summary {
  color: #606266;
  margin: 0 0 16px;
  line-height: 1.6;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.news-footer {
  margin-top: auto;
}

.pagination {
  margin-top: 32px;
  text-align: center;
}

/* 侧边栏 */
.sidebar-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

/* 热门新闻 */
.hot-news-item {
  padding: 12px 0;
  display: flex;
  align-items: center;
  cursor: pointer;
  border-bottom: 1px solid #EBEEF5;
  transition: background-color 0.3s;
}

.hot-news-item:last-child {
  border-bottom: none;
}

.hot-news-item:hover {
  background-color: #f5f7fa;
}

.hot-index {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background-color: #f0f0f0;
  border-radius: 50%;
  margin-right: 12px;
  font-size: 14px;
  font-weight: bold;
  color: #606266;
}

.hot-news-item:nth-child(1) .hot-index {
  background-color: #f56c6c;
  color: white;
}

.hot-news-item:nth-child(2) .hot-index {
  background-color: #e6a23c;
  color: white;
}

.hot-news-item:nth-child(3) .hot-index {
  background-color: #409EFF;
  color: white;
}

.hot-title {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #303133;
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
  margin: 0 0 12px;
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

/* 新闻详情对话框 */
.news-detail-header {
  text-align: center;
  margin-bottom: 20px;
}

.news-detail-header h2 {
  margin: 0 0 16px;
  font-size: 24px;
}

.news-detail-meta {
  color: #909399;
  font-size: 14px;
  display: flex;
  gap: 16px;
  justify-content: center;
  align-items: center;
}

.news-detail-image {
  max-height: 400px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 24px;
}

.news-detail-image img {
  width: 100%;
  object-fit: cover;
}

.news-detail-text {
  line-height: 1.8;
}

@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }
  
  .page-header h1 {
    font-size: 28px;
  }
  
  .sidebar {
    margin-top: 30px;
  }
  
  .news-detail-header h2 {
    font-size: 20px;
  }
  
  .news-detail-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
