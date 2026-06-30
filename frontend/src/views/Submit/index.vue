<template>
  <div class="page">

    <div class="top">
      <button @click="back">返回</button>
      <h2>提交作业</h2>
    </div>

    <div class="box">
      <h3>作业ID：{{ id }}</h3>

      <textarea v-model="content" placeholder="请输入作业内容"></textarea>

      <button class="submit" @click="submit">
        提交
      </button>
    </div>

  </div>
</template>

<script setup>
import { ref } from "vue"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()

const id = route.params.id
const mode = route.query.mode || "submit"

const content = ref("")

const submit = () => {
  if (mode === "view") {
    alert("当前为查看模式（只读）")
    return
  }

  if (!content.value) {
    alert("请输入作业内容")
    return
  }

  alert("提交成功")
  router.push("/course/detail/" + id)
}

const back = () => {
  router.push("/course/detail/" + id)
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
  align-items: center;
  gap: 10px;
}

button {
  padding: 6px 12px;
  background: #2b6cff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.box {
  margin-top: 20px;
  background: white;
  padding: 15px;
  border-radius: 10px;
}

textarea {
  width: 100%;
  height: 150px;
  margin-top: 10px;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #ddd;
  resize: none;
}

.submit {
  margin-top: 10px;
}
</style>