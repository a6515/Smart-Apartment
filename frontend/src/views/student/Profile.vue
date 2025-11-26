<template>
  <div class="student-profile">
    <!-- 个人信息卡片 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button v-if="!isEditing" type="primary" size="small" @click="startEdit">编辑</el-button>
          <div v-else>
            <el-button type="success" size="small" @click="saveUserInfo" :loading="saveLoading">保存</el-button>
            <el-button size="small" @click="cancelEdit">取消</el-button>
          </div>
        </div>
      </template>

      <!-- 查看模式 -->
      <div v-if="!isEditing">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ userInfo.realName }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ userInfo.studentNumber }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ userInfo.gender === 1 ? '男' : '女' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 编辑模式 -->
      <div v-else>
        <el-form :model="userInfo" :rules="formRules" ref="formRef" label-width="80px" status-icon>
          <!-- 不可编辑字段 -->
          <el-form-item label="用户名">
            <el-input v-model="userInfo.username" disabled></el-input>
            <div class="field-hint">用户名不可修改</div>
          </el-form-item>

          <el-form-item label="姓名">
            <el-input v-model="userInfo.realName" disabled></el-input>
            <div class="field-hint">姓名不可修改</div>
          </el-form-item>

          <el-form-item label="学号">
            <el-input v-model="userInfo.studentNumber" disabled></el-input>
            <div class="field-hint">学号不可修改</div>
          </el-form-item>

          <el-form-item label="性别">
            <el-input :value="userInfo.gender === 1 ? '男' : '女'" disabled></el-input>
            <div class="field-hint">性别不可修改</div>
          </el-form-item>

          <!-- 可编辑字段 -->
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="userInfo.phone" placeholder="请输入手机号" maxlength="11"></el-input>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userInfo.email" placeholder="请输入邮箱"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <span>修改密码</span>
      </template>
      
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="pwdLoading">修改密码</el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, changePassword } from '@/api/user'
import { logout } from '@/api/auth'
import { useRouter } from 'vue-router'

// 获取路由实例，用于页面跳转
const router = useRouter()

// 清除Element Plus消息，防止重复显示
ElMessage.closeAll()

// 用户数据
const userStore = useUserStore()
const formRef = ref(null)
const passwordFormRef = ref(null)
const isEditing = ref(false)
const saveLoading = ref(false)
const pwdLoading = ref(false)

// 用户信息
const userInfo = reactive({
  id: '',
  username: '',
  realName: '',
  studentNumber: '',
  phone: '',
  email: '',
  gender: null
})

// 保存原始数据，用于取消编辑时还原
const originalInfo = ref({})

// 表单验证规则
const formRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 开始编辑
const startEdit = () => {
  // 备份原始数据以便取消时恢复
  originalInfo.value = JSON.parse(JSON.stringify(userInfo))
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  // 恢复原始数据
  Object.assign(userInfo, originalInfo.value)
  isEditing.value = false
  ElMessage.info('已取消编辑')
}

// 保存用户信息
const saveUserInfo = async () => {
  if (!formRef.value) return
  
  try {
    // 表单验证
    await formRef.value.validate()
    
    saveLoading.value = true
    
    // 清除可能存在的消息
    ElMessage.closeAll()
    
    // 只发送可编辑字段
    const updateData = {
      id: userInfo.id,
      phone: userInfo.phone,
      email: userInfo.email
    }
    
    await updateUserInfo(updateData)
    
    // 更新本地存储
    userStore.setUserInfo({
      ...userStore.userInfo,
      phone: userInfo.phone,
      email: userInfo.email
    })
    
    isEditing.value = false
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    // 清除可能存在的消息
    ElMessage.closeAll()
    
    console.error('保存失败:', error)
    
    // 格式化错误信息
    const errorMsg = error.response?.data?.message || error.message || '保存失败，请检查表单'
    
    ElMessage({
      message: errorMsg,
      type: 'error',
      duration: 3000,
      showClose: true,
      grouping: true
    })
  } finally {
    saveLoading.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    // 表单验证
    await passwordFormRef.value.validate()
    
    pwdLoading.value = true
    
    // 先清除所有消息
    ElMessage.closeAll()
    
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    ElMessage.success('密码修改成功')
    
    // 修改密码成功后，调用登出API并清除本地信息
    try {
      // 调用后端登出接口，传递用户ID
      await logout(userStore.userInfo.id)
    } catch (logoutErr) {
      console.error('登出API调用失败:', logoutErr)
      // 即使API调用失败，我们也要继续清除本地登录信息
    }
    
    // 清除本地登录状态
    userStore.logout()
    
    // 使用setTimeout确保消息能被用户看到
    setTimeout(() => {
      // 显示提示并跳转到首页
      ElMessage({
        type: 'warning',
        message: '密码已更改，请重新登录',
        duration: 5000,
        showClose: true
      })
      
      // 重定向到首页
      router.push('/')
    }, 1000)
  } catch (error) {
    // 先清除所有消息
    ElMessage.closeAll()
    
    console.error('密码修改失败:', error)
    
    // 输出更详细的错误信息以便调试
    if (error.response) {
      console.log('错误响应数据:', error.response.data)
    }
    
    // 只显示一个错误信息
    const errorMsg = error.response?.data?.message || '密码错误或修改失败'
    
    ElMessage({
      message: errorMsg,
      type: 'error',
      duration: 3000,
      showClose: true,
      grouping: true
    })
  } finally {
    pwdLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

// 初始化用户信息
const initUserInfo = () => {
  // 确保userStore.userInfo存在且有数据
  if (userStore.userInfo && userStore.userInfo.id) {
    Object.assign(userInfo, userStore.userInfo)
  } else {
    console.warn('用户信息不存在，可能用户尚未登录')
  }
}

// 组件挂载时初始化
onMounted(() => {
  console.log('Profile页面加载')
  
  // 清除所有可能存在的消息提示
  ElMessage.closeAll()
  
  // 初始化用户信息
  initUserInfo()
})
</script>

<style scoped>
.student-profile {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.field-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
