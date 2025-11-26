<template>
  <div class="contact-page">
    <section class="page-header">
      <div class="container">
        <h1>联系我们</h1>
        <p>有任何问题或建议，随时与我们取得联系</p>
      </div>
    </section>

    <section class="contact-section">
      <div class="container">
        <el-row :gutter="40">
          <el-col :lg="8" :md="24">
            <div class="contact-info-container">
              <h2 class="contact-title">联系方式</h2>
              
              <div class="contact-card">
                <div class="contact-item">
                  <el-icon><Location /></el-icon>
                  <div class="contact-text">
                    <h3>地址</h3>
                    <p>某某大学校园内明德公寓管理中心</p>
                  </div>
                </div>
                
                <div class="contact-item">
                  <el-icon><Phone /></el-icon>
                  <div class="contact-text">
                    <h3>电话</h3>
                    <p>400-123-4567</p>
                  </div>
                </div>
                
                <div class="contact-item">
                  <el-icon><Message /></el-icon>
                  <div class="contact-text">
                    <h3>邮箱</h3>
                    <p>info@apartment.com</p>
                  </div>
                </div>
                
                <div class="contact-item">
                  <el-icon><Timer /></el-icon>
                  <div class="contact-text">
                    <h3>工作时间</h3>
                    <p>周一至周日 8:00-22:00</p>
                  </div>
                </div>
              </div>
              
              <div class="social-media">
                <h3>关注我们</h3>
                <div class="social-icons">
                  <a href="#" class="social-icon">
                    <el-icon><Message /></el-icon>
                  </a>
                  <a href="#" class="social-icon">
                    <el-icon><ChatDotRound /></el-icon>
                  </a>
                  <a href="#" class="social-icon">
                    <el-icon><Promotion /></el-icon>
                  </a>
                </div>
              </div>
            </div>
          </el-col>
          
          <el-col :lg="16" :md="24">
            <div class="contact-form-container">
              <h2 class="contact-title">发送消息</h2>
              <p class="contact-subtitle">如有任何疑问或建议，请填写下面的表单，我们会尽快回复您。</p>
              
              <el-form 
                ref="contactFormRef"
                :model="contactForm"
                :rules="contactRules"
                label-position="top"
                status-icon
              >
                <el-row :gutter="20">
                  <el-col :md="12" :sm="24">
                    <el-form-item label="姓名" prop="name">
                      <el-input 
                        v-model="contactForm.name"
                        placeholder="请输入您的姓名"
                        clearable
                      />
                    </el-form-item>
                  </el-col>
                  
                  <el-col :md="12" :sm="24">
                    <el-form-item label="邮箱" prop="email">
                      <el-input 
                        v-model="contactForm.email"
                        placeholder="请输入您的邮箱"
                        clearable
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-form-item label="电话" prop="phone">
                  <el-input 
                    v-model="contactForm.phone"
                    placeholder="请输入您的电话"
                    clearable
                  />
                </el-form-item>
                
                <el-form-item label="主题" prop="subject">
                  <el-select 
                    v-model="contactForm.subject"
                    placeholder="请选择咨询主题"
                    style="width: 100%"
                    clearable
                  >
                    <el-option label="入住咨询" value="入住咨询" />
                    <el-option label="退宿咨询" value="退宿咨询" />
                    <el-option label="报修服务" value="报修服务" />
                    <el-option label="投诉建议" value="投诉建议" />
                    <el-option label="其他问题" value="其他问题" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="消息内容" prop="message">
                  <el-input 
                    v-model="contactForm.message"
                    type="textarea"
                    :rows="5"
                    placeholder="请输入您的消息内容"
                    resize="none"
                  />
                </el-form-item>
                
                <el-form-item>
                  <el-button 
                    type="primary" 
                    @click="submitForm"
                    :loading="loading"
                    style="width: 100%"
                    size="large"
                  >
                    发送消息
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>
    
    <section class="map-section">
      <div class="map-container">
        <img src="https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/pin-s+555555(120.12,30.28)/120.12,30.28,15,0/1200x400?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4M29iazA2Z2gycXA4N2pmbDZmangifQ.-g_vE53SD2WrJ6tFX7QHmA" alt="地图位置" />
      </div>
    </section>
    
    <section class="faq-section">
      <div class="container">
        <h2 class="section-title">常见问题</h2>
        <p class="section-subtitle">以下是一些常见问题的解答，希望能帮到您</p>
        
        <el-row :gutter="30">
          <el-col :md="8" :sm="12" :xs="24" v-for="(faq, index) in faqs" :key="index">
            <div class="faq-card">
              <h3>{{ faq.question }}</h3>
              <p>{{ faq.answer }}</p>
              <el-button text type="primary" @click="$router.push('/about')">了解更多</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Location, Phone, Message, Timer,
  ChatDotRound, Promotion
} from '@element-plus/icons-vue'

// 联系表单
const contactFormRef = ref(null)
const loading = ref(false)

const contactForm = reactive({
  name: '',
  email: '',
  phone: '',
  subject: '',
  message: ''
})

const contactRules = {
  name: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2到20个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入您的邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入您的电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  subject: [
    { required: true, message: '请选择咨询主题', trigger: 'change' }
  ],
  message: [
    { required: true, message: '请输入您的消息内容', trigger: 'blur' },
    { min: 10, max: 500, message: '消息内容在10到500个字符之间', trigger: 'blur' }
  ]
}

// 提交表单
const submitForm = async () => {
  if (!contactFormRef.value) return
  
  try {
    await contactFormRef.value.validate()
    
    loading.value = true
    
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('消息发送成功，我们会尽快回复您！')
    
    // 重置表单
    contactFormRef.value.resetFields()
    
  } catch (error) {
    console.error('表单验证失败', error)
    ElMessage.error('请完善表单信息')
  } finally {
    loading.value = false
  }
}

// 常见问题
const faqs = ref([
  {
    question: '如何申请入住公寓？',
    answer: '登录智慧公寓管理系统，进入"入住申请"页面，填写并提交申请表，等待审核通过后办理入住手续。'
  },
  {
    question: '公寓有哪些类型的房间？',
    answer: '我们提供单人间、双人间、四人间和六人间等多种类型，满足不同学生的需求。'
  },
  {
    question: '如何申请维修服务？',
    answer: '登录智慧公寓管理系统，进入"报修服务"页面，填写报修信息并提交，工作人员会尽快处理。'
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

/* 联系部分 */
.contact-section {
  padding: 60px 0;
  background-color: white;
}

.contact-title {
  font-size: 24px;
  margin: 0 0 24px;
  color: #303133;
}

.contact-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0 0 24px;
}

/* 联系信息卡片 */
.contact-info-container {
  height: 100%;
}

.contact-card {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
}

.contact-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.contact-item:last-child {
  margin-bottom: 0;
}

.contact-item .el-icon {
  font-size: 24px;
  color: #409EFF;
  margin-right: 16px;
  margin-top: 4px;
}

.contact-text h3 {
  font-size: 16px;
  margin: 0 0 8px;
  color: #303133;
}

.contact-text p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

/* 社交媒体 */
.social-media {
  margin-top: 30px;
}

.social-media h3 {
  font-size: 16px;
  margin: 0 0 16px;
  color: #303133;
}

.social-icons {
  display: flex;
  gap: 16px;
}

.social-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: #f0f2f5;
  border-radius: 50%;
  color: #606266;
  transition: all 0.3s;
}

.social-icon:hover {
  background-color: #409EFF;
  color: white;
}

/* 地图部分 */
.map-section {
  padding: 0;
}

.map-container {
  height: 400px;
  width: 100%;
  overflow: hidden;
}

.map-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* FAQ部分 */
.faq-section {
  padding: 60px 0;
  background-color: #f9fafb;
}

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

.faq-card {
  background-color: white;
  border-radius: 8px;
  padding: 24px;
  height: calc(100% - 24px);
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.faq-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.faq-card h3 {
  font-size: 18px;
  margin: 0 0 16px;
  color: #303133;
}

.faq-card p {
  font-size: 14px;
  color: #606266;
  margin: 0 0 16px;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }
  
  .page-header h1 {
    font-size: 28px;
  }
  
  .contact-section {
    padding: 40px 0;
  }
  
  .contact-form-container {
    margin-top: 40px;
  }
  
  .map-container {
    height: 300px;
  }
}
</style>
