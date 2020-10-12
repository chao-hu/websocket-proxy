# websocket-proxy
java websocket proxy k8s docker terminal


  之所以有这个项目是因为联通天宫云上有多个区域部署cke集群（就是k8s集群），
每个集群里的容器都需要有终端访问。

  应用程序都是运行在cke里的， 采用sa认证的方式，
cke集群的api server外面并不能访问，所以没法试用apiserver+token的方式。 

   cke集群里的服务会有clb代理出来，这个是外面可以访问的。 
   
  proxy部署一套， k8s-terminal部署多套，每个cke集群里都部署一套。
  proxy可以访问k8s-terminal，通过clb代理后的地址来访问。 
  
  
     



    
  

