<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h3>学生端</h3>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#409EFF"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/student/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/student/room">
          <el-icon><House /></el-icon>
          <span>我的房间</span>
        </el-menu-item>
        <el-menu-item index="/student/checkin">
          <el-icon><DocumentAdd /></el-icon>
          <span>入住申请</span>
        </el-menu-item>
        <el-menu-item index="/student/checkout">
          <el-icon><SwitchButton /></el-icon>
          <span>退宿申请</span>
        </el-menu-item>
        <el-menu-item index="/student/transfer">
          <el-icon><Switch /></el-icon>
          <span>换宿申请</span>
        </el-menu-item>
        <el-menu-item index="/student/repair">
          <el-icon><Tools /></el-icon>
          <span>报修服务</span>
        </el-menu-item>
        <el-menu-item index="/student/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ $route.meta.title }}</span>
        </div>
        <div class="header-right">
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
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { logout } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

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
  background-color: #409EFF;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
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

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
