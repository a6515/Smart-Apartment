<template>
  <div class="public-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="container">
        <div class="logo">
          <el-icon :size="28"><OfficeBuilding /></el-icon>
          <span>智慧公寓管理系统</span>
        </div>
        <nav class="nav">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/about" class="nav-item">关于我们</router-link>
          <router-link to="/rooms" class="nav-item">房源信息</router-link>
          <router-link to="/news" class="nav-item">新闻动态</router-link>
          <router-link to="/contact" class="nav-item">联系我们</router-link>
          
          <!-- 未登录时显示登录按钮 -->
          <router-link v-if="!isLoggedIn" to="/student/login" class="login-btn">登录</router-link>
          
          <!-- 已登录时显示用户头像和下拉菜单 -->
          <el-dropdown v-else @command="handleUserCommand" trigger="hover">
            <div class="avatar-container">
              <el-avatar :size="32" :src="userInfo.avatar || defaultAvatar">{{ userInfo.realName?.slice(0, 1) || 'U' }}</el-avatar>
              <span class="user-name">{{ userInfo.realName || userInfo.username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息详情</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </nav>
      </div>
    </header>

    <!-- 主要内容区 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>关于我们</h3>
            <p>智慧公寓管理系统致力于为学生提供舒适、安全、便捷的住宿环境。</p>
          </div>
          <div class="footer-section">
            <h3>快速链接</h3>
            <div class="links">
              <router-link to="/about">关于我们</router-link>
              <router-link to="/rooms">房源信息</router-link>
              <router-link to="/news">新闻动态</router-link>
              <router-link to="/contact">联系我们</router-link>
            </div>
          </div>
          <div class="footer-section">
            <h3>联系方式</h3>
            <p><el-icon><Phone /></el-icon> 电话：400-123-4567</p>
            <p><el-icon><Message /></el-icon> 邮箱：info@apartment.com</p>
            <p><el-icon><Location /></el-icon> 地址：某某大学校园内</p>
          </div>
        </div>
        <div class="copyright">
          <p>&copy; 2025 智慧公寓管理系统. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { logout } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

// 判断用户是否登录
const isLoggedIn = computed(() => !!userStore.token && !!userStore.userInfo?.id)

// 获取用户信息
const userInfo = computed(() => userStore.userInfo || {})

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 处理登录按钮点击
const handleLogin = (command) => {
  if (command === 'student') {
    router.push('/student/login')
  } else if (command === 'admin') {
    router.push('/admin/login')
  }
}

// 处理用户头像下拉菜单
const handleUserCommand = async (command) => {
  if (command === 'profile') {
    // 跳转到学生后台的个人中心页面
    router.push('/student/profile')
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
    
    // 如果当前在需要登录的页面，跳转到首页
    if (router.currentRoute.value.meta.requiresAuth) {
      router.push('/')
    } else {
      // 如果已经在公共页面，则刷新当前页面
      router.go(0)
    }
  }
}
</script>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
  font-weight: bold;
  cursor: pointer;
}

.nav {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-item {
  color: white;
  text-decoration: none;
  font-size: 16px;
  transition: all 0.3s;
  padding: 5px 10px;
  border-radius: 4px;
}

.nav-item:hover,
.nav-item.router-link-active {
  background: rgba(255, 255, 255, 0.2);
}

.login-btn {
  color: white;
  cursor: pointer;
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s;
  text-decoration: none;
}

.login-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.avatar-container {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.2);
  transition: all 0.3s;
}

.avatar-container:hover {
  background: rgba(255, 255, 255, 0.3);
}

.user-name {
  color: white;
  font-size: 14px;
  max-width: 80px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.main-content {
  flex: 1;
  background: #f5f7fa;
}

.footer {
  background: #2c3e50;
  color: white;
  padding: 40px 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section h3 {
  margin-bottom: 15px;
  font-size: 18px;
}

.footer-section p {
  margin: 8px 0;
  color: #bdc3c7;
  display: flex;
  align-items: center;
  gap: 8px;
}

.links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.links a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s;
}

.links a:hover {
  color: white;
}

.copyright {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: #bdc3c7;
}
</style>
