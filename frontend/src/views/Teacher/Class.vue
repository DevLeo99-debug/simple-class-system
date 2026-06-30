<template>
  <div class="page">

    <div class="top">
      <button @click="back">返回班级</button>
      <h2>作业批改</h2>
    </div>

    <div class="box">

      <div class="item" v-for="s in submissions" :key="s.id">

        <div>
          {{ s.student }} ｜ {{ s.task }}
        </div>

        <div>
          <input v-model="s.score" placeholder="分数" />
          <button @click="grade(s)">提交</button>
        </div>

      </div>

    </div>

  </div>
</template>

<script setup>
import { useRouter } from "vue-router"
import { ref } from "vue"

const router = useRouter()

const submissions = ref([
  { id: 1, student: "张三", task: "作业1", score: "" },
  { id: 2, student: "李四", task: "作业1", score: "" }
])

const grade = (s) => {
  alert(`${s.student} 得分 ${s.score}`)
}

const back = () => {
  router.back()
}
</script>

<style scoped>
.page {
  padding: 20px;
}

.box {
  background: white;
  padding: 15px;
  border-radius: 10px;
}

.item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
}
</style>