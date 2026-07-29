<template>
  <div>
    <h3>首页概览</h3>
    <el-row :gutter="16">
      <el-col :span="6"><el-card><div class="stat"><h4>商品总数</h4><p>{{ stats.products }}</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat"><h4>今日订单</h4><p>{{ stats.orders }}</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat"><h4>运行任务</h4><p>{{ stats.tasks }}</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat"><h4>待处理告警</h4><p>{{ stats.alerts }}</p></div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
const stats = reactive({ products: 0, orders: 0, tasks: 0, alerts: 0 })
</script>

<style scoped>
.stat { text-align: center; }
.stat h4 { color: #999; margin-bottom: 8px; }
.stat p { font-size: 28px; font-weight: bold; color: #303133; }
</style>
