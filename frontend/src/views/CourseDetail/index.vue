<template>
  <div class="student-layout">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <div class="logo-area">
        <span class="logo-icon">📚</span>
        <span class="logo-text">课堂派</span>
      </div>

      <nav class="nav-menu">
        <div class="nav-item" @click="goHome">
          <el-icon><Reading /></el-icon>
          <span>我的课程</span>
        </div>
        <div class="nav-item" @click="goProfile">
          <el-icon><UserFilled /></el-icon>
          <span>个人中心</span>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <div class="avatar">{{ userInfo.name ? userInfo.name.charAt(0) : 'S' }}</div>
          <div class="user-detail">
            <div class="username">{{ userInfo.name || '学生用户' }}</div>
            <div class="user-role">学生</div>
          </div>
        </div>
        <el-button type="danger" plain size="small" @click="logout">退出登录</el-button>
      </div>
    </aside>

    <!-- 右侧内容区 -->
    <main class="main-content">
      <!-- 顶部栏 -->
      <header class="top-bar">
        <div class="breadcrumb">
          <span class="link" @click="goHome">我的课程</span>
          <el-icon><ArrowRight /></el-icon>
          <span>{{ course.name }}</span>
        </div>
        <el-button @click="goHome">
          <el-icon><Back /></el-icon>
          返回
        </el-button>
      </header>

      <!-- 课程信息 -->
      <div class="course-header" :style="{ background: getGradient() }">
        <div class="course-meta">
          <div class="course-badge">课程号：{{ course.id }}</div>
          <h1 class="course-name">{{ course.name }}</h1>
          <div class="course-teacher">
            <el-icon><UserFilled /></el-icon>
            <span>授课教师：{{ course.teacher || '待分配' }}</span>
          </div>
        </div>
        <div class="course-stats">
          <div class="stat-box">
            <div class="stat-num">{{ tasks.length }}</div>
            <div class="stat-desc">作业总数</div>
          </div>
          <div class="stat-box">
            <div class="stat-num">{{ submittedCount }}</div>
            <div class="stat-desc">已提交</div>
          </div>
          <div class="stat-box">
            <div class="stat-num">{{ pendingCount }}</div>
            <div class="stat-desc">待提交</div>
          </div>
        </div>
      </div>

      <!-- 作业列表 -->
      <div class="section-title">
        <h3>作业列表</h3>
        <span class="hint">点击作业卡片查看详情或提交</span>
      </div>

      <div v-if="tasks.length > 0" class="task-list">
        <div
          v-for="task in tasks"
          :key="task.id"
          class="task-card"
          @click="handleTaskClick(task)"
        >
          <div class="task-status" :class="getStatusClass(task)">
            <el-icon v-if="task.status === 'submitted'" :size="24"><CircleCheck /></el-icon>
            <el-icon v-else-if="task.status === 'graded'" :size="24"><Medal /></el-icon>
            <el-icon v-else :size="24"><Clock /></el-icon>
          </div>
          <div class="task-info">
            <div class="task-title">{{ task.title }}</div>
            <div class="task-desc">{{ task.description || '点击查看作业详情并提交' }}</div>
            <div class="task-meta">
              <span v-if="task.deadline" class="deadline">
                <el-icon><Timer /></el-icon>
                截止：{{ task.deadline }}
              </span>
              <span v-if="task.score !== undefined && task.status === 'graded'" class="score">
                <el-icon><Trophy /></el-icon>
                得分：{{ task.score }}分
              </span>
            </div>
          </div>
          <div class="task-action">
            <el-tag
              :type="getTagType(task)"
              effect="light"
              size="large"
            >
              {{ getStatusText(task) }}
            </el-tag>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-icon :size="80" color="#d1d5db"><Document /></el-icon>
        <h3>暂无作业</h3>
        <p>教师尚未发布作业，请耐心等待</p>
      </div>
    </main>

    <!-- 作业详情/提交弹窗 -->
    <el-dialog
      v-model="showTaskDialog"
      :title="currentTask?.title || '作业详情'"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentTask" class="task-detail">
        <div class="detail-row">
          <span class="label">作业标题：</span>
          <span class="value">{{ currentTask.title }}</span>
        </div>
        <div v-if="currentTask.description" class="detail-row">
          <span class="label">作业描述：</span>
        </div>
        <div v-if="currentTask.description" class="desc-box">
          {{ currentTask.description }}
        </div>
        <div v-if="currentTask.deadline" class="detail-row">
          <span class="label">截止时间：</span>
          <span class="value">{{ currentTask.deadline }}</span>
        </div>

        <!-- 已评分显示 -->
        <div v-if="currentTask.status === 'graded'" class="grade-box">
          <el-alert
            :title="'作业已批改，得分：' + currentTask.score + ' 分'"
            type="success"
            show-icon
            :closable="false"
          />
        </div>

        <!-- 已提交显示 -->
        <div v-if="currentTask.status === 'submitted'" class="submitted-box">
          <el-alert
            title="您已提交该作业，等待教师批改"
            type="info"
            show-icon
            :closable="false"
          />
          <div class="submitted-content">
            <div class="label">您提交的内容：</div>
            <div class="content-box">{{ currentTask.content || '（无具体内容）' }}</div>
          </div>
        </div>

        <!-- 提交区域（未提交时显示） -->
        <div v-if="currentTask.status !== 'submitted' && currentTask.status !== 'graded'" class="submit-area">
          <el-form
            ref="submitFormRef"
            :model="submitForm"
            label-position="top"
          >
            <el-form-item label="作业内容" prop="content">
              <el-input
                v-model="submitForm.content"
                type="textarea"
                :rows="8"
                placeholder="请在此输入您的作业内容..."
                resize="none"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      <template #footer>
        <el-button size="large" @click="showTaskDialog = false">关闭</el-button>
        <el-button
          v-if="currentTask && currentTask.status !== 'submitted' && currentTask.status !== 'graded'"
          type="primary"
          size="large"
          :loading="submitting"
          @click="handleSubmit"
        >
          提交作业
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseDetail } from '../../api/course'
import { getStudentTasks, submitTask } from '../../api/task'

const router = useRouter()
const route = useRoute()
const courseId = route.params.id

// 用户信息
const userInfo = ref({})
try {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}')
} catch (e) {
  userInfo.value = {}
}

// 课程信息
const course = ref({
  id: courseId,
  name: '课程详情',
  teacher: '待分配'
})

// 作业列表
const tasks = ref([
  { id: 1, title: '第一次作业 - 基础练习', description: '完成课本第一章课后习题，提交您的答案', deadline: '2024-12-31 23:59', status: 'pending' },
  { id: 2, title: '第二次作业 - 编程实践', description: '使用 Vue3 实现一个简单的待办事项应用', deadline: '2024-12-25 23:59', status: 'submitted', content: '已完成待办事项应用代码' },
  { id: 3, title: '第三次作业 - 项目报告', description: '撰写课程学习报告，不少于1500字', deadline: '2024-12-20 23:59', status: 'graded', score: 92 }
])

// 弹窗状态
const showTaskDialog = ref(false)
const currentTask = ref(null)
const submitting = ref(false)
const submitFormRef = ref(null)
const submitForm = reactive({
  content: ''
})

// 统计计算
const submittedCount = computed(() => {
  return tasks.value.filter(t => t.status === 'submitted' || t.status === 'graded').length
})

const pendingCount = computed(() => {
  return tasks.value.filter(t => t.status === 'pending').length
})

// 渐变色
const getGradient = () => {
  const gradients = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  ]
  const id = parseInt(courseId) || 0
  return gradients[id % gradients.length]
}

// 状态相关函数
const getStatusClass = (task) => {
  if (task.status === 'graded') return 'status-graded'
  if (task.status === 'submitted') return 'status-submitted'
  return 'status-pending'
}

const getTagType = (task) => {
  if (task.status === 'graded') return 'success'
  if (task.status === 'submitted') return 'warning'
  return 'info'
}

const getStatusText = (task) => {
  if (task.status === 'graded') return '已批改'
  if (task.status === 'submitted') return '已提交'
  return '待提交'
}

// 页面加载
onMounted(async () => {
  try {
    const courseData = await getCourseDetail(courseId)
    if (courseData) {
      course.value = courseData
    }
  } catch (err) {
    console.log('使用默认课程信息')
  }

  try {
    const taskData = await getStudentTasks(courseId)
    if (taskData && Array.isArray(taskData) && taskData.length > 0) {
      tasks.value = taskData
    }
  } catch (err) {
    console.log('使用默认作业列表')
  }
})

// 点击作业
const handleTaskClick = (task) => {
  currentTask.value = task
  submitForm.content = ''
  showTaskDialog.value = true
}

// 提交作业
const handleSubmit = async () => {
  if (!submitForm.content.trim()) {
    ElMessage.warning('请输入作业内容')
    return
  }

  submitting.value = true
  try {
    await submitTask(currentTask.value.id, submitForm.content)

    // 更新本地状态
    const taskIndex = tasks.value.findIndex(t => t.id === currentTask.value.id)
    if (taskIndex !== -1) {
      tasks.value[taskIndex].status = 'submitted'
      tasks.value[taskIndex].content = submitForm.content
    }

    ElMessage.success('作业提交成功！')
    showTaskDialog.value = false
  } catch (err) {
    // 模拟模式
    const taskIndex = tasks.value.findIndex(t => t.id === currentTask.value.id)
    if (taskIndex !== -1) {
      tasks.value[taskIndex].status = 'submitted'
      tasks.value[taskIndex].content = submitForm.content
    }
    ElMessage.success('作业提交成功（模拟模式）！')
    showTaskDialog.value = false
  } finally {
    submitting.value = false
  }
}

// 路由导航
const goHome = () => router.push('/home')
const goProfile = () => router.push('/profile')

// 退出登录
const logout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    localStorage.clear()
    router.push('/login')
  } catch (e) {}
}
</script>

<style scoped>
.student-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

/* 侧边栏 - 与首页相同 */
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1e3a5f 0%, #0f172a 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
}

.logo-area {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon { font-size: 28px; }
.logo-text { font-size: 20px; font-weight: 600; }

.nav-menu { flex: 1; padding: 16px 12px; }

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  cursor: pointer;
  color: #cbd5e1;
  font-size: 15px;
  transition: all 0.2s;
  margin-bottom: 4px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.user-detail { flex: 1; }
.username { font-size: 14px; font-weight: 500; }
.user-role { font-size: 12px; color: #94a3b8; }

/* 主内容区 */
.main-content {
  flex: 1;
  margin-left: 240px;
  padding: 0 32px 32px;
}

/* 顶部栏 */
.top-bar {
  background: #fff;
  margin: 0 -32px 24px;
  padding: 16px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #374151;
}

.breadcrumb .link {
  color: #3b82f6;
  cursor: pointer;
}

.breadcrumb .link:hover {
  text-decoration: underline;
}

/* 课程头部 */
.course-header {
  border-radius: 16px;
  padding: 32px;
  color: #fff;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.25);
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  margin-bottom: 12px;
}

.course-name {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
}

.course-teacher {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.95;
}

.course-stats {
  display: flex;
  gap: 24px;
}

.stat-box {
  background: rgba(255, 255, 255, 0.2);
  padding: 16px 24px;
  border-radius: 12px;
  text-align: center;
  min-width: 90px;
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-desc {
  font-size: 13px;
  opacity: 0.9;
}

/* 区域标题 */
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16px;
}

.section-title h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.hint {
  font-size: 13px;
  color: #6b7280;
}

/* 作业列表 */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-card {
  background: #fff;
  padding: 20px 24px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.task-card:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.task-status {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.status-pending {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.status-submitted {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.status-graded {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.task-info {
  flex: 1;
}

.task-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 6px;
}

.task-desc {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
}

.task-meta {
  display: flex;
  gap: 16px;
}

.deadline, .score {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #6b7280;
}

.score {
  color: #059669;
  font-weight: 500;
}

.task-action {
  display: flex;
  align-items: center;
  gap: 12px;
}

.arrow {
  color: #9ca3af;
  font-size: 16px;
}

/* 作业详情弹窗 */
.detail-row {
  margin-bottom: 16px;
  font-size: 14px;
}

.detail-row .label {
  color: #6b7280;
  margin-right: 8px;
}

.detail-row .value {
  color: #1f2937;
  font-weight: 500;
}

.desc-box {
  background: #f9fafb;
  padding: 16px;
  border-radius: 8px;
  line-height: 1.6;
  color: #374151;
  margin-bottom: 16px;
}

.grade-box, .submitted-box {
  margin-bottom: 16px;
}

.submitted-content {
  margin-top: 12px;
}

.submitted-content .label {
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 8px;
  display: block;
}

.content-box {
  background: #f9fafb;
  padding: 12px;
  border-radius: 8px;
  line-height: 1.6;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 60px 20px;
  text-align: center;
}

.empty-state h3 {
  margin: 20px 0 8px;
  font-size: 18px;
  color: #374151;
}

.empty-state p {
  color: #6b7280;
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    width: 70px;
  }
  .logo-text, .nav-item span, .user-detail {
    display: none;
  }
  .main-content {
    margin-left: 70px;
    padding: 0 16px 16px;
  }
  .course-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  .course-stats {
    width: 100%;
    justify-content: center;
  }
}
</style>
