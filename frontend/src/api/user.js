import request from "./request"

// 学生注册
export const studentRegister = (data) => {
  return request.post("/student/register", data)
}

// 教师注册
export const teacherRegister = (data) => {
  return request.post("/teacher/register", data)
}

// 学生登录
export const studentLogin = (data) => {
  return request.post("/student/login", data)
}

// 教师登录
export const teacherLogin = (data) => {
  return request.post("/teacher/login", data)
}

// 获取当前学生信息
export const getStudentInfo = () => {
  return request.get("/student/info")
}

// 获取当前教师信息
export const getTeacherInfo = () => {
  return request.get("/teacher/info")
}

// 更新学生信息
export const updateStudentInfo = (data) => {
  return request.put("/student/info", data)
}

// 更新教师信息
export const updateTeacherInfo = (data) => {
  return request.put("/teacher/info", data)
}
