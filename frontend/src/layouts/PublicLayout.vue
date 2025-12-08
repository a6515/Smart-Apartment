<template>
  <div class="public-layout">
    <!-- 移动端遮罩层（放在header外面） -->
    <div class="mobile-overlay" :class="{ show: mobileNavVisible }" @click="closeMobileNav"></div>
    
    <header class="header">
      <div class="container">
        <div class="logo">
          <el-icon :size="28"><OfficeBuilding /></el-icon>
          <span>智慧公寓管理系统</span>
        </div>
        
        <!-- 移动端汉堡菜单按钮 -->
        <button class="mobile-nav-toggle" :class="{ active: mobileNavVisible }" @click="toggleMobileNav">
          <div class="hamburger">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </button>
        
        <nav class="nav" :class="{ 'mobile-nav-show': mobileNavVisible }">
          <router-link to="/" class="nav-item home-link" @click="closeMobileNav">
            <el-icon><HomeFilled /></el-icon>首页
          </router-link>
          <router-link to="/about" class="nav-item" @click="closeMobileNav">
            <el-icon><InfoFilled /></el-icon>关于我们
          </router-link>
          <router-link to="/rooms" class="nav-item" @click="closeMobileNav">
            <el-icon><House /></el-icon>房源信息
          </router-link>
          <router-link to="/news" class="nav-item" @click="closeMobileNav">
            <el-icon><Document /></el-icon>新闻动态
          </router-link>
          <router-link to="/contact" class="nav-item" @click="closeMobileNav">
            <el-icon><Phone /></el-icon>联系我们
          </router-link>

          <div class="notification-wrapper" v-if="isLoggedIn">
            <WebSocketNotification />
          </div>

          <router-link v-if="!isLoggedIn" to="/student/login" class="login-btn">
            <el-icon><User /></el-icon> 登录
          </router-link>

          <el-dropdown v-else @command="handleUserCommand" trigger="click">
            <div class="avatar-container">
              <el-avatar :size="36" :src="userInfo.avatar || defaultAvatar">{{ userInfo.realName?.slice(0, 1) || 'U' }}</el-avatar>
              <div class="user-info">
                <span class="user-name" :title="userInfo.realName || userInfo.username">{{ userInfo.realName || userInfo.username || '用户' }}</span>
                <span class="user-role">{{ userRoleText }}</span>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><UserFilled /></el-icon>
                  {{ isAdminUser ? '返回管理后台' : '个人信息详情' }}
                </el-dropdown-item>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </nav>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

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
import { computed, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { logout } from '@/api/auth'
import WebSocketNotification from '@/components/WebSocketNotification.vue'
import {
  OfficeBuilding,
  Phone,
  Message,
  Location,
  HomeFilled,
  InfoFilled,
  House,
  Document,
  User,
  UserFilled,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 移动端导航菜单状态
const mobileNavVisible = ref(false)

// 切换移动端导航菜单
const toggleMobileNav = () => {
  mobileNavVisible.value = !mobileNavVisible.value
}

// 关闭移动端导航菜单
const closeMobileNav = () => {
  mobileNavVisible.value = false
}

// 监听路由变化，关闭菜单
router.afterEach(() => {
  closeMobileNav()
})

// 判断用户是否登录
const isLoggedIn = computed(() => !!userStore.token && !!userStore.userInfo?.id)

// 判断是否是管理员或宿管用户
const isAdminUser = computed(() => {
  const userType = userStore.userInfo?.userType
  return userType === 1 || userType === 2
})

// 用户角色文本
const userRoleText = computed(() => {
  const userType = userStore.userInfo?.userType
  if (userType === 1) return '系统管理员'
  if (userType === 2) return '宿舍管理员'
  if (userType === 3) return '学生用户'
  return '访客'
})

const userInfo = computed(() => userStore.userInfo || {})
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const handleUserCommand = async (command) => {
  if (command === 'profile') {
    const userType = userStore.userInfo?.userType
    if (userType === 3) {
      router.push('/student/profile')
    } else if ([1, 2].includes(userType)) {
      router.push('/admin/dashboard')
    } else {
      router.push('/')
    }
  } else if (command === 'logout') {
    try {
      if (userStore.userInfo && userStore.userInfo.id) {
        await logout(userStore.userInfo.id)
      }
    } catch (error) {
      console.error('登出API调用失败:', error)
    }
    userStore.logout()
    ElMessage.success('退出成功')
    if (router.currentRoute.value.meta.requiresAuth) {
      router.push('/')
    } else {
      router.go(0)
    }
  }
}
</script>

<style scoped>
/* 整体布局和变量定义 */
.public-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  --nav-text-color: #505256;
  --nav-hover-bg: rgba(0, 0, 0, 0.04);
  --nav-active-color: #4f79db;
  --nav-active-bg: rgba(79, 121, 219, 0.12);
  --header-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* ==================================================== */
/* 核心修复：Header 布局重构 (解决遮挡问题的关键) */
/* ==================================================== */
.header {
  /* 1. 改为 fixed 固定定位，直接相对于浏览器窗口，层级最高 */
  position: fixed; 
  top: 0;
  left: 0;
  width: 100%;
  
  /* 2. 移除 backdrop-filter (这会导致 overflow 失效)，改用纯白背景 */
  background: #ffffff; 
  
  z-index: 1000; /* 只要比内容区高就行 */
  box-shadow: var(--header-shadow);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  height: 64px;
  display: flex;
  align-items: center;
  
  /* 3. 彻底放开溢出限制 */
  overflow: visible !important;
}

.container {
  width: 92%;
  max-width: 1320px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  overflow: visible !important; /* 确保不裁剪 */
}

/* Logo 样式 */
.logo {
  display: flex;
  align-items: center;
  font-size: 1.35rem;
  font-weight: 700;
  color: var(--nav-active-color);
  text-decoration: none;
}

.logo span {
  margin-left: 0.6rem;
  letter-spacing: 0.8px;
}

/* 导航区域样式 */
.nav {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  height: 100%;
  /* 开启定位，确保子元素层级生效 */
  position: relative;
  z-index: 1001; 
  overflow: visible !important;
}

/* 导航链接通用样式 */
.nav-item {
  color: var(--nav-text-color);
  text-decoration: none;
  padding: 0 1rem;
  height: 42px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  font-size: 0.95rem;
  border-radius: 6px;
  transition: all 0.25s ease;
  position: relative;
}

.nav-item:hover {
  color: var(--nav-active-color);
  background-color: var(--nav-hover-bg);
}

/* 修复首页高亮逻辑 */
.nav-item.router-link-active:not(.home-link) {
  color: var(--nav-active-color) !important;
  background-color: var(--nav-active-bg);
  font-weight: 600;
}

.nav-item.home-link.router-link-exact-active {
  color: var(--nav-active-color) !important;
  background-color: var(--nav-active-bg);
  font-weight: 600;
}

/* 登录按钮样式 */
.login-btn {
  background-color: var(--nav-active-color);
  color: #fff;
  padding: 0.5rem 1.2rem;
  border-radius: 6px;
  text-decoration: none;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  font-size: 0.95rem;
  margin-left: 0.5rem;
  box-shadow: 0 2px 6px rgba(79, 121, 219, 0.25);
}

.login-btn:hover {
  background-color: #3d67c7;
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(79, 121, 219, 0.35);
}

/* 用户头像区域优化 */
.avatar-container {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 8px 4px 4px;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  margin-left: 0.5rem;
  background-color: rgba(0, 0, 0, 0.03);
}

.avatar-container:hover {
  background-color: #fff;
  border-color: var(--nav-active-bg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.user-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  line-height: 1.3;
  text-align: left;
}

.user-name {
  color: #2c3e50;
  font-weight: 600;
  font-size: 0.9rem;
  max-width: 110px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  color: #8590a6;
  font-size: 0.75rem;
  font-weight: normal;
  margin-top: 1px;
}

.dropdown-icon {
  color: #8590a6;
  font-size: 0.85rem;
  margin-left: 4px;
  transition: transform 0.3s;
}

.el-dropdown[aria-expanded="true"] .dropdown-icon {
  transform: rotate(180deg);
}

/* ==================================================== */
/* 修复消息通知包裹器 */
/* ==================================================== */
.notification-wrapper {
  margin: 0 8px;
  display: flex;
  align-items: center;
  height: 100%;
  position: relative;
  /* 确保这里层级足够高，且 overflow 为 visible */
  z-index: 5000 !important; 
  overflow: visible !important;
}

/* 主要内容区调整 */
.main-content {
  flex: 1;
  background-color: #f5f7fa;
  /* 关键：因为 Header 改为 fixed 脱离了文档流，这里必须加 padding 把它顶下来 */
  padding-top: 64px; 
  position: relative;
  z-index: 1;
}

.footer {
  background-color: #fff;
  padding: 2.5rem 0 1.5rem;
  border-top: 1px solid #eee;
  color: #606266;
}

.footer-content {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 3rem;
}

.footer-section h3 {
  color: #303133;
  font-size: 1.1rem;
  margin-bottom: 1.2rem;
  font-weight: 600;
}

.footer-section p {
  line-height: 1.8;
  font-size: 0.9rem;
}

.footer-section .links {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.footer-section .links a {
  color: #606266;
  text-decoration: none;
  transition: color 0.2s;
  font-size: 0.9rem;
  display: inline-block;
}

.footer-section .links a:hover {
  color: var(--nav-active-color);
}

.footer-section p .el-icon {
  margin-right: 8px;
  vertical-align: middle;
  color: #909399;
}

.copyright {
  margin-top: 3rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
  text-align: center;
  color: #909399;
  font-size: 0.85rem;
}

/* 全局提升弹窗层级 */
:global(.el-popper),
:global(.el-dropdown__popper),
:global(.el-notification),
:global(.el-tooltip__popper) {
  z-index: 9999 !important;
}
</style>