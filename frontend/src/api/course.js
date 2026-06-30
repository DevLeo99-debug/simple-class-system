import request from "./request"

// ============== 学生端接口 ==============

// 获取学生已选课程列表
export const getStudentCourses = () => {
  return request.get("/student/courses")
}

// 学生选课（通过课程号）
export const enrollCourse = (courseId) => {
  return request.post("/student/enroll", { courseId })
}

// 学生退课
export const dropCourse = (courseId) => {
  return request.post("/student/drop", { courseId })
}

// ============== 教师端接口 ==============

// 获取教师管理的课程列表
export const getTeacherCourses = () => {
  return request.get("/teacher/courses")
}

// 教师创建课程
export const createCourse = (data) => {
  return request.post("/teacher/course", data)
}

// 教师更新课程
export const updateCourse = (courseId, data) => {
  return request.put(`/teacher/course/${courseId}`, data)
}

// 教师删除课程
export const deleteCourse = (courseId) => {
  return request.delete(`/teacher/course/${courseId}`)
}

// ============== 通用接口 ==============

// 获取课程详情
export const getCourseDetail = (id) => {
  return request.get(`/course/${id}`)
}

// 获取课程下的学生列表
export const getCourseStudents = (courseId) => {
  return request.get(`/course/${courseId}/students`)
}
