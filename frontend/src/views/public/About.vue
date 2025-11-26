<template>
  <div class="about-page">
    <section class="page-header">
      <div class="container">
        <h1>关于我们</h1>
        <p>了解智慧公寓的理念、历史与发展</p>
      </div>
    </section>

    <section class="intro-section">
      <div class="container">
        <el-row :gutter="40">
          <el-col :md="12" :sm="24">
            <div class="intro-content">
              <h2>智慧公寓介绍</h2>
              <p class="intro-text">
                智慧公寓管理系统是一个面向高校学生公寓的现代化管理平台，致力于为学生提供安全、便捷、舒适的住宿环境。
                我们运用先进的信息技术手段，实现公寓管理的智能化、精细化和人性化，全面提升学生的住宿体验和满意度。
              </p>
              <p class="intro-text">
                系统涵盖入住申请、退宿办理、房间调换、报修服务等全流程在线管理功能，
                同时整合了安全监控、能源管理等智能化管理措施，打造真正的"智慧"公寓。
              </p>
            </div>
          </el-col>
          <el-col :md="12" :sm="24">
            <div class="intro-image">
              <img src="https://images.unsplash.com/photo-1513694203232-719a280e022f?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb&w=600" alt="智慧公寓">
            </div>
          </el-col>
        </el-row>
      </div>
    </section>
    
    <section class="values-section">
      <div class="container">
        <h2 class="section-title">我们的理念</h2>
        <p class="section-subtitle">以人为本，科技赋能，打造舒适安全的学生之家</p>
        
        <el-row :gutter="24">
          <el-col :md="8" :sm="12" :xs="24" v-for="(value, index) in values" :key="index">
            <div class="value-card">
              <div class="value-icon">
                <el-icon :size="40" :color="value.color">
                  <component :is="value.icon"></component>
                </el-icon>
              </div>
              <h3>{{ value.title }}</h3>
              <p>{{ value.description }}</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>
    
    <section class="history-section">
      <div class="container">
        <h2 class="section-title">发展历程</h2>
        <p class="section-subtitle">不断创新，持续提升公寓管理水平</p>
        
        <el-timeline>
          <el-timeline-item
            v-for="(history, index) in histories"
            :key="index"
            :timestamp="history.year"
            :color="history.color"
            placement="top"
            :hollow="index % 2 === 0"
          >
            <el-card class="history-card">
              <h3>{{ history.title }}</h3>
              <p>{{ history.description }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </section>
    
    <section class="team-section">
      <div class="container">
        <h2 class="section-title">管理团队</h2>
        <p class="section-subtitle">专业、负责、高效的公寓管理队伍</p>
        
        <el-row :gutter="24">
          <el-col :md="6" :sm="12" :xs="24" v-for="(member, index) in team" :key="index">
            <el-card class="team-card" shadow="hover">
              <div class="team-avatar">
                <el-avatar :size="120" :src="member.avatar">{{ member.name.slice(0, 1) }}</el-avatar>
              </div>
              <h3 class="team-name">{{ member.name }}</h3>
              <div class="team-title">{{ member.title }}</div>
              <p class="team-description">{{ member.description }}</p>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </section>
    
    <section class="faq-section">
      <div class="container">
        <h2 class="section-title">常见问题</h2>
        <p class="section-subtitle">解答您关心的问题</p>
        
        <el-collapse accordion>
          <el-collapse-item v-for="(faq, index) in faqs" :key="index" :title="faq.question" :name="index">
            <div v-html="faq.answer"></div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </section>
    
    <section class="contact-section">
      <div class="container">
        <el-row :gutter="30">
          <el-col :md="12" :sm="24">
            <h2 class="section-title">联系我们</h2>
            <p class="section-subtitle">有任何问题，随时联系我们</p>
            
            <div class="contact-info">
              <div class="contact-item">
                <el-icon><Location /></el-icon>
                <span>地址：某某大学校园内明德公寓管理中心</span>
              </div>
              <div class="contact-item">
                <el-icon><Phone /></el-icon>
                <span>电话：400-123-4567</span>
              </div>
              <div class="contact-item">
                <el-icon><Message /></el-icon>
                <span>邮箱：info@apartment.com</span>
              </div>
              <div class="contact-item">
                <el-icon><Timer /></el-icon>
                <span>工作时间：周一至周日 8:00-22:00</span>
              </div>
            </div>
          </el-col>
          <el-col :md="12" :sm="24">
            <div class="map-container">
              <img src="https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/pin-s+555555(120.12,30.28)/120.12,30.28,15,0/600x400?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4M29iazA2Z2gycXA4N2pmbDZmangifQ.-g_vE53SD2WrJ6tFX7QHmA" alt="地图位置" />
            </div>
          </el-col>
        </el-row>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  House, Shield, Star, Calendar, Reading, Setting,
  Location, Phone, Message, Timer
} from '@element-plus/icons-vue'

// 核心价值观
const values = ref([
  {
    title: '以人为本',
    description: '以学生需求为中心，关注每一位学生的住宿体验和生活质量',
    icon: 'House',
    color: '#409EFF'
  },
  {
    title: '安全第一',
    description: '建立完善的安全管理机制，保障学生住宿安全',
    icon: 'Shield',
    color: '#67C23A'
  },
  {
    title: '智能便捷',
    description: '运用信息技术手段，提供智能化、便捷化的服务',
    icon: 'Setting',
    color: '#E6A23C'
  },
  {
    title: '育人为先',
    description: '将公寓管理融入育人体系，培养学生良好生活习惯',
    icon: 'Reading',
    color: '#F56C6C'
  },
  {
    title: '优质服务',
    description: '提供专业、热情、高效的服务，提升学生满意度',
    icon: 'Star',
    color: '#909399'
  },
  {
    title: '持续改进',
    description: '不断听取意见，持续优化管理，提升服务水平',
    icon: 'Calendar',
    color: '#9254de'
  }
])

// 发展历程
const histories = ref([
  {
    year: '2020年',
    title: '智慧公寓项目启动',
    description: '启动智慧公寓管理系统规划设计，确定系统整体架构和功能模块',
    color: '#409EFF'
  },
  {
    year: '2021年',
    title: '系统开发完成',
    description: '完成基础版本的开发工作，实现宿舍管理、入住申请等核心功能',
    color: '#67C23A'
  },
  {
    year: '2022年',
    title: '系统正式上线',
    description: '系统正式投入使用，首批接入3栋公寓楼，覆盖2000名学生',
    color: '#E6A23C'
  },
  {
    year: '2023年',
    title: '功能扩展与完善',
    description: '新增报修服务、房间调换等功能模块，优化用户体验',
    color: '#9254de'
  },
  {
    year: '2024年',
    title: '全面智能化升级',
    description: '引入人脸识别、物联网等技术，实现公寓管理全面智能化',
    color: '#F56C6C'
  },
  {
    year: '2025年',
    title: '覆盖全校公寓',
    description: '系统覆盖全校所有公寓楼，服务学生超过10000人',
    color: '#409EFF'
  }
])

// 团队成员
const team = ref([
  {
    name: '李明',
    title: '公寓管理中心主任',
    description: '负责公寓整体管理工作，拥有15年高校公寓管理经验',
    avatar: 'https://xsgames.co/randomusers/assets/avatars/male/1.jpg'
  },
  {
    name: '王华',
    title: '生活服务部主管',
    description: '负责学生生活服务工作，致力于提升学生住宿体验',
    avatar: 'https://xsgames.co/randomusers/assets/avatars/male/2.jpg'
  },
  {
    name: '张芳',
    title: '安全管理部主管',
    description: '负责公寓安全工作，确保学生住宿安全',
    avatar: 'https://xsgames.co/randomusers/assets/avatars/female/1.jpg'
  },
  {
    name: '赵强',
    title: '技术支持部主管',
    description: '负责系统维护和技术支持，保障系统稳定运行',
    avatar: 'https://xsgames.co/randomusers/assets/avatars/male/3.jpg'
  }
])

// 常见问题
const faqs = ref([
  {
    question: '如何申请入住公寓？',
    answer: `<p>入住申请流程如下：</p>
      <ol>
        <li>登录智慧公寓管理系统</li>
        <li>进入"学生端"—"入住申请"页面</li>
        <li>选择意向楼栋和房间类型</li>
        <li>提交申请并等待审批</li>
        <li>审批通过后，按照通知时间前往公寓办理入住手续</li>
      </ol>`
  },
  {
    question: '公寓有哪些类型的房间？',
    answer: `<p>我们提供以下几种类型的房间：</p>
      <ul>
        <li>单人间：配备独立卫浴、空调、书桌、衣柜等</li>
        <li>双人间：两人共用一个房间，配备空调、书桌、衣柜等</li>
        <li>四人间：四人共用一个房间，配备空调、书桌、衣柜等</li>
        <li>六人间：六人共用一个房间，配备空调、书桌、衣柜等</li>
      </ul>
      <p>不同楼栋的配置可能有所不同，具体以实际情况为准。</p>`
  },
  {
    question: '公寓有哪些公共设施？',
    answer: `<p>我们的公寓配备了丰富的公共设施，包括：</p>
      <ul>
        <li>公共洗衣房：每栋公寓一层设有公共洗衣房，配备洗衣机、烘干机</li>
        <li>自习室：每栋公寓设有自习室，提供安静的学习环境</li>
        <li>活动室：配备乒乓球桌、健身器材等</li>
        <li>公共厨房：部分公寓设有公共厨房，供学生使用</li>
        <li>快递收发室：方便学生收发快递</li>
      </ul>`
  },
  {
    question: '如何申请维修服务？',
    answer: `<p>申请维修服务的方式有两种：</p>
      <ol>
        <li>线上申请：登录智慧公寓管理系统，进入"报修服务"页面，填写报修信息并提交</li>
        <li>线下申请：前往公寓管理办公室填写报修单</li>
      </ol>
      <p>工作人员会在收到报修申请后尽快处理，一般情况下会在24小时内安排维修。</p>`
  },
  {
    question: '公寓有门禁时间限制吗？',
    answer: `<p>是的，为了保障学生安全，公寓实行门禁管理：</p>
      <ul>
        <li>公寓大门开放时间：早上6:00至晚上23:00</li>
        <li>23:00后需要通过学生证或门禁卡刷卡进入</li>
        <li>特殊情况需要晚归的同学，请提前向公寓管理员申请</li>
      </ul>
      <p>请同学们合理安排时间，注意人身安全。</p>`
  },
  {
    question: '如何办理退宿手续？',
    answer: `<p>退宿流程如下：</p>
      <ol>
        <li>登录智慧公寓管理系统</li>
        <li>进入"退宿申请"页面，填写退宿原因和预计退宿时间</li>
        <li>提交申请并等待审批</li>
        <li>审批通过后，按照通知时间前往公寓办理退宿手续</li>
        <li>清理个人物品，打扫房间卫生</li>
        <li>公寓管理员检查房间状况后，办理退宿手续</li>
      </ol>`
  }
])
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

/* 介绍部分 */
.intro-section {
  padding: 60px 0;
  background-color: white;
}

.intro-content {
  padding-right: 20px;
}

.intro-content h2 {
  font-size: 28px;
  margin: 0 0 24px;
  color: #303133;
}

.intro-text {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  margin-bottom: 20px;
}

.intro-image {
  height: 100%;
  min-height: 300px;
  border-radius: 8px;
  overflow: hidden;
}

.intro-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 通用节标题 */
.section-title {
  font-size: 28px;
  text-align: center;
  margin: 0 0 16px;
  color: #303133;
}

.section-subtitle {
  font-size: 16px;
  text-align: center;
  color: #606266;
  margin: 0 0 40px;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

/* 价值观部分 */
.values-section {
  padding: 60px 0;
  background-color: #f9fafb;
}

.value-card {
  background-color: white;
  padding: 30px 24px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  height: 100%;
  transition: transform 0.3s, box-shadow 0.3s;
}

.value-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.value-icon {
  margin-bottom: 20px;
}

.value-card h3 {
  font-size: 18px;
  margin: 0 0 16px;
  color: #303133;
}

.value-card p {
  font-size: 14px;
  color: #606266;
  margin: 0;
  line-height: 1.6;
}

/* 历史部分 */
.history-section {
  padding: 60px 0;
  background-color: white;
}

.history-card {
  border: none;
}

.history-card h3 {
  font-size: 18px;
  margin: 0 0 12px;
  color: #303133;
}

.history-card p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

/* 团队部分 */
.team-section {
  padding: 60px 0;
  background-color: #f9fafb;
}

.team-card {
  text-align: center;
  padding: 30px 20px;
  margin-bottom: 24px;
  height: 100%;
  border: none;
}

.team-avatar {
  margin-bottom: 20px;
}

.team-name {
  font-size: 18px;
  margin: 0 0 8px;
  color: #303133;
}

.team-title {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  margin-bottom: 16px;
}

.team-description {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

/* 常见问题部分 */
.faq-section {
  padding: 60px 0;
  background-color: white;
}

/* 联系部分 */
.contact-section {
  padding: 60px 0;
  background-color: #f9fafb;
}

.contact-info {
  margin-top: 30px;
}

.contact-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 16px;
  color: #606266;
}

.contact-item .el-icon {
  margin-right: 12px;
  font-size: 20px;
  color: #409EFF;
}

.map-container {
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
}

.map-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }
  
  .page-header h1 {
    font-size: 28px;
  }
  
  .intro-content {
    padding-right: 0;
    margin-bottom: 30px;
  }
  
  .intro-image {
    min-height: 200px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .map-container {
    height: 300px;
    margin-top: 30px;
  }
}
</style>
