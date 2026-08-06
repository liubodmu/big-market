# big-market 应用镜像（后端 jar + 前端 static 产物一体）
# 构建前需先执行 mvn package 打出 big-market-app.jar（含前端 static）
# 构建: docker build -f docs/deploy/Dockerfile -t big-market-app:1.0 .
FROM openjdk:8-jre-slim

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 应用 jar（由 mvn package 生成，含 src/main/resources/static 前端页面）
ADD big-market-app/target/big-market-app.jar /big-market-app.jar

EXPOSE 8091

# 中间件连接地址由 docker-compose 通过环境变量注入（覆盖 application-prod.yml 的 127.0.0.1 默认值）
# 参考 docker-compose-app-prod.yml 中的 SPRING_ 前缀环境变量
ENTRYPOINT ["sh","-c","java -Xms256m -Xmx512m -jar /big-market-app.jar --spring.profiles.active=prod"]
