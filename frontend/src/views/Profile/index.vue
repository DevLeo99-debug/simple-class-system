<template>
  <div class="layout">
    <!-- 左侧导航 -->
    <aside class="sidebar" :class="{ 'teacher-theme': isTeacher }">
      <div class="logo-area">
        <span class="logo-icon">🎓</span>
        <span class="logo-text">课堂派</span>
      </div>

      <nav class="nav-menu">
        <div class="nav-item" @click="goHome">
          <el-icon><Reading /></el-icon>
          <span>{{ isTeacher ? '我的课程' : '我的课程' }}</span>
        </div>
        <div class="nav-item active">
          <el-icon><UserFilled /></el-icon>
          <span>个人中心</span>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <div class="avatar" :class="isTeacher ? 'teacher-avatar' : 'student-avatar'">
            {{ userInfo.name ? userInfo.name.charAt(0) : 'U' }}
          </div>
          <div class="user-detail">
            <div class="username">{{ userInfo.name || '用户' }}</div>
            <div class="user-role">{{ isTeacher ? '教师' : '学生' }}</div>
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
          <h2>个人中心</h2>
          <span class="subtitle">查看和管理您的个人信息</span>
        </div>
      </header>

      <!-- 用户信息卡片 -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="big-avatar" :class="isTeacher ? 'teacher-avatar' : 'student-avatar'">
            {{ userInfo.name ? userInfo.name.charAt(0) : 'U' }}
          </div>
          <div class="profile-name">
            <h2>{{ userInfo.name || '用户' }}</h2>
            <span class="role-tag" :class="isTeacher ? 'tag-teacher' : 'tag-student'">
              {{ isTeacher ? '教师账号' : '学生账号' }}
            </span>
          </div>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <div class="info-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">账号</div>
              <div class="info-value">{{ userInfo.username || userInfo.email || userInfo.phone || '---' }}</div>
            </div>
          </div>

          <div class="info-item">
            <div class="info-icon">
              <el-icon><Avatar /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">姓名</div>
              <div class="info-value">{{ userInfo.name || '---' }}</div>
            </div>
          </div>

          <div class="info-item">
            <div class="info-icon">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">ID</div>
              <div class="info-value">{{ userInfo.id || userInfo.studentId || userInfo.teacherId || '---' }}</div>
            </div>
          </div>

          <div class="info-item">
            <div class="info-icon">
              <el-icon><Medal /></el-icon>
            </div>
            <div class="info-content">
              <div class="info-label">角色</div>
              <div class="info-value">{{ isTeacher ? '教师' : '学生' }}</div>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="actions-section">
          <el-button size="large" @click="showChangePassword = true">
            <el-icon><Lock /></el-icon>
            修改密码
          </el-button>
          <el-button type="primary" size="large" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </div>

      <!-- 使用提示 -->
      <div class="tips-card">
        <h3>
          <el-icon><InfoFilled /></el-icon>
          使用提示
        </h3>
        <ul>
          <li v-if="isTeacher">您可以在"我的课程"中创建和管理您的课程</li>
          <li v-if="isTeacher">每门课程可以发布多个作业，并查看学生的提交情况</li>
          <li v-if="isTeacher">学生提交作业后，您可以为其进行评分和评语</li>
          <li v-if="!isTeacher">请在"加入课程"页面输入课程号来加入您选修的课程</li>
          <li v-if="!isTeacher">请注意作业的截止时间，按时完成并提交</li>
          <li>保持您的账号密码安全，不要分享给他人</li>
        </ul>
      </div>
    </main>

    <!-- 修改密码弹窗 -->
    <el-dialog
      v-model="showChangePassword"
      title="修改密码"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-position="top"
      >
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="passwordForm.password"
            type="password"
            placeholder="请输入新密码"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            size="large"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="large" @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" size="large" :loading="changing" @click="handleChangePassword">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 判断是否是教师
const isTeacher = computed(() => {
  return route.path.includes('teacher')
})

// 用户信息
const userInfo = ref({})
try {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}')
} catch (e) {
  userInfo.value = {}
}

// 修改密码相关
const showChangePassword = ref(false)
const changing = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    changing.value = true
    setTimeout(() => {
      ElMessage.success('密码修改成功！（演示模式）')
      showChangePassword.value = false
      passwordForm.password = ''
      passwordForm.confirmPassword = ''
      changing.value = false
    }, 800)
  })
}

// 导航
const goHome = () => {
  if (isTeacher.value) {
    router.push('/teacher')
  } else {
    router.push('/student')
  }
}

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
.layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

/* 侧边栏 - 学生主题（绿色） */
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #16a34a 0%, #064e3b 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
}

/* 教师主题（紫色） */
.sidebar.teacher-theme {
  background: linear-gradient(180deg, #5b21b6 0%, #2e1065 100%);
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

.sidebar:not(.teacher-theme) .nav-item {
  color: #dcfce7;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.nav-item.active {
  background: #8b5cf6;
  color: #fff;
}

.sidebar:not(.teacher-theme) .nav-item.active {
  background: #22c55e;
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
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.student-avatar {
  background: linear-gradient(135deg, #22c55e 0%, #166534 100%);
}

.teacher-avatar {
  background: linear-gradient(135deg, #ec4899 0%, #be185d 100%);
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
  color: rgba(255, 255, 255, 0.7);
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

/* 信息卡片 */
.profile-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid #e5e7eb;
}

.big-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  font-size: 32px;
  font-weight: 600;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-name h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #1f2937;
}

.role-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
}

.tag-student {
  background: #dcfce7;
  color: #166534;
}

.tag-teacher {
  background: #e9d5ff;
  color: #6b21a8;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 10px;
}

.info-icon {
  width: 48px;
  height: 48px;
  background: #fff;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6366f1;
  font-size: 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.info-label {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
}

.info-value {
  font-size: 16px;
  color: #1f2937;
  font-weight: 500;
}

.actions-section {
  display: flex;
  gap: 16px;
  padding-top: 8px;
}

/* 提示卡片 */
.tips-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.tips-card h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #374151;
}

.tips-card ul {
  margin: 0;
  padding-left: 24px;
}

.tips-card li {
  padding: 6px 0;
  color: #4b5563;
  font-size: 14px;
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
  }
  .profile-card,
  .tips-card {
    padding: 20px;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
  .profile-header {
    flex-direction: column;
    text-align: center;
  }
  .actions-section {
    flex-direction: column;
  }
  .actions-section .el-button {
    width: 100%;
  }
}
</style>
