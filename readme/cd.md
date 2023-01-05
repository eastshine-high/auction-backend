# Jenkins, Docker, Nginx를 이용한 무중단 CD 구축

앞서, **Github Actions, Docker를 이용한 CI 구축** 에서는 변경 사항을 프로덕션에 배포할 준비를 마쳤습니다. 
이번 페이지에서는 **Jenkins, Docker, Nginx를 이용한 무중단 CD 구축**에 대해 다뤄 보겠습니다(이 글은 AWS EC2 사용에 대한 기본적인 이해를 전제로 작성하였습니다). 

Auction Backend의 CI/CD 구성도는 다음과 같습니다.

![http://dl.dropbox.com/s/m5u0e1r8w6uahlc/ci-cd.png](http://dl.dropbox.com/s/m5u0e1r8w6uahlc/ci-cd.png)

## Jenkins

Jenkins는 대표적인 CI/CD 도구 중의 하나입니다. 최근에는 Travis, Github Actions 등 다양한 CI/CD 도구들이 개발되어 사용되고 있지만, 여전히 많은 곳에서 Jenkins를 사용하고 있습니다.

### 1. AWS에 접속하기 위한 SSH Plugin 설치

AWS에 배포를 하기 위해서는 먼저 젠킨스에서 AWS에 접속할 수 있어야 합니다. 이를 위해 Plugin Manager에서 [SSH Pipeline Steps](https://plugins.jenkins.io/ssh-steps/) 플러그인을 설치합니다(이 외에도 여러 플러그인으로 구현 가능하며, Jenkins의 분산 빌드 아키텍처(Controller-Agent 구조)로 구현하는 것이 더 권장되는 방식입니다).

![http://dl.dropbox.com/s/ieu0eeqi7wje4uc/ssh](http://dl.dropbox.com/s/ieu0eeqi7wje4uc/ssh)

### 2. Jenkins Credentials에 AWS 접속을 위한 SSH 인증키 등록하기

SSH를 이용해 AWS EC2에 접속하기 위해서는 인증키가 필요합니다. 이 때 보안을 조금 생각하여, AWS로 부터 발급받은 `.pem` 키를 사용하지 않고 새로운 인증키를 생성하여 사용합니다.

```bash
$ ssh-keygen -t rsa -C "eastshine.high@gmail.com" -f eastshine_rsa -m PEM
```

- `-t` : 사용할 암호화 알고리즘을 지정합니다. `rsa` , `dsa` 등을 사용할 수 있습니다. ssh2 버전에서는 지정하지 않으면 `rsa` 를 사용합니다.
- `-C` : 생성할 공개키 내부에 커멘트(comment)를 추가합니다.
- `-f` : 파일의 이름을 지정합니다.
- `-m PEM` : 젠킨스에서 SSH 연결을 할 때 사용하는 [com.jcraft.jsch](http://www.jcraft.com/jsch/) 는 `----BEGIN OPENSSH PRIVATE KEY-----` 으로 시작하는 개인키를 지원하지 않습니다. 따라서 `----BEGIN RSA PRIVATE KEY---—` 으로 시작하는 PEM 형식의 개인키를 생성해야 합니다.

인증키가 정상적으로 생성된 것을 확인합니다.

```bash
$ ls -al
-rw-------   1 eastshine  staff  2459 12  9 14:07 eastshine_rsa
-rw-r--r--   1 eastshine  staff   593 12  9 14:07 eastshine_rsa.pub
```

AWS EC2에 생성한 공개키를 추가합니다.

```bash
(my-pc)$ ssh -i "AWS에서 발급 받은 PEM 키" ec2-user@ec2-"IPv4-address".ap-northeast-2.compute.amazonaws.com
(aws)$ vim ~/.ssh/authorized_keys
```

![http://dl.dropbox.com/s/hkzgu0i661aohev/ssh](http://dl.dropbox.com/s/hkzgu0i661aohev/ssh)

비공개키를 이용해 SSH 접속이 잘 되는 지 확인합니다.

```bash
(my-pc)$ chmod 600 eastshine_rsa
(my-pc)$ ssh -i "/{Path}/eastshine_rsa" ec2-user@ec2-"IPv4-address".ap-northeast-2.compute.amazonaws.com
			__|  __|_  )
       _|  (     /   Amazon Linux 2 AMI
      ___|\___|___|
```

정상적으로 SSH 접속이 된다면, 젠킨스 Credentials에 생성한 비공개키를 등록합니다.

![http://dl.dropbox.com/s/kg1i22tsb8l4ges/new](http://dl.dropbox.com/s/kg1i22tsb8l4ges/new)

이제 AWS에 SSH로 접속할 준비를 마쳤습니다. Jenkins에서 새로운 아이템을 추가하여 파이프라인을 작성합니다.

### 3. Jenkins 파이프라인 작성

```groovy
node {
    withCredentials([sshUserPrivateKey(credentialsId: 'eastshine-aws', keyFileVariable: 'identity')]) {
        def remote = [:]
        remote.name = "eastshine-aws"
        remote.host = "ec2-52-79-43-121.ap-northeast-2.compute.amazonaws.com"
        remote.allowAnyHosts = true
        remote.user = "ec2-user"
        remote.identityFile = identity

        stage("Deploy") {
            sshCommand remote: remote, command: "docker pull eastshine/auction-backend:${DOCKER_HUB_TAG}"
            sshCommand remote: remote, command: "docker tag eastshine/auction-backend:${DOCKER_HUB_TAG} auction-backend"
            sshCommand remote: remote, command: "docker stop server"
            sshCommand remote: remote, command: """
                                                docker run \
                                                    --name server \
                                                    -p 8080:8080 \
                                                    -v /home/ec2-user/application-prod.yml:/application-prod.yml \
																										-v /home/ec2-user/log:/log
                                                    -e SPRING_PROFILES_ACTIVE=prod \
                                                    --add-host=host.docker.internal:host-gateway \
                                                    -d \
                                                    --rm \
                                                auction-backend
                                                """
        }
    }
}
```

- `node` :  [SSH Pipeline Plugin](https://plugins.jenkins.io/ssh-steps/) 을 사용할 경우, remote `node` 에 명령어들을 작성합니다.
- `remote.*` : SSH Pipeline 플러그인의 대부분의 단계에는 사용자 이름, 비밀번호 등과 같은 원격 노드 설정의 Map인 `remote` 라는 공통 단계 변수가 필요합니다. romote의 키들에 대한 설명은 [공식 문서](https://plugins.jenkins.io/ssh-steps/#plugin-content-remote) 를 참조하였습니다.
- `withCredentials` : Credentials를 변수에 바인딩합니다. [Credentials Binding Plugin](https://plugins.jenkins.io/credentials-binding/) 은 기본 설정으로 젠킨스를 설치할 경우, 자동으로 설치되는 플러그인입니다.
- `sshUserPrivateKey` : Credentials에 있는 SSH key 파일을 임시 저장소에 복사한 뒤에, 변수들을 해당 위치(location)에 설정(set)합니다(파일은 빌드가 성공하면, 삭제됩니다). 다음과 같은 변수들이 있습니다. `keyFileVariable`, `credentialsId`, `passphraseVariable`, `usernameVariable` . 변수들에 대한 설명은 [Credentials Binding Plugin](https://plugins.jenkins.io/credentials-binding/) 문서를 통해 참조할 수 있습니다.
- `stage` : 젠킨스 파이프라인의 step들을 정의하는 영역입니다. 괄호 안에 여러 개의 step들을 정의할 수 있는데, 이 step들 내부에서 실제로 동작하는 내용들이 정의됩니다.
- `sshCommand` : [SSH Pipeline Plugin](https://plugins.jenkins.io/ssh-steps/) 이 지원하는 스텝 중 하나입니다. 리모트 노드에 명령을 실행하고 응답들을 출력합니다. SSH Pipeline Plugin은 추가적으로 `sshScript` , `sshPut` , `sshGet` , `sshRemove` 스텝을 지원합니다.

## Docker

앞서, [CI 과정](https://github.com/eastshine-high/auction-backend/blob/main/readme/ci.md) 에서는 도커 이미지를 빌드하고 Dockerhub(원격 저장소)에 Push하였습니다. 다음으로 Dockerhub(원격 저장소)에 있는 도커 이미지를 서버에 내려(pull)받고 실행(run)합니다. `docker run` 과 옵션에 대해서는 [Github](https://github.com/eastshine-high/til/blob/main/docker/docker-run.md) 에 좀 더 자세히 정리하였습니다.

젠킨스 파이프라인의 `stage("Deploy")` 에 작성한 도커 명령어들을 살펴보겠습니다.

```groovy
docker pull eastshine/auction-backend:${DOCKER_HUB_TAG}
docker tag eastshine/auction-backend:${DOCKER_HUB_TAG} auction-backend
docker stop server
docker run \
	--name server \
	-p 8080:8080 \
	-v /home/ec2-user/application-prod.yml:/application-prod.yml \
	-v /home/ec2-user/log:/log \
	-e SPRING_PROFILES_ACTIVE=prod \
	--add-host=host.docker.internal:host-gateway \
	-d \
	--rm \
auction-backend
```

- `docker pull` : Docker 저장소에서 이미지를 내려받습니다.
- `docker tag` : 태그는 이미지에 참조(reference)를 추가합니다.
- `docker stop server` : 프로세스의 이름이 `server` 인 docker 프로세스를 중지시킵니다.
- `docker run` : 컨테이너의 리소스들을 런타임에 정의합니다.
  - `-p` : 컨테이너 내부의 네트워크 포트를 컨테이너 외부에 노출(publish)하기 위해 사용합니다. `-p localhost에서 사용할 포트 번호 : 컨테이너 내부 포트 번호` 로 작성하여 포트를 매핑합니다.
  - `-v` : 도커 컨테이너에서 생성된 데이터를 영속화하기 위해 사용합니다. 또는 외부 파일 시스템에 영속화된 데이터를 컨테이너로 전달하기 위해서 사용합니다.
      - `-v /home/ec2-user/log:/log` : 컨테이너에서 생성된 로그를 외부 파일 시스템에 영속화합니다.
      - `-v /home/ec2-user/application-prod.yml:/application-prod.yml` : 외부 파일 시스템에 영속화되어있는 스프링 설정 파일을 컨테이너에 전달합니다.
  - `-e` : 컨테이너 내에서의 환경변수를 설정합니다.
  - `--add-host=host.docker.internal` : 컨테이너 내부와 로컬 호스트가 통신을 하기 위해 Keeper Connection Manager를 의미하는 `host.docker.internal` 를 컨테이너 호스트에 추가합니다. `host-gateway` 는 이 커넥션 매니저의 IP 주소(기본값 172.17.0.1)입니다.
      - 예를 들어, 도커 컨테이너 안에 있는 스프링 부트와 로컬 호스트에 있는 Redis가 통신을 하기 위해서는 위와 같은 설정이 필요합니다.
  - `-d` : 백그라운드(분리 모드)에서 컨테이너를 시작합니다.
  - `—rm` : 기본적으로 도커 컨테이너를 나가더라도(exit or stop) 컨테이너의 파일 시스템은 영속화 됩니다. `rm` 옵션을 사용하면, 컨테이너를 나갈 때 자동적으로 컨테이너 파일 시스템을 삭제합니다.

## 무중단 배포

위 과정을 통해 CD 구축을 완료하였습니다. 하지만 긴 시간은 아니지만, 배포하는 동안 애플리케이션이 종료된다는 문제가 남아있습니다. 새로운 Docker 컨테이너가 시작되기 전까지 기존 Docker 컨테이너를 종료시켜 놓기 때문에 서비스가 중단됩니다. 따라서 무중단 배포로 CD를 개선합니다. 무중단 배포는 Nginx, Docker compose, Shell script를 이용해 구현합니다.

### Nginx

Nginx는 웹 서버, 리버스 프록시, 캐싱, 로드 밸런싱, 미디어 스트리밍 등을 위한 오픈소스 소프트웨어입니다. Nginx의 리버스 프록시 기능을 활용하여 무중단 배포를 구현합니다. Nginx의 기초 사용법은 [Github](https://github.com/eastshine-high/til/blob/main/nginx/basic-usage.md) 을 통해 정리하였습니다.

먼저 엔진엑스의 설정 파일 `/etc/nginx/conf.d` 에서 `server` 컨텍스트를 수정합니다.

```purescript
server {
    listen 80;
    server_name {domain};

    include /etc/nginx/conf.d/service-url.inc;
    location / {
        proxy_pass          $service_url;
        proxy_set_header    X-Real-Ip $remote_addr;
        proxy_set_header    x-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header    Host $host;
    }
}
```

- `proxy_pass` : 리버스 프록시를 통해 외부의 요청을 어플리케이션 서버로 전달합니다.
- `include /etc/nginx/conf.d/service-url.inc` : `service url` 을 환경변수로 등록합니다.

```purescript
$ sudo vim /etc/nginx/conf.d/service-url.inc

set $service_url http://127.0.0.1:8081;
```

- `service url` 는 외부 요청을 전달할 어플리케이션 서버를 변경할 때 사용할 것입니다.
- `127.0.0.1` 를 `localhost` 처럼 별칭으로 사용하면 프록시 서버가 요청을 전달하지 못합니다.

### Docker compose

총 2개(blue, green)의 Docker compose를 작성합니다. 위에서 작성한 `docker run` 명령어와 크게 다르지는 않습니다.

docker-compose-blue.yml

```yaml
version: "3.9"
services:
  api:
    extra_hosts:
      - host.docker.internal:host-gateway
    image: "eastshine/auction-backend:${AUCTION_BACKEND_TAG}"
    ports:
      - "8081:8080"
    volumes:
      - "/home/ec2-user/application-prod.yml:/application-prod.yml"
      - "/home/ec2-user/log/blue:/log"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

docker-compose-blue.yml

```yaml
version: "3.9"
services:
  api:
    extra_hosts:
      - host.docker.internal:host-gateway
    image: "eastshine/auction-backend:${AUCTION_BACKEND_TAG}"
    ports:
      - "8082:8080"
    volumes:
      - "/home/ec2-user/application-prod.yml:/application-prod.yml"
      - "/home/ec2-user/log/green:/log"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

- `${AUCTION_BACKEND_TAG}` : 쉘의 환경변수를 통해 실행할 이미지의 Tag를 지정하여 사용합니다. 도커 컴포즈 파일에 파라미터를 전달할 때는 환경변수를 이용합니다.
- blue 컨테이너는 8081 포트를 포워딩하고, green 컨테이너는 8082 포트를 포워딩합니다.
- blue, green 컨터이너에 따라 별도의 로그 디렉토리를 사용합니다.

### **배포 스크립트 작성**

이제 배포하는 쉘 스크립트를 작성합니다.

`deploy.sh`

```purescript
#!/usr/bin/env bash

export AUCTION_BACKEND_TAG=$1 

# 1. Start idle docker container
EXIST_BLUE=$(docker-compose -p app-server-blue -f docker-compose-blue.yml ps | grep running)

if [ -z "$EXIST_BLUE" ]; then
    docker-compose -p app-server-blue -f ~/docker-compose-blue.yml up -d
    BEFORE_COMPOSE_COLOR="green"
    AFTER_COMPOSE_COLOR="blue"
    BEFORE_PORT_NUMBER=8082
    AFTER_PORT_NUMBER=8081
else
    docker-compose -p app-server-green -f ~/docker-compose-green.yml up -d
    BEFORE_COMPOSE_COLOR="blue"
    AFTER_COMPOSE_COLOR="green"
    BEFORE_PORT_NUMBER=8081
    AFTER_PORT_NUMBER=8082
fi

echo "app-server-${AFTER_COMPOSE_COLOR}(port:${AFTER_PORT_NUMBER}) started"
echo "Start health check"

# 2. Health check
SUCCESS_COUNT=0
for RETRY_COUNT in {1..10}
do
  sleep 10
  RESPONSE=$(curl -s http://localhost:${AFTER_PORT_NUMBER}/api/health)
  SUCCESS_COUNT=$(expr SUCCESS_COUNT + $(echo ${RESPONSE} | grep 'server is on' | wc -l))

  if [ ${SUCCESS_COUNT} -ge 3 ]
  then # $up_count >= 1 ("server is on" 문자열이 있는지 검증)
      echo "> Health check 3회 성공!"
      break
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> Health check 실패. ${RESPONSE}"
    echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "> Health check 시도(${RETRY_COUNT}/10), ${SUCCESS_COUNT}회 성공."
done

# 3. Switch nginx port
sudo sed -i "s/${BEFORE_PORT_NUMBER}/${AFTER_PORT_NUMBER}/" /etc/nginx/conf.d/service-url.inc
sudo nginx -s reload
echo "Deploy Completed!!"

# 4. Stop idle docker container
docker-compose -p app-server-${BEFORE_COMPOSE_COLOR} -f docker-compose-${BEFORE_COMPOSE_COLOR}.yml down
echo "Stop idle Docker(app-server-${BEFORE_COMPOSE_COLOR})"
```

- `1. Start idle docker container` :  blue container가 실행되고 있는지 확인 후 실행되고 있다면 green container를 생성하고 실행되고 있지 않다면 blue container를 생성합니다.
- `2. Health check` : 생성한 어플리케이션 컨테이너가 정상적으로 작동하고 있는지 핑을 보내 체크합니다. 10초 단위로 보내는 10번의 핑 중에서, 3번의 핑 테스트가 성공하면 정상 작동으로 판단합니다.
- `3. Switch nginx port` : 1. Health 테스트가 정상 통과했다면, service-url.inc에 포트를 바꿔주고 Nginx를 reload 시켜 80 port에 새로운 container를 바인딩합니다.
- `4. Stop idle Docker` : 마지막으로 사용하지 않는 컨테이너를 삭제합니다.

쉘 스크립트가 잘 작동하는 지 확인합니다.

![http://dl.dropbox.com/s/lm5j57s9jkji1gq/deploy-test.png](http://dl.dropbox.com/s/lm5j57s9jkji1gq/deploy-test.png)

### Jenkins 파이프라인 수정

Jenkins 파이프라인을 수정하여 작성한 쉘 스크립트를 사용합니다.

```purescript
node {
    withCredentials([sshUserPrivateKey(credentialsId: 'eastshine-aws', keyFileVariable: 'identity')]) {
        def remote = [:]
        remote.name = "eastshine-aws"
        remote.host = "ec2-52-79-43-121.ap-northeast-2.compute.amazonaws.com"
        remote.allowAnyHosts = true
        remote.user = "ec2-user"
        remote.identityFile = identity

        stage("Deploy") {
            sshCommand remote: remote, command: "./deploy.sh ${DOCKER_HUB_TAG}"            
        }
    }
}
```

Jenkins를 빌드하여 배포가 정상적으로 수행하는 지 확인합니다.

![http://dl.dropbox.com/s/hkk5450cbtf6cox/jenkins-test.png](http://dl.dropbox.com/s/hkk5450cbtf6cox/jenkins-test.png)
