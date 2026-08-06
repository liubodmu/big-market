import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios'
import type { ApiResponse, BizError } from '@/types/api'
import { errorMsg, NETWORK_ERROR } from './errorMap'

// 拦截器只做"解包 + reject"，不统一弹窗：
// draw 等接口需要对 0004/0005/0006/额度不足做专属 UI，统一弹窗会重复提示。
// 弹窗由页面/调用方决定。
const service = axios.create({
  // 环境变量与接口 URL 必须一致，统一带 /v1：/api/v1 -> /raffle/activity/draw
  baseURL: import.meta.env.VITE_API_BASE || '/api/v1',
  timeout: 5000
})

// 成功时解包 data；失败时 reject BizError（不抛 AxiosResponse 形态）
service.interceptors.response.use(
  (resp: AxiosResponse<ApiResponse<unknown>>) => {
    const body = resp.data
    if (body && body.code === '0000') return body.data as never
    const msg = body ? errorMsg(body.code) : NETWORK_ERROR
    return Promise.reject({ code: body?.code ?? 'UNKNOWN', msg })
  },
  (err: AxiosError) => {
    const msg = err?.message?.includes('timeout')
      ? '请求超时，请稍后再试'
      : NETWORK_ERROR
    return Promise.reject<BizError>({ code: 'NETWORK', msg })
  }
)

// 泛型：业务层直接拿到 data（成功时解包后的数据）
export function request<T>(config: AxiosRequestConfig): Promise<T> {
  return service.request(config) as Promise<T>
}
