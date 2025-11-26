import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
  // 公共页面
  {
    path: '/',
    component: () => import('@/layouts/PublicLayout.vue'),
    children: [
      {
        path: '',
        name: 'PublicHome',
        component: () => import('@/views/public/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'admin/login',
        name: 'AdminLogin',
        component: () => import('@/views/public/AdminLogin.vue'),
        meta: { title: '管理员登录' }
      },
      {
        path: 'student/login',
        name: 'StudentLogin',
        component: () => import('@/views/public/StudentLogin.vue'),
        meta: { title: '学生登录' }
      },
      // 其他公共页面
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/public/About.vue'),
        meta: { title: '关于我们' }
      },
      {
        path: 'rooms',
        name: 'Rooms',
        component: () => import('@/views/public/Rooms.vue'),
        meta: { title: '房源信息' }
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/views/public/News.vue'),
        meta: { title: '新闻动态' }
      },
      {
        path: 'contact',
        name: 'Contact',
        component: () => import('@/views/public/Contact.vue'),
        meta: { title: '联系我们' }
      }
    ]
  },

  // 管理员/宿管后台
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, roles: [1, 2] },
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { title: '首页概览' } },
      { path: 'building', name: 'Building', component: () => import('@/views/admin/Building.vue'), meta: { title: '楼宇管理' } },
      { path: 'room', name: 'Room', component: () => import('@/views/admin/Room.vue'), meta: { title: '房间管理' } },
      { path: 'checkin', name: 'CheckIn', component: () => import('@/views/admin/CheckIn.vue'), meta: { title: '入住管理' } },
      { path: 'checkout', name: 'AdminCheckout', component: () => import('@/views/admin/Checkout.vue'), meta: { title: '退宿管理' } },
      { path: 'transfer', name: 'AdminTransfer', component: () => import('@/views/admin/Transfer.vue'), meta: { title: '换宿管理' } },
      { path: 'repair', name: 'Repair', component: () => import('@/views/admin/Repair.vue'), meta: { title: '报修管理' } },
      { path: 'user', name: 'User', component: () => import('@/views/admin/User.vue'), meta: { title: '用户管理', roles: [1] } }
    ]
  },

  // 学生端
  {
    path: '/student',
    component: () => import('@/layouts/StudentLayout.vue'),
    redirect: '/student/home',
    meta: { requiresAuth: true, roles: [3] },
    children: [
      { path: 'home', name: 'StudentHome', component: () => import('@/views/student/Home.vue'), meta: { title: '学生首页' } },
      { path: 'room', name: 'StudentRoom', component: () => import('@/views/student/Room.vue'), meta: { title: '我的房间' } },
      { path: 'checkin', name: 'StudentCheckIn', component: () => import('@/views/student/CheckIn.vue'), meta: { title: '入住申请' } },
      { path: 'checkout', name: 'StudentCheckout', component: () => import('@/views/student/Checkout.vue'), meta: { title: '退宿申请' } },
      { path: 'transfer', name: 'StudentTransfer', component: () => import('@/views/student/Transfer.vue'), meta: { title: '换宿申请' } },
      { path: 'repair', name: 'StudentRepair', component: () => import('@/views/student/Repair.vue'), meta: { title: '报修服务' } },
      { path: 'profile', name: 'StudentProfile', component: () => import('@/views/student/Profile.vue'), meta: { title: '个人中心' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 如果用户已登录且试图访问登录页，重定向到首页或对应的后台
  if (userStore.token && userStore.userInfo?.userType) {
    const userType = userStore.userInfo.userType
    
    if (to.path === '/admin/login' || to.path === '/student/login') {
      if (userType === 3) { // 学生
        next('/')  // 学生重定向到公共首页
      } else if ([1, 2].includes(userType)) { // 管理员或宿管
        next('/admin/dashboard')  // 管理员重定向到管理后台
      } else {
        next('/')
      }
      return
    }
  }
  
  if (to.meta.requiresAuth) {
    // 检查是否有token
    if (!userStore.token) {
      ElMessage.warning('请先登录')
      // 根据要访问的路径，跳转到对应的登录页
      if (to.path.startsWith('/admin')) {
        next('/admin/login')
      } else if (to.path.startsWith('/student')) {
        next('/student/login')
      } else {
        next('/student/login') // 默认学生登录
      }
      return
    }
    
    // 检查用户信息是否完整
    const userType = userStore.userInfo?.userType
    if (!userType) {
      // 用户信息不完整，清除token并重新登录
      ElMessage.warning('登录信息已过期，请重新登录')
      userStore.logout()
      if (to.path.startsWith('/admin')) {
        next('/admin/login')
      } else if (to.path.startsWith('/student')) {
        next('/student/login')
      } else {
        next('/student/login')
      }
      return
    }
    
    // 权限检查
    if (to.meta.roles && !to.meta.roles.includes(userType)) {
      ElMessage.error('权限不足')
      next(false)
      return
    }
  }
  
  next()
})

export default router
