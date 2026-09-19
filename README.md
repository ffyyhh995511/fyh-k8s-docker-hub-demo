# fyh-k8s-docker-hub-demo

用来练习 Docker 镜像构建和 Kubernetes 部署的 Spring Boot 示例。

- JDK 8
- Spring Boot 2.7.18
- 服务端口 `9981`
- 接口 `GET /hello`，返回本机 IP、毫秒时间戳和 `hello world`

## 本地启动

在项目目录执行：

```bash
mvn spring-boot:run
```

访问：`http://localhost:9981/hello`

## Dockerfile

文件是项目根目录的 `Dockerfile`，没有后缀。它不负责编译，只把已经打好的 jar 放进 JDK 8 运行镜像。

先打包，再构建镜像。镜像名和 `k8s/deploy.yaml` 里写的保持一致：

```bash
mvn clean package -DskipTests

docker build -t ffyyhh995511/fyh-k8s-docker-hub-demo:1.0.0 .
```

本地用容器跑一遍：

```bash
docker run --rm -p 9981:9981 ffyyhh995511/fyh-k8s-docker-hub-demo:1.0.0
```

访问：`http://localhost:9981/hello`

推到 Docker Hub。远程 Kubernetes 集群只能拉仓库里的镜像，本地 `docker build` 的结果集群看不到。

```bash
docker login
docker push ffyyhh995511/fyh-k8s-docker-hub-demo:1.0.0
```

换账号时，同时改 `docker build -t` 的名字和 `k8s/deploy.yaml` 里的 `image`。

## Kubernetes YAML

部署文件是 `k8s/deploy.yaml`，里面有两段：

- `Deployment`：跑 1 个副本，容器端口 `9981`，镜像 `ffyyhh995511/fyh-k8s-docker-hub-demo:1.0.0`
- `Service`：类型是 `NodePort`，集群内端口 `9981`，节点端口 `30981`

先确认 `kubectl` 连的是目标集群：

```bash
kubectl config current-context
kubectl get nodes
```

`get nodes` 里节点状态是 `Ready` 才算连上。要换集群：

```bash
kubectl config get-contexts
kubectl config use-context 上下文名称
```

然后部署：

```bash
kubectl apply -f k8s/deploy.yaml
kubectl get pods
kubectl get svc fyh-k8s-docker-hub-demo
```

访问：`http://节点IP:30981/hello`。当前这台机器的上下文是 Docker Desktop 时，节点 IP 用 `localhost`。

在 Rancher 里也可以不敲命令：进入集群对应命名空间，导入 YAML，把 `k8s/deploy.yaml` 贴进去。

删掉这次部署：

```bash
kubectl delete -f k8s/deploy.yaml
```

`imagePullPolicy` 现在是 `IfNotPresent`。本机 Docker Desktop 集群如果已经有这个镜像，不会再去仓库拉。部署到别的机器上的集群时，先 `docker push`，并把这一项改成 `Always`，否则节点上没有镜像时会拉取失败，或者一直用旧镜像。
