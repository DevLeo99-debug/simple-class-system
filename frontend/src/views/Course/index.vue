<template>
  <div class="page">
    <div class="header">
      <h2>我的课程</h2>
      <button @click="goHome">返回首页</button>
    </div>

    <div class="list">
      <div class="card" v-for="item in courses" :key="item.id">
        <div class="title">{{ item.name }}</div>
        <div class="teacher">教师：{{ item.teacher }}</div>

        <button @click="enterCourse(item)">进入课程</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router"

const router = useRouter()

const courses = [
  { id: 1, name: "Vue 前端开发", teacher: "张老师" },
  { id: 2, name: "Java Web", teacher: "李老师" },
  { id: 3, name: "数据库原理", teacher: "王老师" }
]

const enterCourse = (item) => {
  router.push("/course/detail/" + item.id)
}

const goHome = () => {
  router.push("/home")
}
</script>

<style scoped>
.page {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

button {
  padding: 6px 12px;
  border: none;
  background: #2b6cff;
  color: white;
  border-radius: 6px;
  cursor: pointer;
}

.list {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.card {
  width: 220px;
  background: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.teacher {
  margin: 10px 0;
  font-size: 14px;
  color: #666;
}
</style>