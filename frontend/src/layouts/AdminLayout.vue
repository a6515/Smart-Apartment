<template>
  <el-container class="layout-container">
    <!-- 移动端菜单按钮 -->
    <button class="mobile-menu-btn" @click="toggleMobileMenu">
      <el-icon><Menu /></el-icon>
    </button>
    
    <!-- 移动端遮罩层 -->
    <div class="mobile-overlay" :class="{ show: mobileMenuVisible }" @click="closeMobileMenu"></div>
    
    <el-aside width="200px" class="sidebar" :class="{ 'mobile-show': mobileMenuVisible }">
      <div class="logo">
        <h3>智慧公寓</h3>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/building">
          <el-icon><OfficeBuilding /></el-icon>
          <span>楼宇管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/room">
          <el-icon><House /></el-icon>
          <span>房间管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/announcement">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/checkin">
          <el-icon><User /></el-icon>
          <span>入住管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/checkout">
          <el-icon><SwitchButton /></el-icon>
          <span>退宿管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/transfer">
          <el-icon><Switch /></el-icon>
          <span>换宿管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/repair">
          <el-icon><Tools /></el-icon>
          <span>报修管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/user" v-if="userStore.userInfo.userType === 1">
          <el-icon><Setting /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
      
      <!-- 通知组件（移动端显示在侧边栏） -->
      <div class="notification-wrapper">
        <WebSocketNotification />
      </div>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ $route.meta.title }}</span>
        </div>
        <div class="header-right">
          <!-- PC端通知图标 -->
          <div class="header-notification">
            <WebSocketNotification />
          </div>
          
          <el-dropdown @command="handleCommand" trigger="hover">
            <span class="user-info">
              <el-icon><Avatar /></el-icon>
              {{ userStore.userInfo.realName || userStore.userInfo.username }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">返回首页</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { logout } from '@/api/auth'
import WebSocketNotification from '@/components/WebSocketNotification.vue'

const router = useRouter()
const userStore = useUserStore()
const mobileMenuVisible = ref(false)

// 切换移动端菜单
const toggleMobileMenu = () => {
  mobileMenuVisible.value = !mobileMenuVisible.value
}

// 关闭移动端菜单
const closeMobileMenu = () => {
  mobileMenuVisible.value = false
}

// 监听路由变化，关闭菜单
router.afterEach(() => {
  closeMobileMenu()
})

const handleCommand = async (command) => {
  if (command === 'home') {
    // 跳转到公共首页
    router.push('/')
  } else if (command === 'logout') {
    try {
      // 调用后端登出API
      if (userStore.userInfo && userStore.userInfo.id) {
        await logout(userStore.userInfo.id)
      }
    } catch (error) {
      console.error('登出API调用失败:', error)
      // 即使API调用失败，我们也继续清除本地登录信息
    }
    
    // 清除本地登录状态
    userStore.logout()
    ElMessage.success('退出成功')
    router.push('/')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  border-bottom: 1px solid #1f2d3d;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-notification {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 500;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 侧边栏通知（移动端显示） */
.notification-wrapper {
  display: none;
  padding: 15px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

/* PC端显示header中的通知，隐藏侧边栏的 */
@media screen and (min-width: 769px) {
  .sidebar .notification-wrapper {
    display: none;
  }
}

/* 移动端显示侧边栏中的通知，隐藏header的 */
@media screen and (max-width: 768px) {
  .sidebar .notification-wrapper {
    display: block;
  }
  
  .header-notification {
    display: none;
  }
}
</style>
