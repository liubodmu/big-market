// 将前端构建产物拷贝到后端 Spring Boot 的 static 目录，
// 使「启动后端 -> 8091 直接访问前端页面」一条命令生效。
// 用法：npm run build 末尾自动执行
import { cpSync, mkdirSync, rmSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { dirname, join } from 'node:path'

const __dirname = dirname(fileURLToPath(import.meta.url))
const dist = join(__dirname, '../dist')
const target = join(__dirname, '../../big-market-app/src/main/resources/static')

mkdirSync(target, { recursive: true })
rmSync(target, { recursive: true, force: true })
cpSync(dist, target, { recursive: true })

console.log(`[copy-dist] 前端产物已拷贝到 ${target}`)
