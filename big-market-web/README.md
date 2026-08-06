# big-market-web 掘金狂潮前端

基于 Vite + Vue 3 + TypeScript + Pinia + Vue Router + Element Plus 的营销抽奖平台前端。

## 目录结构

```
src/
├── api/          # 接口封装（raffle / activity / credit / dcc）
├── components/   # Wheel 转盘、AwardDialog 中奖弹窗、SkuCard 商品卡
├── mock/         # mock 数据（SQL 种子真实数据，VITE_USE_MOCK=true 启用）
├── router/       # 路由 + 演示账号守卫
├── stores/       # Pinia：user / raffle
├── styles/       # 全局样式
├── types/        # 后端 DTO 类型定义 + auto-imports 声明
├── utils/        # request 封装、errorMap、format
└── views/        # Home / Raffle / Account / Exchange / Records / admin
```

## 启动

### 方式一：整合进后端（一条命令，推荐）
前端构建产物会被自动拷贝到后端 `big-market-app/src/main/resources/static/`，
启动后端 Spring Boot 后直接访问 **http://localhost:8091** 即是前端页面，
`/api/v1/*` 同端口走 Controller，无跨域。

```bash
npm run build          # vue-tsc 类型检查 + vite build + 产物拷贝到后端 static
# 然后启动后端 Application（或 mvn spring-boot:run / 打包后运行）
```

注意：
- 前端每次改动需重新 `npm run build` 才会生效（静态资源被拷贝到 classpath；
  重启后端使新资源生效）。
- 深层路由（/raffle、/account 等）刷新已由后端 `WebMvcConfig` 回落 index.html。

### 方式二：前后端分离（开发调试）
```bash
npm install
npm run dev            # 走本地后端（vite proxy -> http://localhost:8091）
npm run dev:mock       # 纯前端联调（不依赖后端，使用 mock 数据）
```
- 开发地址：http://localhost:5173
- 环境变量：
  - `.env.development`：`VITE_API_BASE=/api/v1`、`VITE_USE_MOCK=false`
  - `.env.mock`：`VITE_USE_MOCK=true`（`npm run dev:mock` 读取）

## 构建

```bash
npm run build        # vue-tsc 类型检查 + vite build
npm run preview      # 预览构建产物
```

产物输出到 `dist/`，生产由 nginx 托管并将 `/api` 反代到后端 8091。
Element Plus 采用按需引入（unplugin-vue-components + unplugin-auto-import），
主包约 215 KB（gzip 84 KB）。

## 页面与功能

| 页面 | 路由 | 功能 | 后端接口 |
|---|---|---|---|
| 首页 | `/` | 活动列表，点击跳转抽奖 | `query_activity_list` |
| 抽奖 | `/raffle` | 大转盘 + 奖品池 | `query_raffle_award_list`、`draw`、`query_user_activity_account` |
| 我的账户 | `/account` | 额度概览 + 日历签到 | `query_user_activity_account`、`calendar_sign_rebate`、`is_calendar_sign_rebate` |
| 积分兑换 | `/exchange` | 积分换抽奖次数 | `query_sku_product_list_by_activity_id`、`query_user_credit_account`、`credit_pay_exchange_sku` |
| 中奖记录 | `/records` | 中奖列表 + 发奖状态 | `query_user_award_record` |
| 后台（运维） | `/admin/armory`、`/admin/dcc` | 活动/策略装配、DCC 开关 | `armory`、`strategy_armory`、`update_config` |

演示账号固定为 `xiaofuge`，右上角仅展示"演示账号"。

## 联调注意事项

1. 接口参数绑定：`query_sku_product_list_by_activity_id`、`query_user_credit_account`
   两个接口无 `@RequestBody`，前端统一走 query string；签到接口走 query；其余走 JSON body。
2. draw 可能返回降级码：0004（活动降级）、0005（限流）、0006（熔断），前端在
   `Raffle.vue` 做统一提示。
3. 抽奖接口有 Hystrix 3 秒超时 + 每用户每秒 1 次限流；后端 MQ 消息由
   `SendMessageTaskJob` 定时补发（不再阻塞请求线程）。
4. 兑换成功后库存为异步同步，不要立即用刷新接口验证。
5. mock 数据与 SQL 种子一致：userId=xiaofuge、activityId=100301、SKU=9011-9014。

## 一期范围说明

- 中奖记录接口 `query_user_award_record` 已实现（后端 + 前端）。
- 活动列表接口 `query_activity_list` 已实现（后端 + 前端），首页展示真实活动。
- 首页/抽奖台按产品要求精简：抽奖页只保留大转盘 + 奖品池。
