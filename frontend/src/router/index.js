import { createRouter, createWebHistory } from "vue-router"

// 登录页面
import Login from "../views/Login/index.vue"

// 学生端页面
import StudentHome from "../views/Home/index.vue"
import StudentCourseDetail from "../views/CourseDetail/index.vue"

// 教师端页面
import TeacherHome from "../views/Teacher/Home.vue"
import TeacherCourseDetail from "../views/Teacher/CourseDetail.vue"

// 共用页面
import Profile from "../views/Profile/index.vue"

const routes = [
  { path: "/", redirect: "/login" },

  // ======================
  // 登录页面（公开）
  // ======================
  { path: "/login", component: Login, meta: { requiresAuth: false } },

  // ======================
  // 学生端（Student）
  // ======================
  { path: "/student", redirect: "/home" },
  { path: "/home", component: StudentHome, meta: { requiresAuth: true } },
  { path: "/course/:id", component: StudentCourseDetail, meta: { requiresAuth: true } },
  { path: "/profile", component: Profile, meta: { requiresAuth: true } },

  // ======================
  // 教师端（Teacher）
  // ======================
  { path: "/teacher", redirect: "/teacher/home" },
  { path: "/teacher/home", component: TeacherHome, meta: { requiresAuth: true } },
  { path: "/teacher/course/:id", component: TeacherCourseDetail, meta: { requiresAuth: true } },
  { path: "/teacher/profile", component: Profile, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ======================
// 路由守卫 - 权限控制
// ======================
router.beforeEach((to, from, next) => {
  // 检查是否需要登录
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth) {
    // 需要登录 - 检查是否有 token
    const token = localStorage.getItem("token")

    if (!token) {
      // 未登录 - 跳转到登录页
      next({
        path: "/login",
        query: { redirect: to.fullPath }
      })
      return
    }

    // 有 token - 允许访问
    next()
  } else {
    // 不需要登录（如登录页）
    // 如果用户已登录且访问登录页，跳转到对应主页
    const token = localStorage.getItem("token")
    const role = localStorage.getItem("role")

    if (token && to.path === "/login") {
      if (role === "teacher") {
        next("/teacher/home")
      } else {
        next("/home")
      }
      return
    }

    next()
  }
})

export default router
