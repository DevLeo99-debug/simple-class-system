<template>
  <div class="teacher-layout">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <div class="logo-area">
        <span class="logo-icon">🎓</span>
        <span class="logo-text">课堂派</span>
      </div>

      <nav class="nav-menu">
        <div class="nav-item active">
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
          <div class="avatar">{{ userInfo.name ? userInfo.name.charAt(0) : 'T' }}</div>
          <div class="user-detail">
            <div class="username">{{ userInfo.name || '教师用户' }}</div>
            <div class="user-role">教师</div>
          </div>
        </div>
        <el-button type="danger" plain size="small" @click="logout">退出登录</el-button>
      </div>
    </aside>

    <!-- 右侧内容区 -->
    <main class="main-content">
      <!-- 顶部栏 -->
      <header class="top-bar">
        <div class="page-title">
          <h2>我的课程</h2>
          <span class="subtitle">管理您教授的所有课程</span>
        </div>
        <div class="top-actions">
          <el-button type="primary" size="large" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon>
            创建课程
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
            <div class="stat-label">课程数量</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon green">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalTasks }}</div>
            <div class="stat-label">作业总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon orange">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalStudents }}</div>
            <div class="stat-label">学生总数</div>
          </div>
        </div>
      </div>

      <!-- 课程列表区域 -->
      <div class="section-header">
        <h3>课程列表</h3>
        <span class="course-count">共 {{ courses.length }} 门课程</span>
      </div>

      <!-- 课程卡片网格 -->
      <div v-if="courses.length > 0" class="course-grid">
        <!-- 新建课程卡片 -->
        <div class="course-card new-course" @click="showCreateDialog = true">
          <div class="new-icon">
            <el-icon :size="40"><Plus /></el-icon>
          </div>
          <div class="new-text">创建新课程</div>
        </div>

        <!-- 已有课程卡片 -->
        <div v-for="course in courses" :key="course.id" class="course-card">
          <div class="course-cover" :style="{ background: getGradient(course.id) }">
            <div class="course-id-badge">课程号：{{ course.id }}</div>
          </div>
          <div class="course-body">
            <div class="course-title">{{ course.name }}</div>
            <div class="course-meta">
              <span class="meta-item">
                <el-icon><Document /></el-icon>
                {{ course.taskCount || 0 }} 个作业
              </span>
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ course.studentCount || 0 }} 名学生
              </span>
            </div>
            <div class="course-actions">
              <el-button type="primary" plain size="small" @click="goCourseDetail(course.id)">
                <el-icon><Grid /></el-icon>
                进入管理
              </el-button>
              <el-button type="danger" plain size="small" @click="handleDelete(course)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-icon :size="80" color="#d1d5db"><Box /></el-icon>
        <h3>还没有创建课程</h3>
        <p>点击下方按钮创建您的第一门课程</p>
        <el-button type="primary" size="large" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          立即创建课程
        </el-button>
      </div>
    </main>

    <!-- 创建课程弹窗 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建课程"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="courseRules"
        label-position="top"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input
            v-model="courseForm.name"
            placeholder="请输入课程名称，如：Vue前端开发"
            size="large"
          />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述（选填）"
          />
        </el-form-item>
        <el-alert
          title="课程号将由系统自动生成，学生需要使用课程号加入课程"
          type="info"
          :closable="false"
          show-icon
        />
      </el-form>
      <template #footer>
        <el-button size="large" @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" size="large" :loading="creating" @click="handleCreateCourse">
          创建课程
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTeacherCourses, createCourse, deleteCourse } from '../../api/course'

const router = useRouter()

// 用户信息
const userInfo = ref({})
try {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}')
} catch (e) {
  userInfo.value = {}
}

// 课程列表 - 模拟数据
const courses = ref([
  { id: 2001, name: 'Vue前端开发', description: 'Vue3核心概念与实战', taskCount: 3, studentCount: 32 },
  { id: 2002, name: 'Java程序设计', description: 'Java语言基础与面向对象', taskCount: 5, studentCount: 28 },
  { id: 2003, name: '数据库系统原理', description: 'SQL与数据库设计', taskCount: 2, studentCount: 40 }
])

// 创建课程相关
const showCreateDialog = ref(false)
const creating = ref(false)
const courseFormRef = ref(null)
const courseForm = reactive({
  name: '',
  description: ''
})

const courseRules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '课程名称长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 统计数据
const totalTasks = computed(() => {
  return courses.value.reduce((sum, c) => sum + (c.taskCount || 0), 0)
})

const totalStudents = computed(() => {
  return courses.value.reduce((sum, c) => sum + (c.studentCount || 0), 0)
})

// 渐变色
const getGradient = (id) => {
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

// 页面加载
onMounted(async () => {
  try {
    const result = await getTeacherCourses()
    if (result && Array.isArray(result) && result.length > 0) {
      courses.value = result
    }
  } catch (err) {
    console.log('使用模拟课程数据')
  }
})

// 创建课程
const handleCreateCourse = async () => {
  if (!courseFormRef.value) return

  await courseFormRef.value.validate(async (valid) => {
    if (!valid) return

    creating.value = true
    try {
      const result = await createCourse(courseForm)
      const newCourse = {
        id: result?.id || Math.floor(Math.random() * 9000) + 1000,
        name: courseForm.name,
        description: courseForm.description,
        taskCount: 0,
        studentCount: 0
      }
      courses.value.unshift(newCourse)

      ElMessage.success(`课程"${courseForm.name}"创建成功！课程号：${newCourse.id}`)
      showCreateDialog.value = false
      courseForm.name = ''
      courseForm.description = ''
    } catch (err) {
      // 模拟模式
      const newCourse = {
        id: Math.floor(Math.random() * 9000) + 1000,
        name: courseForm.name,
        description: courseForm.description,
        taskCount: 0,
        studentCount: 0
      }
      courses.value.unshift(newCourse)
      ElMessage.success(`课程"${courseForm.name}"创建成功！课程号：${newCourse.id}`)
      showCreateDialog.value = false
      courseForm.name = ''
      courseForm.description = ''
    } finally {
      creating.value = false
    }
  })
}

// 删除课程
const handleDelete = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程"${course.name}"吗？此操作将同时删除课程下的所有作业。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    try {
      await deleteCourse(course.id)
    } catch (err) {
      console.log('模拟删除')
    }

    const index = courses.value.findIndex(c => c.id === course.id)
    if (index !== -1) {
      courses.value.splice(index, 1)
    }
    ElMessage.success('课程已删除')
  } catch (e) {
    // 用户取消
  }
}

// 路由导航
const goCourseDetail = (id) => router.push(`/teacher/course/${id}`)
const goProfile = () => router.push('/teacher/profile')

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
.teacher-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #5b21b6 0%, #2e1065 100%);
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
  color: #e9d5ff;
  font-size: 15px;
  transition: all 0.2s;
  margin-bottom: 4px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.nav-item.active {
  background: #8b5cf6;
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
  background: linear-gradient(135deg, #ec4899 0%, #be185d 100%);
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
  color: #d8b4fe;
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
  background: linear-gradient(135deg, #6366f1 0%, #4338ca 100%);
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
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.course-cover {
  height: 100px;
  position: relative;
  display: flex;
  align-items: flex-end;
  padding: 12px;
}

.course-id-badge {
  background: rgba(255, 255, 255, 0.95);
  padding: 4px 12px;
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

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
}

.course-actions {
  display: flex;
  gap: 8px;
}

.course-actions .el-button {
  flex: 1;
}

/* 新建课程卡片 */
.new-course {
  border: 2px dashed #c4b5fd;
  background: #faf5ff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 260px;
  cursor: pointer;
  transition: all 0.2s;
}

.new-course:hover {
  border-color: #8b5cf6;
  background: #f3e8ff;
}

.new-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #ddd6fe;
  color: #7c3aed;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.new-text {
  font-size: 16px;
  font-weight: 500;
  color: #7c3aed;
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
