<template>
  <div class="page">

    <div class="top">
      <button @click="back">返回</button>
      <h2>课程：{{ course.name }}</h2>
    </div>

    <!-- 班级列表 -->
    <div class="box">
      <h3>班级列表</h3>

      <div
        class="class-item"
        v-for="c in classes"
        :key="c.id"
        @click="selectClass(c)"
      >
        {{ c.name }} ｜ 学生数：{{ c.students.length }}
      </div>
    </div>

    <!-- 学生列表 -->
    <div v-if="currentClass" class="box">

      <h3>学生列表 - {{ currentClass.name }}</h3>

      <div class="student" v-for="s in currentClass.students" :key="s.id">
        {{ s.name }}
      </div>

      <button class="btn" @click="goGrade(currentClass)">
        进入作业批改
      </button>

    </div>

  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router"
import { ref } from "vue"

const route = useRoute()
const router = useRouter()

const id = route.params.id

const course = {
  1: { name: "Vue 前端开发" },
  2: { name: "Java Web" },
  3: { name: "数据库原理" }
}[id] || { name: "未知课程" }

const classes = ref([
  {
    id: 1,
    name: "1班",
    students: [
      { id: 1, name: "张三" },
      { id: 2, name: "李四" }
    ]
  },
  {
    id: 2,
    name: "2班",
    students: [
      { id: 3, name: "王五" },
      { id: 4, name: "赵六" }
    ]
  }
])

const currentClass = ref(null)

const selectClass = (c) => {
  currentClass.value = c
}

const goGrade = (c) => {
  router.push({
    path: "/teacher/class/" + c.id
  })
}

const back = () => {
  router.push("/teacher/home")
}
</script>

<style scoped>
.page {
  padding: 20px;
  background: #f5f7fb;
  min-height: 100vh;
}

.top {
  display: flex;
  gap: 10px;
  align-items: center;
}

.box {
  margin-top: 20px;
  background: white;
  padding: 15px;
  border-radius: 10px;
}

.class-item {
  padding: 10px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.student {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.btn {
  margin-top: 10px;
  padding: 8px 12px;
  background: #2b6cff;
  color: white;
  border: none;
  border-radius: 6px;
}
</style>