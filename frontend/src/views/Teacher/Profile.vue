<template>
  <div class="page">

    <div class="header">
      <h2>个人中心</h2>
    </div>

    <!-- 用户卡片 -->
    <div class="card">

      <div class="avatar">
        T
      </div>

      <div class="info">
        <div class="name">Teacher001</div>
        <div class="role">教师账号</div>
      </div>

    </div>

    <!-- 数据面板 -->
    <div class="panel">

      <!-- 身份 -->
      <div class="item">
        <div class="label">身份</div>
        <div class="value">教师</div>
      </div>

      <!-- 课程数（可展开） -->
      <div class="item clickable" @click="toggleCourses">

        <div class="label">
          管理课程数
          <span class="hint">（点击查看）</span>
        </div>

        <div class="value">{{ courses.length }}</div>

      </div>

      <!-- 展开课程列表 -->
      <div v-if="showCourses" class="course-list">

        <div
          class="course-item"
          v-for="c in courses"
          :key="c.id"
        >
          {{ c.name }}
        </div>

      </div>

    </div>

    <!-- 退出 -->
    <div class="logout">
      <button @click="logout">退出登录</button>
    </div>

  </div>
</template>

<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()

const showCourses = ref(false)

const courses = ref([
  { id: 1, name: "Vue 前端开发" },
  { id: 2, name: "Java Web" },
  { id: 3, name: "数据库原理" }
])

const toggleCourses = () => {
  showCourses.value = !showCourses.value
}

const logout = () => {
  localStorage.clear()
  router.push("/login")
}
</script>

<style scoped>
.page {
  padding: 20px;
  background: #f5f7fb;
  min-height: 100vh;
}

/* 顶部 */
.header {
  margin-bottom: 20px;
}

/* 用户卡片 */
.card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2b6cff, #6aa8ff);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
}

.name {
  font-size: 18px;
  font-weight: bold;
}

.role {
  font-size: 13px;
  color: #888;
}

/* 面板 */
.panel {
  margin-top: 20px;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}

.item {
  display: flex;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #eee;
}

.item:last-child {
  border-bottom: none;
}

.label {
  color: #666;
}

.value {
  font-weight: bold;
}

/* 可点击样式 */
.clickable {
  cursor: pointer;
}

.hint {
  font-size: 12px;
  color: #999;
  margin-left: 6px;
}

/* 展开课程列表 */
.course-list {
  padding: 10px 16px;
  background: #fafafa;
}

.course-item {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.course-item:last-child {
  border-bottom: none;
}

/* 退出 */
.logout {
  margin-top: 20px;
}

button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 8px;
  background: #ef4444;
  color: white;
  cursor: pointer;
}

button:hover {
  background: #dc2626;
}
</style>