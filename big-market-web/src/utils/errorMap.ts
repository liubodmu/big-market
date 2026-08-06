// 错误码 -> 前端文案（对应后端 ResponseCode 枚举，见 frontDev/cc.txt 第七节）
export const errorMap: Record<string, string> = {
  '0000': '成功',
  '0001': '系统繁忙，请稍后再试',
  '0002': '参数错误',
  '0003': '操作已提交，请勿重复操作',
  '0004': '活动已降级，请稍后再试',
  '0005': '操作太频繁，请稍后再试',
  '0006': '系统繁忙，请稍后再试',
  ERR_BIZ_001: '配置异常，请联系运营',
  ERR_BIZ_002: '活动配置中，请稍后再试',
  ERR_BIZ_003: '活动未开启',
  ERR_BIZ_004: '活动已结束或未开始',
  ERR_BIZ_005: '库存不足，先到先得',
  ERR_BIZ_006: '抽奖总次数已用完',
  ERR_BIZ_007: '本月抽奖次数已用完',
  ERR_BIZ_008: '今日抽奖次数已用完',
  ERR_BIZ_009: '不能重复抽奖',
  ERR_CONFIG_001: '配置异常，请联系运营',
  ERR_CREDIT_001: '积分余额不足'
}

export const NETWORK_ERROR = '网络异常，请稍后再试'

export function errorMsg(code?: string): string {
  if (!code) return NETWORK_ERROR
  return errorMap[code] || '系统繁忙，请稍后再试'
}
