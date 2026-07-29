<template>
  <div>
    <div style="display:flex; justify-content:space-between; margin-bottom:16px">
      <h3>订单管理</h3>
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:160px">
        <el-option label="待支付" value="PENDING" />
        <el-option label="支付中" value="PAYING" />
        <el-option label="已支付" value="PAID" />
        <el-option label="支付失败" value="FAILED" />
        <el-option label="已取消" value="CANCELLED" />
      </el-select>
    </div>
    <el-table :data="[]" border>
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="productName" label="商品" />
      <el-table-column prop="totalAmount" label="金额" width="120" />
      <el-table-column prop="mailNo" label="邮件号" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const statusFilter = ref('')
</script>
