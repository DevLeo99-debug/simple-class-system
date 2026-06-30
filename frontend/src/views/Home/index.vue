<template>
  <div class="student-layout">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <div class="logo-area">
        <span class="logo-icon">📚</span>
        <span class="logo-text">课堂派</span>
      </div>

      <nav class="nav-menu">
        <div
          class="nav-item active"
          @click="goHome"
        >
          <el-icon><Reading /></el-icon>
          <span>我的课程</span>
        </div>
        <div
          class="nav-item"
          @click="goProfile"
        >
          <el-icon><UserFilled /></el-icon>
          <span>个人中心</span>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <div class="avatar">
            {{ userInfo.name ? userInfo.name.charAt(0) : 'S' }}
          </div>
          <div class="user-detail">
            <div class="username">{{ userInfo.name || '学生用户' }}</div>
            <div class="user-role">学生</div>
          </div>
        </div>
        <el-button type="danger" plain size="small" @click="logout">
          退出登录
        </el-button>
      </div>
    </aside>

    <!-- 右侧内容区 -->
    <main class="main-content">
      <!-- 顶部栏 -->
      <header class="top-bar">
        <div class="page-title">
          <h2>我的课程</h2>
          <span class="subtitle">查看您已选修的所有课程</span>
        </div>
        <div class="top-actions">
          <el-button type="primary" size="large" @click="showEnrollDialog = true">
            <el-icon><Plus /></el-icon>
            加入课程
          </el-button>
        </div>
      </header>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon blue">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ courses.length }}</div>
            <div class="stat-label">已选课程</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon green">
            <el-icon><DocumentChecked /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalTasks }}</div>
            <div class="stat-label">总作业数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon orange">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ pendingTasks }}</div>
            <div class="stat-label">待提交</div>
          </div>
        </div>
      </div>

      <!-- 选课提示 -->
      <div class="section-header">
        <h3>课程列表</h3>
        <span class="course-count">共 {{ courses.length }} 门课程</span>
      </div>

      <!-- 课程卡片网格 -->
      <div v-if="courses.length > 0" class="course-grid">
        <div
          v-for="course in courses"
          :key="course.id"
          class="course-card"
          @click="goCourseDetail(course.id)"
        >
          <div class="course-cover" :style="{ background: getRandomGradient(course.id) }">
            <span class="course-code">课程号：{{ course.id }}</span>
          </div>
          <div class="course-body">
            <div class="course-title">{{ course.name }}</div>
            <div class="course-teacher">
              <el-icon><User /></el-icon>
              <span>{{ course.teacher || '待分配' }}</span>
            </div>
            <div class="course-footer">
              <div class="task-info">
                <el-icon><Document /></el-icon>
                <span>{{ course.taskCount || 0 }} 个作业</span>
              </div>
              <el-button type="primary" text size="small" @click.stop="goCourseDetail(course.id)">
                进入课程
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-icon :size="80" color="#d1d5db"><Box /></el-icon>
        <h3>还没有选修课程</h3>
        <p>点击右上角的"加入课程"按钮，输入课程号加入课程</p>
        <el-button type="primary" size="large" @click="showEnrollDialog = true">
          <el-icon><Plus /></el-icon>
          立即加入课程
        </el-button>
      </div>
    </main>

    <!-- 选课弹窗 -->
    <el-dialog
      v-model="showEnrollDialog"
      title="加入课程"
      width="420px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="enrollFormRef"
        :model="enrollForm"
        :rules="enrollRules"
        label-position="top"
      >
        <el-form-item label="课程号" prop="courseId">
          <el-input
            v-model="enrollForm.courseId"
            placeholder="请输入教师提供的课程号"
            size="large"
          />
        </el-form-item>
        <el-alert
          title="提示"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            请向您的授课教师获取课程号，输入后即可加入该课程。
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button size="large" @click="showEnrollDialog = false">取消</el-button>
        <el-button type="primary" size="large" :loading="enrolling" @click="handleEnroll">
          确认加入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentCourses, enrollCourse } from '../../api/course'

const router = useRouter()
const showEnrollDialog = ref(false)
const enrolling = ref(false)
const enrollFormRef = ref(null)

// 用户信息
const userInfo = ref({})
try {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}')
} catch (e) {
  userInfo.value = {}
}

// 课程列表
const courses = ref([
  { id: 1001, name: 'Vue前端开发', teacher: '张老师', taskCount: 3 },
  { id: 1002, name: 'Java程序设计', teacher: '李老师', taskCount: 5 },
  { id: 1003, name: '数据库系统原理', teacher: '王老师', taskCount: 2 },
  { id: 1004, name: '计算机网络', teacher: '赵老师', taskCount: 4 }
])

// 选课表单
const enrollForm = reactive({
  courseId: ''
})

const enrollRules = {
  courseId: [
    { required: true, message: '请输入课程号', trigger: 'blur' }
  ]
}

// 统计数据
const totalTasks = computed(() => {
  return courses.value.reduce((sum, c) => sum + (c.taskCount || 0), 0)
})

const pendingTasks = computed(() => {
  return Math.floor(totalTasks.value / 2)
})

// 获取随机渐变色
const getRandomGradient = (id) => {
  const gradients = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #30cfd0 0%, #330867 100%)'
  ]
  return gradients[id % gradients.length]
}

// 页面加载时获取课程列表
onMounted(async () => {
  try {
    const result = await getStudentCourses()
    if (result && Array.isArray(result) && result.length > 0) {
      courses.value = result
    }
  } catch (err) {
    console.log('使用模拟课程数据')
  }
})

// 选课
const handleEnroll = async () => {
  if (!enrollFormRef.value) return

  await enrollFormRef.value.validate(async (valid) => {
    if (!valid) return

    enrolling.value = true
    try {
      const courseId = parseInt(enrollForm.courseId)
      await enrollCourse(courseId)

      // 检查课程是否已存在
      const exists = courses.value.some(c => c.id === courseId)
      if (!exists) {
        courses.value.push({
          id: courseId,
          name: `新课程 ${courseId}`,
          teacher: '待分配',
          taskCount: 0
        })
      }

      ElMessage.success('成功加入课程！')
      showEnrollDialog.value = false
      enrollForm.courseId = ''
    } catch (err) {
      // 模拟模式下也允许加入
      const courseId = parseInt(enrollForm.courseId)
      if (courseId) {
        const exists = courses.value.some(c => c.id === courseId)
        if (!exists) {
          courses.value.push({
            id: courseId,
            name: `新课程 ${courseId}`,
            teacher: '待分配',
            taskCount: 0
          })
        }
        ElMessage.success('成功加入课程（模拟模式）！')
        showEnrollDialog.value = false
        enrollForm.courseId = ''
      } else {
        ElMessage.error('请输入有效的课程号')
      }
    } finally {
      enrolling.value = false
    }
  })
}

// 路由导航
const goHome = () => router.push('/home')
const goProfile = () => router.push('/profile')
const goCourseDetail = (id) => router.push(`/course/detail/${id}`)

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
  } catch (e) {
    // 用户取消
  }
}
</script>

<style scoped>
.student-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

/* 侧边栏 */
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

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
}

.nav-menu {
  flex: 1;
  padding: 16px 12px;
}

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

.nav-item.active {
  background: #3b82f6;
  color: #fff;
}

.nav-item .el-icon {
  font-size: 18px;
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

.user-detail {
  flex: 1;
}

.username {
  font-size: 14px;
  font-weight: 500;
}

.user-role {
  font-size: 12px;
  color: #94a3b8;
}

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
  padding: 20px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.page-title h2 {
  margin: 0;
  font-size: 22px;
  color: #1f2937;
}

.page-title .subtitle {
  font-size: 13px;
  color: #6b7280;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-icon.blue {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.stat-icon.green {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.orange {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
}

/* 区域标题 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.course-count {
  font-size: 13px;
  color: #6b7280;
}

/* 课程卡片网格 */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.course-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.course-cover {
  height: 120px;
  position: relative;
  display: flex;
  align-items: flex-end;
  padding: 12px;
}

.course-code {
  background: rgba(255, 255, 255, 0.9);
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  color: #374151;
  font-weight: 500;
}

.course-body {
  padding: 16px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.course-teacher {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 12px;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.task-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
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
  margin-bottom: 24px;
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    width: 70px;
  }
  .logo-text,
  .nav-item span,
  .user-detail {
    display: none;
  }
  .main-content {
    margin-left: 70px;
    padding: 0 16px 16px;
  }
  .top-bar {
    margin: 0 -16px 16px;
    padding: 16px;
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  .stats-row {
    grid-template-columns: 1fr;
  }
  .course-grid {
    grid-template-columns: 1fr;
  }
}
</style>
