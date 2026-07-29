<template>
  <div>
    <h3>用户管理</h3>
    <el-table :data="[]" border>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="role" label="角色" width="120" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150">
        <template #default><el-button size="small">编辑</el-button><el-button size="small" type="danger">禁用</el-button></template>
      </el-table-column>
    </el-table>
  </div>
</template>
