<template>
  <div>
    <h3>首页概览</h3>
    <el-row :gutter="16">
      <el-col :span="6"><el-card><div class="stat"><h4>商品总数</h4><p>{{ stats.products }}</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat"><h4>订单总数</h4><p>{{ stats.orders }}</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat"><h4>进行中任务</h4><p>{{ stats.tasks }}</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat"><h4>待处理告警</h4><p style="color:#e6a23c">{{ stats.alerts }}</p></div></el-card></el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card>
          <h4>邮政对接状态</h4>
          <p>Mock 模式: {{ postalMockEnabled ? '已启用' : '真实接口' }}</p>
          <p>DstSysID: XYDYYQDXT</p>
          <p>Signature: MD5 + BASE64</p>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <h4>快速操作</h4>
          <el-button type="primary" @click="$router.push('/products')">管理商品</el-button>
          <el-button type="success" style="margin-left:8px" @click="$router.push('/orders')">查看订单</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const stats = reactive({ products: 0, orders: 0, tasks: 0, alerts: 0 })
const postalMockEnabled = true
</script>

<style scoped>
.stat { text-align: center; }
.stat h4 { color: #999; margin-bottom: 8px; }
.stat p { font-size: 28px; font-weight: bold; color: #303133; }
</style>
