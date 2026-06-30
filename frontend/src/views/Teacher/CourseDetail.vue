<template>
  <div class="teacher-layout">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <div class="logo-area">
        <span class="logo-icon">🎓</span>
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
      <!-- 顶部课程信息 -->
      <header class="course-header" :style="{ background: headerGradient }">
        <el-button link class="back-btn" @click="goHome">
          <el-icon><ArrowLeft /></el-icon>
          返回课程列表
        </el-button>
        <div class="course-info">
          <h1>{{ course.name }}</h1>
          <p>{{ course.description || '暂无课程描述' }}</p>
          <div class="course-stats">
            <span>课程号：{{ courseId }}</span>
            <span>学生：{{ course.studentCount || 0 }}人</span>
            <span>作业：{{ tasks.length }}个</span>
          </div>
        </div>
      </header>

      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="toolbar-title">
          <h3>作业列表</h3>
          <span>管理和批改本课程的作业</span>
        </div>
        <el-button type="primary" size="large" @click="showCreateTaskDialog = true">
          <el-icon><Plus /></el-icon>
          发布作业
        </el-button>
      </div>

      <!-- 作业列表 -->
      <div v-if="tasks.length > 0" class="task-list">
        <div v-for="task in tasks" :key="task.id" class="task-card">
          <div class="task-main">
            <div class="task-title">
              <el-icon class="task-icon"><Document /></el-icon>
              <span>{{ task.title }}</span>
            </div>
            <div class="task-desc">{{ task.description || '暂无作业描述' }}</div>
            <div class="task-meta">
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                截止时间：{{ task.deadline || '未设置' }}
              </span>
              <span class="meta-item">
                <el-icon><User /></el-icon>
                已提交：{{ task.submittedCount || 0 }}/{{ course.studentCount || 0 }}
              </span>
              <span class="meta-item graded-badge">
                <el-icon><Select /></el-icon>
                已批改：{{ task.gradedCount || 0 }}
              </span>
            </div>
          </div>
          <div class="task-actions">
            <el-button type="primary" size="small" @click="viewSubmissions(task)">
              查看提交
            </el-button>
            <el-button type="danger" plain size="small" @click="deleteTask(task)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-icon :size="80" color="#d1d5db"><Document /></el-icon>
        <h3>还没有作业</h3>
        <p>点击上方按钮发布第一个作业</p>
      </div>
    </main>

    <!-- 发布作业弹窗 -->
    <el-dialog
      v-model="showCreateTaskDialog"
      title="发布作业"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="taskFormRef"
        :model="taskForm"
        :rules="taskRules"
        label-position="top"
      >
        <el-form-item label="作业标题" prop="title">
          <el-input
            v-model="taskForm.title"
            placeholder="请输入作业标题，如：第一次编程作业"
            size="large"
          />
        </el-form-item>
        <el-form-item label="作业内容/要求" prop="description">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入作业的具体要求和内容"
          />
        </el-form-item>
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker
            v-model="taskForm.deadline"
            type="datetime"
            placeholder="选择截止日期和时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            style="width: 100%"
            size="large"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="large" @click="showCreateTaskDialog = false">取消</el-button>
        <el-button type="primary" size="large" :loading="creating" @click="handleCreateTask">
          发布作业
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看提交详情弹窗 -->
    <el-dialog
      v-model="showSubmissionDialog"
      :title="`作业：${currentTask?.title} - 学生提交列表`"
      width="900px"
      top="5vh"
    >
      <div class="submission-content">
        <div class="submission-summary">
          <div class="summary-item">
            <el-icon><Document /></el-icon>
            <span>已提交：{{ (currentTask?.submittedCount || 0) }} 人</span>
          </div>
          <div class="summary-item">
            <el-icon><Select /></el-icon>
            <span>已批改：{{ (currentTask?.gradedCount || 0) }} 人</span>
          </div>
        </div>

        <el-table :data="currentSubmissions" border stripe size="large">
          <el-table-column type="index" label="序号" width="80" align="center" />
          <el-table-column prop="studentName" label="学生姓名" width="140" />
          <el-table-column prop="studentId" label="学号" width="140" />
          <el-table-column prop="content" label="提交内容" min-width="200" show-overflow-tooltip>
            <template #default="scope">
              <span v-if="scope.row.content">{{ scope.row.content }}</span>
              <span v-else class="text-muted">未提交</span>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" width="160" />
          <el-table-column prop="score" label="成绩" width="100" align="center">
            <template #default="scope">
              <span v-if="scope.row.score != null" class="score-value">
                {{ scope.row.score }}分
              </span>
              <span v-else class="not-graded">未批改</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="openGradeDialog(scope.row)"
              >
                {{ scope.row.score != null ? '重新批改' : '批改' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 批改作业弹窗 -->
    <el-dialog
      v-model="showGradeDialog"
      :title="`批改：${gradingSubmission?.studentName}`"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="grade-content">
        <div class="student-info">
          <div class="info-row">
            <span class="label">学生姓名：</span>
            <span class="value">{{ gradingSubmission?.studentName }}</span>
          </div>
          <div class="info-row">
            <span class="label">学号：</span>
            <span class="value">{{ gradingSubmission?.studentId }}</span>
          </div>
          <div class="info-row">
            <span class="label">提交时间：</span>
            <span class="value">{{ gradingSubmission?.submitTime || '未提交' }}</span>
          </div>
        </div>

        <div class="submission-box">
          <h4>学生提交内容</h4>
          <div class="submission-text">
            {{ gradingSubmission?.content || '学生暂未提交内容' }}
          </div>
        </div>

        <el-form
          ref="gradeFormRef"
          :model="gradeForm"
          :rules="gradeRules"
          label-position="top"
        >
          <el-form-item label="评分 (满分100分)" prop="score">
            <el-input-number
              v-model="gradeForm.score"
              :min="0"
              :max="100"
              style="width: 200px"
              size="large"
              placeholder="输入分数"
            />
          </el-form-item>
          <el-form-item label="教师评语" prop="comment">
            <el-input
              v-model="gradeForm.comment"
              type="textarea"
              :rows="3"
              placeholder="请输入对本次作业的评语（选填）"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button size="large" @click="showGradeDialog = false">取消</el-button>
        <el-button type="primary" size="large" :loading="grading" @click="submitGrade">
          确认批改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTeacherTasks, createTask, deleteTask as deleteTaskApi, getTaskSubmissions, gradeSubmission } from '../../api/task'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref({})
try {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}')
} catch (e) {
  userInfo.value = {}
}

const courseId = route.params.id

// 课程信息
const course = ref({
  id: courseId,
  name: '课程详情',
  description: '',
  studentCount: 30,
  taskCount: 0
})

// 作业列表
const tasks = ref([
  {
    id: 1,
    title: '第一次编程作业',
    description: '完成基础的HTML页面开发，包含标题、段落、列表等基本元素',
    deadline: '2024-12-31 23:59',
    submittedCount: 25,
    gradedCount: 12
  },
  {
    id: 2,
    title: '第二次作业：CSS样式练习',
    description: '使用CSS实现页面布局和美化，要求包含响应式设计',
    deadline: '2025-01-15 23:59',
    submittedCount: 18,
    gradedCount: 5
  }
])

// 渐变色
const headerGradient = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'

// 创建作业相关
const showCreateTaskDialog = ref(false)
const creating = ref(false)
const taskFormRef = ref(null)
const taskForm = reactive({
  title: '',
  description: '',
  deadline: ''
})

const taskRules = {
  title: [
    { required: true, message: '请输入作业标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入作业内容/要求', trigger: 'blur' }
  ],
  deadline: [
    { required: true, message: '请选择截止时间', trigger: 'change' }
  ]
}

// 提交列表相关
const showSubmissionDialog = ref(false)
const currentTask = ref(null)
const currentSubmissions = ref([])

// 批改相关
const showGradeDialog = ref(false)
const grading = ref(false)
const gradingSubmission = ref(null)
const gradeFormRef = ref(null)
const gradeForm = reactive({
  score: null,
  comment: ''
})

const gradeRules = {
  score: [
    { required: true, message: '请输入评分', trigger: 'blur' }
  ]
}

// 页面加载
onMounted(async () => {
  try {
    const result = await getTeacherTasks(courseId)
    if (result && Array.isArray(result) && result.length > 0) {
      tasks.value = result
    }
    if (result && result.length > 0 && result[0].courseName) {
      course.value.name = result[0].courseName
    }
  } catch (err) {
    console.log('使用模拟数据')
  }
})

// 创建作业
const handleCreateTask = async () => {
  if (!taskFormRef.value) return

  await taskFormRef.value.validate(async (valid) => {
    if (!valid) return

    creating.value = true
    try {
      const result = await createTask(courseId, taskForm)
      const newTask = {
        id: result?.id || Date.now(),
        title: taskForm.title,
        description: taskForm.description,
        deadline: taskForm.deadline,
        submittedCount: 0,
        gradedCount: 0
      }
      tasks.value.unshift(newTask)

      ElMessage.success('作业发布成功！')
      showCreateTaskDialog.value = false
      resetTaskForm()
    } catch (err) {
      // 模拟模式
      const newTask = {
        id: Date.now(),
        title: taskForm.title,
        description: taskForm.description,
        deadline: taskForm.deadline,
        submittedCount: 0,
        gradedCount: 0
      }
      tasks.value.unshift(newTask)
      ElMessage.success('作业发布成功！')
      showCreateTaskDialog.value = false
      resetTaskForm()
    } finally {
      creating.value = false
    }
  })
}

const resetTaskForm = () => {
  taskForm.title = ''
  taskForm.description = ''
  taskForm.deadline = ''
  if (taskFormRef.value) {
    taskFormRef.value.clearValidate()
  }
}

// 删除作业
const deleteTask = async (task) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除作业"${task.title}"吗？此操作将同时删除所有学生的提交记录。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    try {
      await deleteTaskApi(task.id)
    } catch (err) {
      console.log('模拟删除')
    }

    const index = tasks.value.findIndex(t => t.id === task.id)
    if (index !== -1) {
      tasks.value.splice(index, 1)
    }
    ElMessage.success('作业已删除')
  } catch (e) {}
}

// 查看提交
const viewSubmissions = async (task) => {
  currentTask.value = task

  // 模拟的提交数据
  const mockSubmissions = [
    { id: 1, studentId: '2021001', studentName: '张三', content: '这是我的作业内容，包含了HTML的各种标签使用...', submitTime: '2024-12-20 10:30', score: 85, comment: '完成得不错' },
    { id: 2, studentId: '2021002', studentName: '李四', content: '作业已经完成，主要实现了要求的基本功能', submitTime: '2024-12-21 14:20', score: 92, comment: '非常棒！' },
    { id: 3, studentId: '2021003', studentName: '王五', content: '完成了基本要求', submitTime: '2024-12-22 09:15', score: null },
    { id: 4, studentId: '2021004', studentName: '赵六', content: '我的HTML作业', submitTime: '2024-12-23 16:45', score: null },
    { id: 5, studentId: '2021005', studentName: '钱七', content: '', submitTime: '', score: null }
  ]

  try {
    const result = await getTaskSubmissions(task.id)
    if (result && Array.isArray(result) && result.length > 0) {
      currentSubmissions.value = result
    } else {
      currentSubmissions.value = mockSubmissions
    }
  } catch (err) {
    currentSubmissions.value = mockSubmissions
  }

  showSubmissionDialog.value = true
}

// 打开批改弹窗
const openGradeDialog = (submission) => {
  gradingSubmission.value = submission
  gradeForm.score = submission.score
  gradeForm.comment = submission.comment || ''
  showGradeDialog.value = true
}

// 提交批改
const submitGrade = async () => {
  if (!gradeFormRef.value) return

  await gradeFormRef.value.validate(async (valid) => {
    if (!valid) return

    grading.value = true
    try {
      await gradeSubmission(gradingSubmission.value.id, gradeForm.score, gradeForm.comment)

      // 更新本地数据
      const submission = currentSubmissions.value.find(
        s => s.id === gradingSubmission.value.id
      )
      if (submission) {
        submission.score = gradeForm.score
        submission.comment = gradeForm.comment

        // 更新作业的已批改统计
        if (currentTask.value) {
          const gradedCount = currentSubmissions.value.filter(s => s.score != null).length
          currentTask.value.gradedCount = gradedCount
          const task = tasks.value.find(t => t.id === currentTask.value.id)
          if (task) {
            task.gradedCount = gradedCount
          }
        }
      }

      ElMessage.success('批改成功！')
      showGradeDialog.value = false
    } catch (err) {
      // 模拟模式
      const submission = currentSubmissions.value.find(
        s => s.id === gradingSubmission.value.id
      )
      if (submission) {
        submission.score = gradeForm.score
        submission.comment = gradeForm.comment

        if (currentTask.value) {
          const gradedCount = currentSubmissions.value.filter(s => s.score != null).length
          currentTask.value.gradedCount = gradedCount
          const task = tasks.value.find(t => t.id === currentTask.value.id)
          if (task) {
            task.gradedCount = gradedCount
          }
        }
      }

      ElMessage.success('批改成功！')
      showGradeDialog.value = false
    } finally {
      grading.value = false
    }
  })
}

// 导航
const goHome = () => router.push('/teacher')
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
}

/* 课程头部 */
.course-header {
  color: #fff;
  padding: 32px;
  position: relative;
}

.back-btn {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  padding: 0 0 16px;
  --el-text-color-hover: #fff;
}

.course-info h1 {
  margin: 0 0 12px;
  font-size: 28px;
}

.course-info p {
  margin: 0 0 16px;
  font-size: 15px;
  opacity: 0.9;
}

.course-stats {
  display: flex;
  gap: 24px;
  font-size: 14px;
  opacity: 0.95;
}

/* 工具栏 */
.toolbar {
  background: #fff;
  padding: 20px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.toolbar-title h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.toolbar-title span {
  font-size: 13px;
  color: #6b7280;
}

/* 作业列表 */
.task-list {
  padding: 24px 32px;
}

.task-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}

.task-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.task-main {
  flex: 1;
}

.task-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.task-icon {
  color: #6366f1;
  font-size: 20px;
}

.task-desc {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 12px;
}

.task-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #6b7280;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.graded-badge {
  color: #059669;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 60px 20px;
  text-align: center;
  margin: 24px 32px;
}

.empty-state h3 {
  margin: 20px 0 8px;
  font-size: 18px;
  color: #374151;
}

.empty-state p {
  color: #6b7280;
}

/* 提交详情表格 */
.submission-content {
  padding: 8px 0;
}

.submission-summary {
  display: flex;
  gap: 24px;
  padding: 16px 8px;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 16px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #374151;
}

.score-value {
  color: #059669;
  font-weight: 600;
}

.not-graded {
  color: #9ca3af;
}

.text-muted {
  color: #9ca3af;
  font-size: 13px;
}

/* 批改内容 */
.grade-content {
  padding: 8px 0;
}

.student-info {
  background: #f9fafb;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  padding: 6px 0;
}

.label {
  width: 100px;
  color: #6b7280;
  font-size: 14px;
}

.value {
  color: #1f2937;
  font-size: 14px;
  font-weight: 500;
}

.submission-box {
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.submission-box h4 {
  margin: 0 0 12px;
  font-size: 15px;
  color: #92400e;
}

.submission-text {
  font-size: 14px;
  color: #1f2937;
  line-height: 1.8;
  white-space: pre-wrap;
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
  }
  .course-header,
  .toolbar,
  .task-list {
    padding: 16px;
  }
  .task-card {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  .task-actions {
    width: 100%;
    display: flex;
    gap: 8px;
  }
  .task-actions .el-button {
    flex: 1;
  }
  .course-stats {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
