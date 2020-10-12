FROM openjdk:8-jdk-alpine
MAINTAINER ""
LABEL description="websocket proxy"

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk --no-cache add ca-certificates
#add CST
ENV TZ="Asia/Shanghai"
RUN apk --no-cache add tzdata && ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./target/proxy-1.0.0.jar /usr/local/app/proxy.jar
EXPOSE 8080
CMD java -jar /usr/local/app/proxy.jar