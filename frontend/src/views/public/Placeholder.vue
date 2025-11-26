<template>
  <div class="placeholder-page">
    <div class="container">
      <el-result
        icon="info"
        :title="$route.meta.title"
        sub-title="该页面正在建设中，敬请期待！"
      >
        <template #extra>
          <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<style scoped>
.placeholder-page {
  padding: 60px 0;
}
</style>
