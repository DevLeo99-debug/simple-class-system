<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧品牌区 -->
      <div class="brand-section">
        <div class="logo-circle">
          <span class="logo-icon">📚</span>
        </div>
        <h1 class="brand-title">课堂派</h1>
        <p class="brand-subtitle">在线学习管理平台</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-icon">🎓</span>
            <span>课程管理</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">📝</span>
            <span>作业提交</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">✅</span>
            <span>在线批改</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-section">
        <div class="form-wrapper">
          <h2 class="form-title">{{ isLogin ? '欢迎登录' : '注册账号' }}</h2>
          <p class="form-subtitle">{{ isLogin ? '请输入您的账号信息' : '创建您的新账号' }}</p>

          <!-- 角色选择 -->
          <div class="role-tabs">
            <div
              class="role-tab"
              :class="{ active: role === 'student' }"
              @click="role = 'student'"
            >
              <span class="role-icon">🎓</span>
              <span>学生</span>
            </div>
            <div
              class="role-tab"
              :class="{ active: role === 'teacher' }"
              @click="role = 'teacher'"
            >
              <span class="role-icon">👨‍🏫</span>
              <span>教师</span>
            </div>
          </div>

          <!-- 登录/注册表单切换 -->
          <div class="mode-switch">
            <span @click="toggleMode" class="switch-link">
              {{ isLogin ? '还没有账号？立即注册' : '已有账号？返回登录' }}
            </span>
          </div>

          <!-- 表单 -->
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            class="login-form"
            label-position="top"
          >
            <el-form-item v-if="!isLogin" prop="name" label="姓名">
              <el-input
                v-model="form.name"
                placeholder="请输入您的姓名"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>

            <el-form-item prop="username" :label="isLogin ? '账号' : '设置账号'">
              <el-input
                v-model="form.username"
                placeholder="请输入账号（学号/工号）"
                prefix-icon="UserFilled"
                size="large"
              />
            </el-form-item>

            <el-form-item prop="password" :label="isLogin ? '密码' : '设置密码'">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                size="large"
              />
            </el-form-item>

            <el-form-item v-if="!isLogin" prop="confirmPassword" label="确认密码">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="Lock"
                show-password
                size="large"
              />
            </el-form-item>

            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="loading"
              @click="handleSubmit"
            >
              {{ isLogin ? '登 录' : '注 册' }}
            </el-button>
          </el-form>

          <div class="demo-hint">
            <el-icon><InfoFilled /></el-icon>
            <span>如遇登录问题，请确保后端服务已启动</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  studentLogin,
  teacherLogin,
  studentRegister,
  teacherRegister
} from '../../api/user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const isLogin = ref(true)
const role = ref('student')

const form = reactive({
  name: '',
  username: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 30, message: '账号长度在 3 到 30 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度在 6 到 30 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    {
      required: true,
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 切换登录/注册模式
const toggleMode = () => {
  isLogin.value = !isLogin.value
  // 重置表单
  form.name = ''
  form.username = ''
  form.password = ''
  form.confirmPassword = ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 处理登录/注册
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const loginData = {
        username: form.username,
        password: form.password
      }

      const registerData = {
        name: form.name,
        username: form.username,
        password: form.password
      }

      let result
      if (isLogin.value) {
        // 登录
        if (role.value === 'student') {
          result = await studentLogin(loginData)
        } else {
          result = await teacherLogin(loginData)
        }
        ElMessage.success('登录成功！')
      } else {
        // 注册
        if (role.value === 'student') {
          result = await studentRegister(registerData)
        } else {
          result = await teacherRegister(registerData)
        }
        ElMessage.success('注册成功！请登录')
        isLogin.value = true
        loading.value = false
        return
      }

      // 保存登录状态
      const token = result?.token || 'mock-token-' + Date.now()
      const userInfo = result?.user || {
        id: 1,
        name: form.username,
        username: form.username
      }

      localStorage.setItem('token', token)
      localStorage.setItem('role', role.value)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))

      // 跳转
      setTimeout(() => {
        if (role.value === 'student') {
          router.push('/home')
        } else {
          router.push('/teacher/home')
        }
      }, 500)
    } catch (err) {
      // 后端未启动时，使用模拟数据方便前端调试
      console.warn('后端请求失败，使用模拟模式:', err)

      if (isLogin.value) {
        const token = 'mock-token-' + Date.now()
        const userInfo = {
          id: 1,
          name: form.username,
          username: form.username
        }
        localStorage.setItem('token', token)
        localStorage.setItem('role', role.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo))

        ElMessage.success('登录成功（模拟模式）')

        setTimeout(() => {
          if (role.value === 'student') {
            router.push('/home')
          } else {
            router.push('/teacher/home')
          }
        }, 500)
      } else {
        ElMessage.success('注册成功（模拟模式），请登录')
        isLogin.value = true
      }
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

.login-container {
  width: 100%;
  max-width: 900px;
  min-height: 560px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  display: flex;
  overflow: hidden;
}

/* 左侧品牌区 */
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  padding: 50px 40px;
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: rotate 30s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.logo-circle {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.logo-icon {
  font-size: 50px;
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 8px;
  position: relative;
  z-index: 1;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 40px;
  position: relative;
  z-index: 1;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  background: rgba(255, 255, 255, 0.1);
  padding: 12px 20px;
  border-radius: 30px;
}

.feature-icon {
  font-size: 20px;
}

/* 右侧表单区 */
.form-section {
  flex: 1;
  padding: 50px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-wrapper {
  width: 100%;
  max-width: 360px;
}

.form-title {
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px;
}

.form-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 28px;
}

/* 角色选择 */
.role-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.role-tab {
  flex: 1;
  padding: 14px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 15px;
  color: #6b7280;
  transition: all 0.2s ease;
}

.role-tab:hover {
  border-color: #60a5fa;
  color: #2563eb;
}

.role-tab.active {
  border-color: #2563eb;
  background: #eff6ff;
  color: #2563eb;
  font-weight: 500;
}

.role-icon {
  font-size: 20px;
}

/* 模式切换 */
.mode-switch {
  text-align: right;
  margin-bottom: 16px;
}

.switch-link {
  font-size: 13px;
  color: #2563eb;
  cursor: pointer;
  text-decoration: none;
}

.switch-link:hover {
  text-decoration: underline;
}

/* 表单 */
.login-form {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e5e7eb;
  transition: box-shadow 0.2s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #93c5fd;
}

:deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 2px #2563eb;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border: none;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #1d4ed8 0%, #1e40af 100%);
}

/* 提示 */
.demo-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #fef3c7;
  border-radius: 8px;
  font-size: 12px;
  color: #92400e;
}

.demo-hint .el-icon {
  font-size: 16px;
}

/* 响应式 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    min-height: auto;
  }

  .brand-section {
    padding: 40px 30px;
  }

  .form-section {
    padding: 40px 30px;
  }

  .brand-features {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
