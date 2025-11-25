import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  // 管理员/宿管后台
  {
    path: '/admin',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, roles: [1, 2] }, // 1-管理员, 2-宿管
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页概览', roles: [1, 2] }
      },
      {
        path: 'building',
        name: 'Building',
        component: () => import('@/views/Building.vue'),
        meta: { title: '楼宇管理', roles: [1, 2] }
      },
      {
        path: 'room',
        name: 'Room',
        component: () => import('@/views/Room.vue'),
        meta: { title: '房间管理', roles: [1, 2] }
      },
      {
        path: 'checkin',
        name: 'CheckIn',
        component: () => import('@/views/CheckIn.vue'),
        meta: { title: '入住管理', roles: [1, 2] }
      },
      {
        path: 'repair',
        name: 'Repair',
        component: () => import('@/views/Repair.vue'),
        meta: { title: '报修管理', roles: [1, 2] }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue'),
        meta: { title: '用户管理', roles: [1] } // 仅管理员
      }
    ]
  },
  // 学生端
  {
    path: '/student',
    component: () => import('@/layouts/StudentLayout.vue'),
    redirect: '/student/home',
    meta: { requiresAuth: true, roles: [3] }, // 3-学生
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('@/views/student/StudentHome.vue'),
        meta: { title: '学生首页', roles: [3] }
      },
      {
        path: 'room-info',
        name: 'StudentRoomInfo',
        component: () => import('@/views/student/RoomInfo.vue'),
        meta: { title: '我的房间', roles: [3] }
      },
      {
        path: 'checkin',
        name: 'StudentCheckIn',
        component: () => import('@/views/student/CheckIn.vue'),
        meta: { title: '入住申请', roles: [3] }
      },
      {
        path: 'repair',
        name: 'StudentRepair',
        component: () => import('@/views/student/Repair.vue'),
        meta: { title: '报修服务', roles: [3] }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/Profile.vue'),
        meta: { title: '个人中心', roles: [3] }
      }
    ]
  },
  // 根路径重定向
  {
    path: '/',
    redirect: (to) => {
      const userStore = useUserStore()
      const userType = userStore.userInfo?.userType
      if (userType === 3) {
        return '/student/home'
      } else if (userType === 1 || userType === 2) {
        return '/admin/dashboard'
      }
      return '/login'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 不需要认证的页面直接放行
  if (!to.meta.requiresAuth) {
    next()
    return
  }
  
  // 未登录跳转到登录页
  if (!userStore.token) {
    next('/login')
    return
  }
  
  // 检查权限
  const userType = userStore.userInfo?.userType
  const requiredRoles = to.meta.roles
  
  if (requiredRoles && !requiredRoles.includes(userType)) {
    ElMessage.error('权限不足，无法访问该页面')
    // 根据用户类型跳转到对应首页
    if (userType === 3) {
      next('/student/home')
    } else {
      next('/admin/dashboard')
    }
    return
  }
  
  next()
})

export default router
