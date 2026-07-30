import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
      { path: 'products', name: 'Products', component: () => import('@/views/Products.vue') },
      { path: 'orders', name: 'Orders', component: () => import('@/views/Orders.vue') },
      { path: 'inventory', name: 'Inventory', component: () => import('@/views/Inventory.vue') },
      { path: 'tasks', name: 'Tasks', component: () => import('@/views/Tasks.vue') },
      { path: 'alerts', name: 'Alerts', component: () => import('@/views/Alerts.vue') },
      { path: 'audit', name: 'Audit', component: () => import('@/views/Audit.vue') },
      { path: 'users', name: 'Users', component: () => import('@/views/Users.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    if (token) return next('/dashboard')
    return next()
  }
  if (to.matched.some(r => r.meta.requiresAuth) && !token) {
    return next('/login')
  }
  next()
})

export default router
