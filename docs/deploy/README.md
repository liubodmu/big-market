# big-market 云服务器部署指南

本指南面向 Linux 云服务器（Ubuntu/CentOS 均可），把后端 + 前端一体部署到 Docker。

## 0. 前置条件

- 一台 Linux 云服务器（建议 2C4G 以上）
- 服务器开放端口：**8091**（前端页面 + 后端接口）
- 代码已推送到你的 GitHub：`https://github.com/liubodmu/big-market`

## 1. 服务器装基础环境

```bash
# 装 Docker（含 compose 插件）
curl -fsSL https://get.docker.com | sh

# 装 git
apt update && apt install -y git

# 验证
docker --version
docker compose version
```

## 2. 拉取代码

```bash
git clone https://github.com/liubodmu/big-market.git
cd big-market
git checkout fix-240807-xfg-clear-queue-by-sku
```

## 3. 构建应用 jar + 镜像

```bash
# 打 jar（含前端 static 产物）
mvn -pl big-market-app -am package -DskipTests

# 构建应用镜像
docker build -f docs/deploy/Dockerfile -t big-market-app:1.0 .
```

> 如果服务器没装 Maven，用 `docker run` 装：`docker run --rm -v $PWD:/app -w /app maven:3.8-jdk-8 mvn -pl big-market-app -am package -DskipTests`

## 4. 启动全部（中间件 + 应用）

```bash
cd docs/deploy
docker compose -f docker-compose-app-prod.yml up -d
```

首次启动 MySQL 会执行 `docs/dev-ops/mysql/sql/*.sql` 自动建库建表（约 30 秒）。

## 5. 验证

```bash
# 看容器状态（app 应为 Up）
docker compose -f docs/deploy/docker-compose-app-prod.yml ps

# 看应用日志（出现 Started Application 即成功）
docker logs -f big-market-app

# 测试接口
curl http://localhost:8091/api/v1/raffle/activity/query_activity_list
curl http://localhost:8091/api/v1/raffle/activity/armory?activityId=100301
```

浏览器访问：**http://<服务器IP>:8091**

## 6. 更新代码后重新部署

```bash
git pull
mvn -pl big-market-app -am package -DskipTests
docker build -f docs/deploy/Dockerfile -t big-market-app:1.0 .
docker compose -f docs/deploy/docker-compose-app-prod.yml up -d --force-recreate app
```

## 常见问题

### 应用启动失败：连不上 MySQL
- 等 MySQL 健康检查通过再重启 app：`docker compose -f docs/deploy/docker-compose-app-prod.yml restart app`
- 确认 prod 配置连接地址是容器名（mysql/redis/rabbitmq），见 `big-market-app/src/main/resources/application-prod.yml`

### 前端页面打不开
- 确认 8091 端口在云安全组放行
- 前端已随 jar 打包（static 在 classpath），不需要单独部署前端

### 抽奖慢（本地 Windows Docker 环境）
- 云服务器 Linux 原生 Docker 性能远好于 Windows Docker Desktop，draw 应恢复亚秒级
- 若仍慢，可在 MySQL 容器配置 `--innodb-flush-log-at-trx-commit=2` 提升 commit 性能

### 备份数据库
```bash
docker exec big-market-mysql mysqldump -uroot -p123456 big_market > backup.sql
```

## 目录说明

| 文件 | 作用 |
|---|---|
| `docs/deploy/Dockerfile` | 应用镜像（后端 jar + 前端产物一体） |
| `docs/deploy/docker-compose-app-prod.yml` | 中间件 + 应用一体编排 |
| `big-market-app/src/main/resources/application-prod.yml` | prod 环境配置（连接地址支持环境变量覆盖） |
