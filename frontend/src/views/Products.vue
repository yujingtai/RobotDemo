<template>
  <div>
    <div style="display:flex; justify-content:space-between; margin-bottom:16px">
      <h3>商品管理</h3>
      <el-button type="primary">新增商品</el-button>
    </div>
    <el-table :data="[]" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="price" label="价格" width="120" />
      <el-table-column prop="tags" label="标签" />
      <el-table-column prop="displayPosition" label="陈列点位" />
      <el-table-column prop="robotGrabbable" label="可抓取展示" width="100" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" width="150">
        <template #default><el-button size="small">编辑</el-button><el-button size="small" type="danger">下架</el-button></template>
      </el-table-column>
    </el-table>
  </div>
</template>
