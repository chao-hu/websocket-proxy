FROM openjdk:8-jdk-alpine
MAINTAINER ""
LABEL description="k8s-terminal"

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk --no-cache add ca-certificates
#add CST
ENV TZ="Asia/Shanghai"
RUN apk --no-cache add tzdata && ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./target/k8s-terminal-1.0.0.jar /usr/local/app/k8s-terminal.jar
EXPOSE 8081
CMD java -jar /usr/local/app/k8s-terminal.jar