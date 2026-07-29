<template>
  <div>
    <h3>库存管理</h3>
    <el-table :data="[]" border>
      <el-table-column prop="productName" label="商品" />
      <el-table-column prop="totalQuantity" label="总库存" width="100" />
      <el-table-column prop="lockedQuantity" label="锁定库存" width="100" />
      <el-table-column prop="availableQuantity" label="可用库存" width="100" />
      <el-table-column prop="lowThreshold" label="低库存阈值" width="120" />
      <el-table-column label="状态" width="150">
        <template #default><el-tag type="danger">低库存</el-tag></template>
      </el-table-column>
    </el-table>
  </div>
</template>
