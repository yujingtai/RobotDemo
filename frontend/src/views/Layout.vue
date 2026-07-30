<template>
  <el-container style="height:100vh">
    <el-aside width="220px" style="background:#304156">
      <div class="logo">主题邮局后台</div>
      <el-menu
        :default-active="route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard"><el-icon><DataLine /></el-icon>&nbsp;首页概览</el-menu-item>
        <el-menu-item index="/products"><el-icon><Goods /></el-icon>&nbsp;商品管理</el-menu-item>
        <el-menu-item index="/orders"><el-icon><Document /></el-icon>&nbsp;订单管理</el-menu-item>
        <el-menu-item index="/inventory"><el-icon><Box /></el-icon>&nbsp;库存管理</el-menu-item>
        <el-menu-item index="/tasks"><el-icon><Monitor /></el-icon>&nbsp;任务监控</el-menu-item>
        <el-menu-item index="/alerts"><el-icon><Bell /></el-icon>&nbsp;告警管理</el-menu-item>
        <el-menu-item index="/audit"><el-icon><List /></el-icon>&nbsp;审计日志</el-menu-item>
        <el-menu-item v-if="isAdmin" index="/users"><el-icon><User /></el-icon>&nbsp;用户管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <span class="user-info">{{ user?.username || '-' }} ({{ user?.role || '-' }})</span>
        <el-button text @click="logout">退出登录</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const isAdmin = computed(() => user.role === 'ADMIN')

const logout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.logo { color: #fff; text-align: center; padding: 16px; font-size: 16px; font-weight: bold; border-bottom: 1px solid #4a5b6d; }
.topbar { background:#fff; border-bottom:1px solid #e6e6e6; display:flex; align-items:center; justify-content:flex-end; gap:12px; }
.user-info { color: #606266; }
.el-main { background: #f0f2f5; }
</style>
