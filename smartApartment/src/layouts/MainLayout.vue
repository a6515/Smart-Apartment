<template>
  <el-container class="main-container">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h3>智慧公寓</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
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
        <el-menu-item index="/admin/checkin">
          <el-icon><User /></el-icon>
          <span>入住管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/repair">
          <el-icon><Tools /></el-icon>
          <span>报修管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/user" v-if="userInfo.userType === 1">
          <el-icon><Setting /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32">{{ userStore.userInfo.realName?.[0] }}</el-avatar>
              <span style="margin-left: 8px">{{ userStore.userInfo.realName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '')

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.logout()
      router.push('/login')
      ElMessage.success('退出成功')
    } catch (error) {
      // 用户取消
    }
  }
}
</script>

<style scoped>
.main-container {
  height: 100vh;
}

.sidebar {
  background: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2c3e50;
  color: white;
}

.logo h3 {
  font-size: 18px;
  margin: 0;
}

.header {
  background: white;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 500;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.main-content {
  background: #f5f5f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
