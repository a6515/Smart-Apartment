<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
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
