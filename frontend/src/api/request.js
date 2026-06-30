import axios from "axios"
import { ElMessage } from "element-plus"

const request = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 10000
})

// 请求拦截（自动带token）
request.interceptors.request.use(config => {
  const token = localStorage.getItem("token")
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

// 响应拦截
request.interceptors.response.use(
  response => {
    const res = response.data
    // 统一响应格式处理：{ code, data, msg } 或 直接返回 data
    if (res && res.code !== undefined) {
      if (res.code === 200 || res.code === 0) {
        return res.data
      } else {
        ElMessage.error(res.msg || "请求失败")
        return Promise.reject(res)
      }
    }
    return res
  },
  error => {
    console.error("请求错误:", error)
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.warning("登录已过期，请重新登录")
        localStorage.clear()
        window.location.href = "/login"
      } else {
        ElMessage.error(error.response.data?.msg || "网络请求失败")
      }
    } else {
      ElMessage.error("网络连接失败，请检查后端服务是否启动")
    }
    return Promise.reject(error)
  }
)

export default request
