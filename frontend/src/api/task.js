import request from "./request"

// ============== 学生端接口 ==============

// 获取课程下的作业列表（学生视角，包含提交状态）
export const getStudentTasks = (courseId) => {
  return request.get(`/student/course/${courseId}/tasks`)
}

// 获取作业详情
export const getTaskDetail = (taskId) => {
  return request.get(`/task/${taskId}`)
}

// 提交作业
export const submitTask = (taskId, content) => {
  return request.post(`/student/task/${taskId}/submit`, { content })
}

// 获取学生的作业提交记录
export const getSubmission = (taskId) => {
  return request.get(`/student/task/${taskId}/submission`)
}

// ============== 教师端接口 ==============

// 获取课程下的作业列表（教师视角）
export const getTeacherTasks = (courseId) => {
  return request.get(`/teacher/course/${courseId}/tasks`)
}

// 教师发布作业
export const createTask = (courseId, data) => {
  return request.post(`/teacher/course/${courseId}/task`, data)
}

// 教师更新作业
export const updateTask = (taskId, data) => {
  return request.put(`/teacher/task/${taskId}`, data)
}

// 教师删除作业
export const deleteTask = (taskId) => {
  return request.delete(`/teacher/task/${taskId}`)
}

// 获取作业的所有提交记录（用于批改）
export const getTaskSubmissions = (taskId) => {
  return request.get(`/teacher/task/${taskId}/submissions`)
}

// 教师评分
export const gradeSubmission = (submissionId, score, comment) => {
  return request.post(`/teacher/grade`, {
    submissionId,
    score,
    comment
  })
}
