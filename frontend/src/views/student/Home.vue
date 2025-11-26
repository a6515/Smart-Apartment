<template>
  <div class="student-home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <h2>欢迎，{{ userStore.userInfo.realName || '同学' }}！</h2>
          <p style="margin-top: 10px; color: #909399;">这是学生端首页</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card class="quick-link" @click="$router.push('/student/room')">
          <el-icon :size="40" color="#409EFF"><House /></el-icon>
          <div class="link-title">我的房间</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="quick-link" @click="$router.push('/student/checkin')">
          <el-icon :size="40" color="#67C23A"><DocumentAdd /></el-icon>
          <div class="link-title">入住申请</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="quick-link" @click="$router.push('/student/checkout')">
          <el-icon :size="40" color="#F56C6C"><SwitchButton /></el-icon>
          <div class="link-title">退宿申请</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="quick-link" @click="$router.push('/student/transfer')">
          <el-icon :size="40" color="#9C27B0"><Switch /></el-icon>
          <div class="link-title">换宿申请</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card class="quick-link" @click="$router.push('/student/repair')">
          <el-icon :size="40" color="#E6A23C"><Tools /></el-icon>
          <div class="link-title">报修服务</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="quick-link" @click="$router.push('/student/profile')">
          <el-icon :size="40" color="#607D8B"><User /></el-icon>
          <div class="link-title">个人中心</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>最近动态</span>
          </template>
          <el-empty description="暂无动态" :image-size="150" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.student-home {
  padding: 0;
}

.quick-link {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-link:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.link-title {
  margin-top: 10px;
  font-size: 16px;
  font-weight: 500;
}
</style>
