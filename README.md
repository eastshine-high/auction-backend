# Auction backend

## 사용 기술

- Java 11, Gradle
- JPA, QueryDsl, Junit5, JWT, Json Merge Patch
- Spring Boot, Spring Data JPA, Spring REST Docs, Spring Security
- MariaDB 10, Redis, Kafka
- Devops : AWS EC2, Github Action, Jenkins, Nginx, Docker
- Design : 도메인 주도 설계, REST API

## 프로젝트 개요

Auction backend는 백엔드 개발 학습을 목적으로 쇼핑몰의 REST API를 설계하고 구현한 개인 프로젝트입니다. 서비스가 가능한 REST API를 목표로하여, 프로젝트 과정에서 고민하고 배운 내용들을 블로그 형식으로 기록해 나가고 있습니다.

## 목차
- [프로젝트를 통해 무엇을 할 수 있게 되었는가](#i-am-able-to)
- [프로젝트 문서](#document)
- [테스트](#test)
- [프로젝트 ERD](#entity-relationship-diagram)
- 지속적 통합 및 배포
    - [Github Actions, Docker를 활용한 CI 구축](#ci)
    - [Jenkins, Docker, Nginx를 활용한 무중단 CD 구축](#cd)
- 기능 설명과 설계, 구현 과정에서 배운 내용 정리
    - 공통 기능
        - [REST API의 예외(Exception) 처리](#exception)
        - [API의 보안(Security)](#security)
        - [Auditing](#auditing)
    - 주문
        - [도메인 모델링](#order)
        - [주문 프로세스(비동기 이벤트)](#order-process)
        - [Hibernate - MultipleBagFetchException 해결하기](#multiple-bag-fetch-exception)
    - 상품
        - [도메인 모델링](#product)
        - [재고 관리(동시성 이슈)](#stock)
        - [단일 책임 원칙과 URI 설계](#single-responsibility)
        - [Main-Sub 구조 엔터티 VS 계층(재귀) 구조 엔터티](#entity-design)
        - [상품 검색](#searching-product)
    - 사용자
        - [모델링](#user)
        - [인증(JWT)](#jwt)
        - [외래키와 복합키 사용에 대하여](#constraints)

- 코드 개선하기
    - [테스트 코드 작성을 통한 올바른 책임의 이해(캡슐화)](#test-responsibility)
    - [관심사의 분리](#separation-of-concern)
- [기술 사용 배경](#why-use)
    - Flyway
    - Redis
    - JSON Merge Patch
    - Kafka

## 프로젝트를 통해 무엇을 할 수 있게 되었는가 <a name = "i-am-able-to"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

- **데이터 모델링** - [데이터에 대한 접근](https://github.com/eastshine-high/til/blob/main/relational-database/data-access/database-storage-structure.md) 을 고려하여 테이블을 설계합니다. 설계에 정답있는 것은 아니며 Trade off를 하는 과정임을 이해합니다(사례. [Main-Sub 구조 엔터티 VS 계층(재귀) 구조 엔터티](#entity-design)).
- **ORM** - 복잡도가 있는 엔터티들을 JPA를 이용해 정의하고 Spring Data JPA와 QueryDsl을 이용하여 다룰(manage) 수 있습니다(사례. [Hibernate - MultipleBagFetchException 해결하기](#multiple-bag-fetch-exception)).
- **기술 사용** - 프로젝트 진행을 통해 문제들을 직접 겪어 보면서, 기술 사용의 이유를 이해하고 도입합니다(사례. [기술 사용 배경](#why-use)).
- **Config 파일 작성** - Config 파일들을 직접 작성하고 어플리케이션을 빌드해 보면서, Bean의 생명주기 등을 고려해볼 수 있었습니다.
- **테스트** - 단위, 통합 테스트 작성을 통해 견고한 소프트웨어를 만들고 기능들을 문서화합니다(사례. [테스트](#test)).
- **리눅스 운영** - CI/CD를 구축하고 AWS EC2에 Nginx, Redis, Docker를 운영해 보면서 리눅스 운영 능력을 기를 수 있었습니다.
- **HTTP 프로토콜** - [HTTP 프로토콜](https://github.com/eastshine-high/til/tree/main/web) 에 대한 이해는 REST API 개발, CI/CD를 구축 등의 작업을 스스로 학습하고 진행하는 데 많은 도움이 되었습니다.

</details>

## 프로젝트 문서 <a name = "document"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

- [API 문서(AWS 배포, Spring REST Docs 활용)](http://52.79.43.121/docs/index.html)

- [API 유스 케이스](https://eastshine.notion.site/5802417b375e474380a1a092e07e79fe?v=65b6e4f02626434597726a247cb3bf2e)

- [도메인 언어 탐구](https://github.com/eastshine-high/auction-backend/wiki/%EB%8F%84%EB%A9%94%EC%9D%B8-%EC%96%B8%EC%96%B4-%ED%83%90%EA%B5%AC)

</details>


## 테스트 <a name = "test"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

![](http://dl.dropbox.com/s/0s73r805xebz1nd/auction_test.png)

총 126개의 단위, 통합 테스트 진행

</details>

## 프로젝트 ERD <a name = "entity-relationship-diagram"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

![](http://dl.dropbox.com/s/ocsyfifqx945ere/auction_erd.png)

</details>

## 지속적 통합 및 배포

### Github Actions, Docker를 활용한 CI 구축 <a name = "ci"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

Auction Backend의 CI/CD 구성도는 다음과 같습니다. 최대한 여러 도구들을 활용해 보는 것을 목표로 두다 보니, 아래와 같은 CI/CD를 구성하게 되었습니다.

![http://dl.dropbox.com/s/m5u0e1r8w6uahlc/ci-cd.png](http://dl.dropbox.com/s/m5u0e1r8w6uahlc/ci-cd.png)

**이번 섹션에서는 Github Action과 Docker를 이용한 CI 구축**에 대해 설명하겠습니다.

## Github Actions

GitHub Actions는 CI/CD 플랫폼입니다. 특정 이벤트가 발생했을 때, 빌드, 테스트 및 배포 파이프라인을 자동으로 수행할 수 있도록 합니다. Github Actions을 사용하면 CI 시스템을 따로 구축하지 않아도 되는 장점이 있습니다. 따라서 CI 시스템을 구축(JDK 구성, Redis 구성 등)을 하는데 필요한 시간과 비용을 아낄 수 있습니다.

### Workflows

Github Action을 사용하기 위해서는 먼저 저장소에 Workflow를 작성해야 합니다. Workflow(작업 흐름)는 GitHub Actions에서 가장 상위 개념으로 쉽게 말해 자동화해놓은 작업 과정입니다. Workflow는 코드 저장소 내에서 `.github/workflows/` 디렉토리 내부에 `yml` 파일로 작성합니다. 필요에 따라 복수의 Workflow를 작성할 수 있습니다. 이 프로젝트에서는 아래와 같이 CI를 위한 Workflow 하나를 작성하였습니다.

[.github/workflows/ci.yml](https://github.com/eastshine-high/auction-backend/blob/main/.github/workflows/ci.yml)

```yaml
name: CI for Auction backend

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  ci-job:
    runs-on: ubuntu-20.04
    services:
      redis: # Label used to access the service container
        image: redis
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379

    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      - name: Build with Gradle
        run: ./gradlew clean build

      - name: Build docker
        run: |
		      docker build -t eastshine/auction-backend:${GITHUB_SHA::7} .
		      docker login -u ${{ secrets.USERNAME }} -p ${{ secrets.PASSWORD }}
		      docker push eastshine/auction-backend:${GITHUB_SHA::7}
```

- `name: CI for Auction backend` : 워크플로우의 이름입니다. 워크플로우가 어떤 용도로 사용되는 지 작성합니다. Github 저장소의 Actions 탭에서 `name` 으로 등록된 워크플로우의 리스트를 확인할 수 있습니다.

![http://dl.dropbox.com/s/fnn3p70y3yhium6/workflows.png](http://dl.dropbox.com/s/fnn3p70y3yhium6/workflows.png)

- `on` : 워크플로우가 언제 트리거가 되는 지를 지정합니다.

    ```yaml
    on:
      push:
        branches: [ main ]
      pull_request:
        branches: [ main ]
    ```

  위 워크플로우는 Github 저장소의 main 브렌치에 `push` 하거나 `pull_request` 를 했을 때 트리거됩니다. 추가적으로 특정 시간마다 워크플로우가 트리거 되기를 원할 경우에는 `schedule` 속성을 사용할 수 있습니다.


### Jobs

Job이란 독립된 가상 머신(machine) 또는 컨테이너(container)에서 돌아가는 하나의 처리 단위를 의미합니다. Workflow는 하나 혹은 여러 개의 Job으로 구성됩니다. Job들은 기본적으로 병렬로 수행됩니다(순서를 지정할 수도 있습니다).

```yaml
jobs:
  ci-job:
    runs-on: ubuntu-20.04
    services: 
      redis: 

	...
```

- `jobs` : 이 속성 하위에 작업들을 작성합니다. 하나 혹은 여러 개의 작업을 작성할 수 있습니다.
- `ci-job` : 작업의 식별자(ID)입니다.
- `runs-on: ubuntu-20.04` : job이 수행될 환경을 지정합니다. 리눅스 혹은 윈도우즈 등을 지정할 수 있으며, 필수 속성입니다.
- `services: redis` : Github Actions는 워크플로우 안에서 어플리케이션을 테스트하거나 동작시킬 때 필요한 호스트 서비스를 제공하는 데, 이를 [Service containers](https://docs.github.com/en/actions/using-containerized-services/about-service-containers) 라고 합니다. Service containers는 도커 컨테이너와 동일합니다. Java 프로젝트를 테스트하는 과정에서 Redis가 필요하므로 redis 컨테이너를 띄웁니다.

### Steps

Job 안에서 어떤 순서대로 명령을 수행하는 지를 Step들을 통해 작성합니다. 각 Step에서는 커맨드(command), 스크립트(script) 또는 **Action**들을 사용할 수 있습니다.

```yaml
steps:
  - uses: actions/checkout@v2
  - name: Set up JDK 11
    uses: actions/setup-java@v2
    with:
      java-version: '11'
      distribution: 'adopt'

  - name: Grant execute permission for gradlew
    run: chmod +x gradlew
  - name: Build with Gradle
    run: ./gradlew clean build

  - name: Build docker
    run: |
      docker build -t eastshine/auction-backend:${GITHUB_SHA::7} .
      docker login -u ${{ secrets.USERNAME }} -p ${{ secrets.PASSWORD }}
      docker push eastshine/auction-backend:${GITHUB_SHA::7}
```

- `steps` : 이 속성 하위에 스탭들을 작성합니다.
- `name` : 스탭의 이름을 지정합니다.

### Action

액션(Action)은 GitHub Actions의 꽃이라고도 볼 수 있습니다. 액션은 GitHub Actions에서 빈번하게 필요한 반복 단계를 재사용하기 용이하도록 제공되는 일종의 작업 공유 메커니즘입니다. 이 액션은 하나의 코드 저장소 범위 내에서 여러 워크플로우 간에서 공유를 할 수 있을 뿐만 아니라, 공개 코드 저장소를 통해 액션을 공유하면 GitHub 상의 모든 코드 저장소에서 사용이 가능해집니다.

- `uses: actions/checkout@v2` : Github 저장소로 부터 코드를 내려받기 위해 [Checkout 액션](https://github.com/actions/checkout) 을 사용합니다. 따라서 Checkout 액션은 Github Actions에서 거의 필수 액션으로 볼 수 있습니다.
- [uses: actions/setup-java@v2](https://github.com/actions/setup-java) - 필요한 버전의 Java를 다운로드하고 셋팅해주는 액션입니다. 또한 Maven, Gradle 등을 사용하여 배포할 수 있게, Github Actions의 러너(runner) 환경을 설정합니다.
- `run: ./gradlew clean build` : Gradle의 명령어를 이용하여 스프링 부트 프로젝트를 빌드합니다.

## Docker

Docker는 컨테이너 관리도구입니다. 컨테이너는 하나의 운영 체제 안에서 커널을 공유하며 개별적인 실행 환경을 제공하는 격리된 공간입니다. 여기서 개별적인 실행 환경이란 CPU, 네트워크, 메모리와 같은 시스템 자원을 독자적으로 사용하도록 할당된 환경을 말합니다.

이러한 컨테이너 기술을 사용하는 이유는 개발하는 환경과 배포 실행하는 환경을 일치시키기 위해서입니다. 보통 이것이 달라질 때 마다 문제가 자주 생깁니다. 그래서 도커를 사용하여 배포를 할 때 실행할 어플리케이션 뿐 아니라 애플리케이션이 실행되는 환경까지 같이 배포합니다.

### Dockerfile

도커를 사용하여 배포하기 위해서는 도커 이미지를 빌드해야 합니다. 먼저, 도커 이미지의 빌드 스크립트라 할 수 있는 `Dockerfile` 을 작성합니다.

[./Dockerfile](https://github.com/eastshine-high/auction-backend/blob/main/Dockerfile) 은 다음과 같이 작성하였습니다.

```docker
FROM openjdk:11.0.15
COPY ./app/build/libs/app.jar app.jar
CMD ["java", "-jar", "app.jar"]
```

- `FROM` : 새로운 빌드 단계를 초기화 하고 후속 지시어들을 위한 기본 이미지를 설정합니다. 따라서 유효한 `Dockerfile`은 `FROM` 지시어로 시작해야 합니다.
- `COPY` : 파일들이나 디렉토리들을 복사하여 컨테이너 파일시스템의 경로에 추가합니다.
- `CMD` : 컨테이너를 실행할 때, 기본값들을 제공합니다.

이 외에도 Dockerfile에는 다양한 지시어들이 있습니다. 이에 대한 내용은 [Github](https://github.com/eastshine-high/til/blob/main/docker/dockerfile.md) 을 통해 정리하였습니다.

Dockerfile을 작성했다면, 이제 도커 이미지를 빌드합니다. Github Actions의 워크플로우에서 도커와 관련한 부분을 마져 살펴보겠습니다.

```yaml
  - name: Build docker
    run: |
      docker build -t eastshine/auction-backend:${GITHUB_SHA::7} .
      docker login -u ${{ secrets.USERNAME }} -p ${{ secrets.PASSWORD }}
      docker push eastshine/auction-backend:${GITHUB_SHA::7}
```

- `docker build .`  : Dockerfile과 "context"에서 Docker 이미지를 빌드합니다. context는 현재 디렉토리 `.` 로 지정하였습니다. `Dockerfile` 은 기본적으로 context의 루트에 위치해야 합니다. 그렇지 않은 경우, `-f` 옵션을 이용해 파일 위치를 지정합니다.
- `-t eastshine/auction-backend:${GITHUB_SHA::7}` : 태그는 생성된 이미지를 참조합니다(refer). 태그의 형식은 `repository/name:tag` 를 의미합니다. `${GITHUB_SHA::7}` 은 깃헙 커밋 ID의 첫 7자리를 의미합니다.
- `docker login` : Docker 저장소에 로그인합니다.
- `docker push` : Docker 저장소에 빌드한 이미지를 푸시합니다.
- `secrets` 은 깃헙 저장소 Settings → Security → Secrets → Actions 탭에서 등록하여 사용할 수 있습니다.

![http://dl.dropbox.com/s/wja3vqur0gf1sz4/secrets.png](http://dl.dropbox.com/s/wja3vqur0gf1sz4/secrets.png)

빌드한 도커 이미지를 저장소에 푸시함으로써 지속적 통합(CI) 단계는 마무리 됩니다. 이제 변경 사항을 프로덕션에 배포(Deploy)할 준비를 마쳤습니다.

</details>

### Jenkins, Docker, Nginx를 이용한 무중단 CD 구축 <a name = "cd"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

Auction Backend의 CI/CD 구성도는 다음과 같습니다. 최대한 여러 도구들을 활용해 보는 것을 목표로 두다 보니, 아래와 같은 CI/CD를 구성하게 되었습니다.

![http://dl.dropbox.com/s/m5u0e1r8w6uahlc/ci-cd.png](http://dl.dropbox.com/s/m5u0e1r8w6uahlc/ci-cd.png)

앞서, [Github Actions, Docker를 이용한 CI 구축](https://www.notion.so/CI-CD-a7360ed159524140b9350105e8dd0cbf) 을 통해 변경 사항을 프로덕션에 배포할 준비를 마쳤습니다. **이번 섹션에서는 Jenkins와 Docker를 이용하여 AWS EC2에 배포 자동화**하는 과정에 대해 다루겠습니다. 이 글은 AWS EC2 사용에 대한 기본적인 이해를 전제로 작성하였습니다.

### Jenkins

Jenkins는 대표적인 CI/CD 도구 중의 하나입니다. 최근에는 Travis, Github Actions 등 다양한 CI/CD 도구들이 개발되어 사용되고 있지만, 여전히 많은 곳에서 Jenkins를 사용하고 있습니다.

1. **(Jenkins) AWS에 접속하기 위한 SSH Plugin 설치**

AWS에 배포를 하기 위해서는 먼저 젠킨스에서 AWS에 접속할 수 있어야 합니다. 이를 위해 Plugin Manager에서 [SSH Pipeline Steps](https://plugins.jenkins.io/ssh-steps/) 플러그인을 설치합니다(이 외에도 여러 플러그인으로 구현 가능하며, 분산 빌드 아키텍처(Controller-Agent 구조)로 구현하는 것이 더 권장할만한 방식입니다).

![http://dl.dropbox.com/s/ieu0eeqi7wje4uc/ssh](http://dl.dropbox.com/s/ieu0eeqi7wje4uc/ssh)

2. **Jenkins Credentials에 SSH 인증키 등록하기**

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

1. **Jenkins 파이프라인 작성**

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

### Docker

이번에는 젠킨스 파이프라인의 `stage("Deploy")` 에 작성한 도커 명령어들을 살펴보겠습니다.

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
    - `docker run` 과 옵션에 대해서는 [Github](https://github.com/eastshine-high/til/blob/main/docker/docker-run.md) 에 좀 더 자세히 정리하였습니다.
- `-p` : 컨테이너 내부 포트를 컨터에너 외부에 노출하기 위해 사용합니다. `-p 외부 노출 포트 번호 : 내부 포트 번호` 로 작성합니다.
- `-v` : 도커 컨테이너에서 생성된 데이터를 영속화하기 위해 사용합니다. 또는 외부 파일 시스템에 영속화된 데이터를 컨테이너로 전달하기 위해서 사용합니다.
    - `-v /home/ec2-user/log:/log` : 컨테이너에서 생성된 로그를 외부 파일 시스템에 영속화합니다.
    - `-v /home/ec2-user/application-prod.yml:/application-prod.yml` : 외부 파일 시스템에 영속화되어있는 스프링 설정 파일을 컨테이너에 전달합니다.
- `-e` : 컨테이너 내에서의 환경변수를 설정합니다.
- `--add-host=host.docker.internal` : 컨테이너 내부와 로컬 호스트가 통신을 하기 위해 Keeper Connection Manager를 의미하는 `host.docker.internal` 를 컨테이너 호스트에 추가합니다. `host-gateway` 는 이 커넥션 매니저의 IP 주소(기본값 172.17.0.1)입니다.
    - 예를 들어, 도커 컨테이너 안에 있는 스프링 부트와 로컬 호스트에 있는 Redis가 통신을 하기 위해서는 위와 같은 설정이 필요합니다.
- `-d` : 백그라운드(분리 모드)에서 컨테이너를 시작합니다.
- `—rm` : 기본적으로 도커 컨테이너를 나가더라도(exit or stop) 컨테이너의 파일 시스템은 영속화 됩니다. `rm` 옵션을 사용하면, 컨테이너를 나갈 때 자동적으로 컨테이너 파일 시스템을 삭제합니다.

## 무중단 배포

위 과정을 통해 CD 구축을 완료하였습니다. 하지만 긴 시간은 아니지만, 배포하는 동안 애플리케이션이 종료된다는 문제가 남아있습니다. 새로운 Docker 컨테이너가 시작되기 전까지 기존 Docker 컨테이너를 종료시켜 놓기 때문에 서비스가 중단됩니다. 따라서 무중단 배포로 CD를 개선합니다.

무중단 배포는 Docker compose와 Nginx를 이용해 구현하였습니다.

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

- blue 컨테이너는 8081 포트를 포워딩하고, green 컨테이너는 8082 포트로 포워딩합니다.
- log 또한 blue, green에 따라 별도의 디렉토리에 기록합니다.
- `${AUCTION_BACKEND_TAG}` : 서버의 환경변수를 통해 실행할 Docker 이미지의 Tag를 지정합니다.

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
- 작성한 쉘 스크립트로 빌드를 진행합니다.

Jenkins를 이용해 빌드가 잘 수행되는지 확인합니다.

![http://dl.dropbox.com/s/hkk5450cbtf6cox/jenkins-test.png](http://dl.dropbox.com/s/hkk5450cbtf6cox/jenkins-test.png)

</details>

## 기능 설명 및 설계, 구현 과정에서 배운 내용 정리

## 공통 기능

### REST API의 예외(Exception) 처리 <a name = "exception"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

(1) 일관성 있는 오류 표현

[REST API 디자인 규칙(마크 마세 저)](https://digital.kyobobook.co.kr/digital/ebook/ebookDetail.ink?selectedLargeCategory=001&barcode=480D150507640&orderClick=LAG&Kc=) 에서는 “오류는 일관성 있게 표현하여 응답”하는 것을 권합니다.

```json
{
    "errorCode" : "PRODUCT_NOT_FOUND",
    "message" : "상품을 찾을 수 없습니다."
}
```

이를 위해 공통으로 사용할 오류 응답을 정의합니다.

```java
public class ErrorResponse {
    private String errorCode;
    private String message;
}
```

오류 응답(`ErrorResponse`)의 `errorCode`와 `message`는 내부적으로 Enum을 통해 관리합니다.

```java
public enum ErrorCode {
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다."),
    PRODUCT_UNACCESSABLE("상품에 대한 접근 권한이 없습니다.");

    private final String errorMsg;

    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
```

위에서 정의한 Enum(`ErrorCode`)을 기반으로 예외를 처리하기 위해서는 `RuntimeException`을 상속하여 클래스를 정의해야 합니다.

```java
@Getter
public class BaseException extends RuntimeException {
    private ErrorCode errorCode;

    public BaseException() {
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getErrorMsg());
        this.errorCode = errorCode;
    }
}
```

그리고 `RuntimeException`을 상속한 기반 클래스(`BaseException`) 를 다시 상속하여 실제 비즈니스 로직에서 표현할 예외 클래스들을 만듭니다.

![http://dl.dropbox.com/s/g3rwsw09kf8l2rs/exception%20hierarchy.png](http://dl.dropbox.com/s/g3rwsw09kf8l2rs/exception%20hierarchy.png)

이제 '일관성 있는 오류 표현'을 위한 준비는 마쳤습니다.  

(2) HTTP 응답 상태 코드

REST API는 HTTP 응답 메시지의 Status-Line을 사용하여 클라이언트가 요청한 결과를 알려줍니다. 이 때, 오류의 응답 상태 코드는 ‘4xx’ 또는 ‘5xx’ 중 하나여야 합니다.

위의 다이어그램에서 `BaseException` 상속한 클래스들은 HTTP 응답 상태 코드의 표현이기도 합니다. 예를 들어 `EntityNotFoundException`는 아래와 같이 404 상태 코드를 응답합니다.

```java
@Slf4j
@ControllerAdvice
public class ControllerErrorAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ErrorResponse onEntityNotFoundException(EntityNotFoundException e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.error("[EntityNotFoundException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        return ErrorResponse.of(e.getMessage(), e.getErrorCode().name());
    }
}
```

- 스프링은 API 예외 처리 문제를 해결하기 위해 몇 가지 어노테이션을 지원합니다.
    - `@ControllerAdvice` : 모든 예외를 한 곳에서 처리하기 위해 선언합니다.
    - `@ExceptionHandler` : 처리하고 싶은 예외를 지정합니다.
- 초기에 정의한 `ErrorResponse` 를 사용하여 일관성 있는 표현으로 오류를 응답합니다.

이제 REST API의 예외(Exception) 처리를 위한 준비를 마쳤습니다. 비즈니스 로직에서는 이를 활용하여 예외룰 처리합니다.

```java
@RequiredArgsConstructor
@Service
public class ProductService {
	private final ProductRepository productRepository;

	public Product findProduct(Long id) {
	  return productRepository.findById(id)
	          .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
	}
}
```

- `new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND)`
    - 식별자의 엔터티를 찾지 못했을 경우, 이를 표현하는 `EntityNotFoundException` 을 던집니다.
    - `ErrorCode.PRODUCT_NOT_FOUND` 를 통해 미리 정의된 메세지로 예외 상황을 설명합니다.

</details>

### API의 보안(Security) <a name = "security"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

API는 일부 사용자만 접근을 허용해야할 때가 있습니다. 이러한 보안을 위해서는 인증(당신은 누구입니까)과 인가(당신은 무엇을 할 수 있습니까) 과정이 필요합니다. Spring Security는 서블릿 애플리케이션에서의 인증(Authentication) 및 인가(Authentication) 처리를 지원하므로 이를 이용해 API의 보안 처리를 합니다.

먼저 사용자 도메인에서 [로그인을 통한 인증으로 JWT를 발급](https://github.com/eastshine-high/auction-backend#jwt) 하였습니다. API 보안에서는 Spring Security를 이용하여 사용자 도메인에서 발급한 JWT를 인증한 뒤, 인가 처리를 합니다.

예를 들어 아래는 상품 정보의 수정을 요청하는 HTTP 요청 메세지입니다. HTTP의 `Authorization` 헤더에 발급받은 토큰을 넣어 요청합니다.

```java
PATCH /seller-api/v1/products/1 HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJ1c2VySW5mbyI6eyJpZCI6MSwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsIm5pY2tuYW1lIjoibmlja25hbWUiLCJyb2xlcyI6WyJVU0VSIiwiU0VMTEVSIl19fQ
Content-Length: 160
Host: 52.79.43.121

{
  "name" : "modify name",
  "price" : 99999,
  "stockQuantity" : 30,
}
```

**Spring Security Architecture**

Spring MVC에 위의 요청이 들어오면, 아래와 같은 흐름을 통해 컨트롤러에 전달됩니다.

```
HTTP 요청 -> WAS -> (서블릿)필터 -> 서블릿(dispatcher) -> 스프링 인터셉터 -> 컨트롤러
```

Spring Security는 위 흐름 중에서 서블릿 필터를 기반으로 동작합니다([Spring Security의 구조](https://github.com/eastshine-high/til/blob/main/spring/spring-security/architecture.md) 에 대한 자세한 설명은 Github을 통해 정리하였습니다). `Authorization` 헤더에 담긴 JWT 를 인증하기 위해서는 스프링 시큐리티 구성(Config)에 이를 인증하기 위한 필터를 추가합니다.

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
    AuthenticationService authenticationService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), authenticationService);
        Filter authenticationErrorFilter = new JwtAuthenticationErrorFilter();

        http
                .csrf().disable()
                .headers()
                    .frameOptions().disable() // H2 데이터베이스 콘솔을 위한 설정.
                .and()
                .addFilter(authenticationFilter)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class);
    }
}
```

- `WebSecurityConfigurerAdapter` : `WebSecurity` 를 위한 구성 인스턴스를 생성할 때, 편리한 기본 클래스를 제공합니다. 일반적으로 `@EnableWebSecurity` 어노테이션과 함께 사용합니다. 필요한 메서드를 재정의하여 커스텀할 수 있습니다.
- `HttpSecurity` : 네임스페이스 구성에서 Spring Security의 XML `<http>` 엘러먼트와 유사합니다. 특정 `http` 요청에 대해 웹 기반 보안을 구성할 수 있습니다. 기본적으로 모든 요청에 적용되지만 `requestMatcher(RequestMatcher)` 또는 다른 유사한 방법을 사용하여 제한할 수 있습니다.
- `csrf().disable()` : 현재 서버는 REST API로만 사용하므로 `csrf` 를 사용하지 않았습니다.
- `@EnableGlobalMethodSecurity` : 아래 인가 처리에서 메소드 시큐리티를 활성화하기 위해 사용합니다.

**인증(Authentication)**

위의 시큐리티 구성(`SecurityConfig`)에 등록한 [JwtAuthenticationFilter](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/security/JwtAuthenticationFilter.java) 를 살펴보겠습니다.

```java
package com.eastshine.auction.common.filters;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
		private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        if(Objects.nonNull(authorization)){
						String accessToken =  authorization.substring("Bearer ".length());
            Long userId = authenticationService.parseToken(accessToken);
            UserInfo userInfo = authenticationService.findUserInfo(userId);
            Authentication authentication = new UserAuthentication(userInfo);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
```

- `BasicAuthenticationFilter` : 위의 시큐리티 구성(`SecurityConfig`)에서 사용한 `addFilter` 메소드는 스프링 시큐리티에서 제공하는 필터만 등록이 가능합니다. 따라서 스프링 시큐리티에서 제공하는 필터 중의 하나인 `BasicAuthenticationFilter` 를 상속하여 필터를 구현하였습니다. `BasicAuthenticationFilter` 는 내부적으로 `OncePerRequestFilter` 를 상속합니다.
- `authenticationService.parseToken` : JWT을 인증하고 파싱합니다. JWT에 대해서는 [로그인 인증](https://github.com/eastshine-high/auction-backend#jwt) 에서 설명하므로, 여기서는 관련 내용의 링크만 참조하겠습니다.
    - [JWT 정리 및 활용](https://github.com/eastshine-high/til/blob/main/web/jwt.md)
    - [AuthenticationService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/user/application/AuthenticationService.java)
- `Authentication` : JWT가 인증되었다면, 누가 인증되었는 지를 나타내는 `Authentication` 객체를 통해 이를 표현합니다.
- `authenticationService.findUserInfo` : [로그인 인증](https://github.com/eastshine-high/auction-backend#jwt) 에서는 보안 상의 이유로 사용자의 식별 정보만을 JWT 페이로드에 담았었습니다. 따라서 인증 객체(Authentication)를 생성할 때 필요한 사용자 권한 등의 추가 정보를 데이터베이스(Redis)에서 조회합니다.
- `SecurityContextHolder` 는 스프링 시큐리티 인증 모델의 핵심입니다. 스프링 시큐리티를 통해 인증하는 가장 간단한 방법은 `SecurityContextHolder` 에 누가 인증되었는 지를 직접 설정하는 것입니다(스프링 시큐리티의 다른 필터들과 통합하여 사용하지 않을 경우, `AuthenticationManager` 를 사용하지 않고 `SecurityContextHolder` 를 직접 사용하여 인증할 수 있습니다).

![https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/securitycontextholder.png](https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/securitycontextholder.png)

- `new UserAuthentication(userInfo)` : 누가 인증되었는 지를 나타내는 `Authentication` 은 인터페이스이므로 이를 구현해 사용해야 합니다.

[UserAuthentication](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/security/UserAuthentication.java)

```java
public class UserAuthentication extends AbstractAuthenticationToken {
    private final UserInfo userInfo;

    public UserAuthentication(UserInfo userInfo) {
        super(authorities(userInfo));
        this.userInfo = userInfo;
    }

    private static List<GrantedAuthority> authorities(UserInfo userInfo) {
        if(Objects.isNull(userInfo.getRoles())){
            return new ArrayList<>();
        }

        return userInfo.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userInfo;
    }
}
```

- `AbstractAuthenticationToken` : Authentication 인터페이스를 구현한 기본 클래스입니다. Authentication을 처음부터 구현하지 않고 기본 클래스인 `AbstractAuthenticationToken`을 상속하여 구현합니다.
- `SimpleGrantedAuthority` : `GrantedAuthority` 인터페이스를 구현하는 가장 간단한 구현 클래스입니다.
- `credentials` : 암호를 보관하는 필드이나, 사용자 인증이 된 후에는 암호 유출 방지를 위해 일반적으로 비웁니다.

**인가(Authorization)**

인가 처리는 [메소드 시큐리티](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html) 를 이용하여 처리하였습니다. 보호가 필요한 API에 어노테이션을 추가하여 리소스를 보호합니다.

```java
@PatchMapping("/seller-api/v1/products/{id}")
@PreAuthorize("hasAuthority('SELLER')")
public ResponseEntity patchProduct(@PathVariable Long itemId, @RequestBody @Validated PatchProductDto productDto) {
        ...
}
```

- `@PreAuthorize("hasAuthority('SELLER')")` : 인증된 요청자이며 SELLER 권한이 있는 사용자만 이 API에 접근할 수 있습니다.

</details>

### Auditing <a name = "auditing"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

ORM에서 Auditing은 영속 엔터티와 관련된 이벤트를 추적하고 로깅하는 것을 의미합니다. 여기서 이벤트란 SQL 트리거에서 영감을 얻어은 것으로 삽입, 수정, 삭제 작업을 의미합니다.

이 프로젝트에서는 Spring Data JPA의 Auditing을 사용하여 변경 시점 혹은 사람에 대한 추적이 필요한 엔터티들에 적용합니다. Auditing을 적용하는 과정은 [Github](https://github.com/eastshine-high/til/blob/main/spring/spring-data/spring-data-jpa/auditing.md) 을 통해 정리하였습니다.

</details>


## 주문 도메인 <a name = "order"></a>

### 도메인 모델링

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

다음은 도메인 주도 설계 개념을 적용한 **주문 도메인 모델**입니다.

![http://dl.dropbox.com/s/0wfivcgtgx49awf/order_diagram.png](http://dl.dropbox.com/s/0wfivcgtgx49awf/order_diagram.png)

주문 에그리거트는 루트 엔터티인 주문(Order), Order의 값 객체(VO)인 배달정보(DeliveryFragment), Order와 일대다 관계인 주문물품(OrderItem), OrderItem과 일대다 관계인 주문물품옵션(OrderItemOption)으로 구성됩니다.

### 데이터 모델링

![http://dl.dropbox.com/s/pgedo149dlo3buf/order_erd.png](http://dl.dropbox.com/s/pgedo149dlo3buf/order_erd.png)

</details>

### 주문 프로세스(비동기 이벤트)<a name = "order-process"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

**주문하기**

주문하기 업무는 여러 도메인과의 협력을 통해 진행됩니다. 이 때, 이벤트를 활용하면 도메인 간의 결합도를 낮출 수 있습니다. 따라서 주문 성공에 대한 알림 메일은 Kafka를 이용해 비동기 이벤트 처리하였습니다(사실 MSA가 아닌 모놀리틱 아키텍처에서 비동기 이벤트 처리는 Spring이 지원하는 이벤트 기능만으로 충분합니다. Kafka는 기술 사용 연습 차원에서 사용하였습니다).

![http://dl.dropbox.com/s/auchgbr2ovvvajd/place_order_flow.png](http://dl.dropbox.com/s/auchgbr2ovvvajd/place_order_flow.png)

- 재고 차감은, 재고 차감의 성공 여부에 따라 주문 결과가 달라지므로 이벤트로 처리하지 않고, 상품 도메인의 재고 서비스를 의존성하여 협력하였습니다(MSA 구조에서는 동기 통신으로 협력합니다).

[PlaceOrderService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/application/PlaceOrderService.java) - 주문하기 서비스는 다음과 같이 표현하였습니다. 

```java
@RequiredArgsConstructor
@Service
public class PlaceOrderService {
    private final ProductStockService productStockService;
    private final OrderService orderService;
    private final PlaceOrderProducer placeOrderProducer;

    @Transactional
    public Order placeOrder(OrderDto.PlaceOrderRequest request) {
        request.getOrderItems().stream()
                .forEach(productStockService::decreaseStock); // 1. 재고 차감
        Order registeredOrder = orderService.registerOrder(request);// 2. 주문 등록
        placeOrderProducer.sendMail(request.getUserInfo(), registeredOrder); // 3. 메일 발송 이벤트 발행
        return registeredOrder;
    }
}
```

- [PlaceOrderProducer](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/adaptor/PlaceOrderProducer.java) 는 주문하기 서비스의 이벤트 발행을 담당합니다.
- `PlaceOrderService` 는 퍼사드로도 표현할 수도 있습니다.

<br>

**주문 취소**

주문 취소의 경우, 모든 도메인과 비동기 이벤트를 통해 협력합니다. 재고 차증은 재고 차감과 달리 재고 부족 문제가 발생하지 않으므로 비통기 이벤트로 처리합니다.

![http://dl.dropbox.com/s/3ihq122y08jnulp/cancel_order_flow.png](http://dl.dropbox.com/s/3ihq122y08jnulp/cancel_order_flow.png)

[CancelOrderService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/application/CancelOrderService.java) - 주문 취소 서비스는 다음과 같이 표현하였습니다. 

```java
@RequiredArgsConstructor
@Service
public class CancelOrderService {
    private final OrderRepository orderRepository;
    private final CancelOrderPolicy cancelOrderPolicy;
    private final CancelOrderProducer cancelOrderProducer;    

    @Transactional
    public void cancelOrder(Long orderId, UserInfo userInfo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
        cancelOrderPolicy.validateCancelableOrder(order, userInfo);

        order.cancel(); // 1. 주문 취소
        cancelOrderProducer.increaseStock(order); // 2. 재고 차증
        cancelOrderProducer.sendMail(userInfo, order); // 3. 메일 발송 이벤트 발행
    }
}
```

- [CancelOrderPolicy](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/domain/policy/CancelOrderPolicy.java) - 주문 취소 정책을 통해, 취소가 가능한 주문인지 확인합니다.
- [CancelOrderProducer](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/adaptor/CancelOrderProducer.java) - 주문 취소 서비스의 이벤트 발행을 담당합니다.

</details>

### Hibernate - MultipleBagFetchException 해결하기 <a name = "multiple-bag-fetch-exception"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

`MultipleBagFetchException` 는 JPA의 N+1 문제에 대한 해결책으로 Fetch Join을 사용하다보면 자주 만나는 문제입니다. `MultipleBagFetchException` 는 2개 이상의 ToMany 자식 테이블에 Fetch Join을 선언했을 때 발생합니다.

여기서 `Bag` 이란 `org.hibernate.type.BagType` 을 의미합니다. Bag(Multiset)은 Set과 같이 순서가 없고, List와 같이 중복을 허용하는 자료구조입니다. 하지만 자바 컬렉션 프레임워크에서는 Bag이 없기 때문에 하이버네이트에서는 List를 Bag으로써 사용하고 있습니다.

### 문제 상황

먼저 주문 엔터티의 구조와 정의는 다음과 같습니다.

![http://dl.dropbox.com/s/5jb5mnu9h8vcmed/order_entity_diagram.png](http://dl.dropbox.com/s/5jb5mnu9h8vcmed/order_entity_diagram.png)

```java
@Entity
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems = new ArrayList<>();

		...
}
```

```java
@Entity
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @JoinColumn(name = "order_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.PERSIST)
    private List<OrderItemOption> orderItemOptions = new ArrayList();

		...
}
```

```java
@Entity
public class OrderItemOption  {

    @Id @Column(name = "order_item_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "order_item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderItem orderItem;

		...
}
```

위에서 정의한 주문 에그리것을 QueryDsl을 이용하여 조회합니다. N + 1 문제를 방지하기 위해 `@OneToMany` 관계는 모두 `fetchJoin` 을 사용해 조회하였습니다.

```java
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory query;

    public OrderRepositoryCustomImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<Order> findByIdWithFetchJoin(Long orderId) {
        return Optional.ofNullable(
                query.selectFrom(order)
                        .join(order.orderItems, orderItem).fetchJoin()
                        .leftJoin(orderItem.orderItemOptions, orderItemOption).fetchJoin()
                        .where(order.id.eq(orderId))
                        .fetchOne()
        );
    }
}
```

하지만 다음과 같이 `MultipleBagFetchException` 이 발생합니다.

```
org.springframework.dao.InvalidDataAccessApiUsageException: 
org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags: 
[com.eastshine.auction.order.domain.Order.orderItems, com.eastshine.auction.order.domain.item.OrderItem.orderItemOptions]; 
nested exception is java.lang.IllegalArgumentException: 
org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags: 
[com.eastshine.auction.order.domain.Order.orderItems, com.eastshine.auction.order.domain.item.OrderItem.orderItemOptions]
```

### 문제 해결

**해결 1**

먼저, [Stack overflow](https://stackoverflow.com/questions/4334970/hibernate-throws-multiplebagfetchexception-cannot-simultaneously-fetch-multipl) 를 통해 `List` 자료형을 `Set` 자료형으로 바꾸어 해결할 수 있다는 것을 확인할 수 있습니다. 하지만 **Set 자료형을 사용할 경우에는 다음과 같은 단점**이 있었습니다.

```java
@EqualsAndHashCode(of = "id")
@Entity
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private Set<OrderItem> orderItems = new HashSet<>();

		public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }
		...
}
```

1. [CascadeType.PERSIST](https://github.com/eastshine-high/til/blob/main/java/specification/jpa-hibernate/persistence-context/cascading-entity-state-transitions.md) 를 이용해 여러 자식 엔터티를 한 번에 영속화 하기가 어렵습니다. `id` 로 Equals와 HashCode 메소드가 오버라이드 되어있는 엔터티가 [비영속(transient) 상태](https://github.com/eastshine-high/til/blob/main/java/specification/jpa-hibernate/persistence-context/persistent-data-status.md) 일 때, Set 자료형에서는 `id` 값이 모두 `null` 이므로 같은 객체로 취급됩니다. 따라서 Set의 `add` 메소드를 사용하기 어려우므로 `CascadeType.PERSIST` 를 이용해 복수의 자식 엔터티들을 한 번에 영속화하기가 어렵습니다.
2. 성능적 문제 - 지연 로딩으로 컬렉션을 조회했을 경우, 컬력션이 아직 초기화 되지 않은 상태에서 컬렉션에 값을 넣게 되면 프록시가 강제로 초기화 되는 문제가 발생합니다. 왜냐하면 중복 데이터가 있는지 비교해야 하는데, 그럴러면 컬렉션에 모든 데이터를 로딩해야 하기 때문입니다.

**해결 2**

`MultipleBagFetchException` 이라는 예외의 이름처럼 `ToMany` 에 대한 Fetch Join은 한 번만 사용할 수 있습니다. 따라서 또 다른 N + 1 문제의 해결 방법인 **Hibernate default_batch_fetch_size**를 이용합니다.

스프링부트에서는 다음과 같이 옵션을 적용할 수 있습니다 - application.yml

```yaml
spring:jpa:properties:hibernate.default_batch_fetch_size: 1000
```

위에서 작성한 Querydsl의 조회 문장도 수정하여 `leftJoin` 문 하나를 제거하였습니다.

```java
@Override
public Optional<Order> findByIdWithFetchJoin(Long orderId) {
    return Optional.ofNullable(
            query.selectFrom(order)
                    .join(order.orderItems, orderItem).fetchJoin()
                    .where(order.id.eq(orderId))
                    .fetchOne()
    );
}
```

수행된 SQL을 확인해 보면 Join으로 처리하지 않은 부분은 `IN` 절로 처리되어 N+1 문제 발생 없이 조회가 되는 것을 확인할 수 있습니다.

```
select
    orderitemo0_.order_item_id as order_it8_4_1_,
    orderitemo0_.order_item_option_id as order_it1_4_1_,
    orderitemo0_.order_item_option_id as order_it1_4_0_,
    orderitemo0_.created_at as created_2_4_0_,
    orderitemo0_.updated_at as updated_3_4_0_,
    orderitemo0_.additional_price as addition4_4_0_,
    orderitemo0_.item_option_id as item_opt5_4_0_,
    orderitemo0_.item_option_name as item_opt6_4_0_,
    orderitemo0_.order_count as order_co7_4_0_,
    orderitemo0_.order_item_id as order_it8_4_0_ 
from
    order_item_option orderitemo0_ 
where
    orderitemo0_.order_item_id in (
        ?, ?
    )
```

</details>

## 상품 도메인 <a name = "product"></a>

### 도메인 모델링

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

다음은 도메인 주도 설계 개념을 적용한 **상품 도메인 모델**입니다.

![http://dl.dropbox.com/s/ps226nop93v6g5q/product_diagram.png](http://dl.dropbox.com/s/ps226nop93v6g5q/product_diagram.png)

상품 도메인은 카테고리(Category) 에그리거트와 물품(Item) 에그리거트로 구성됩니다. 물품 에그리거트는 루트 엔터티인 물품(Item), Item과 일대다 관계인 물품옵션(ItemOption), Item의 값 객체(VO)인 배송정보(ShippingFragment), ShippingFragment의 값 객체인 반송정보(ReturnFragment)로 구성됩니다.

### 데이터 모델링

![http://dl.dropbox.com/s/qbsy9xmccyl3eue/product_erd.png](http://dl.dropbox.com/s/qbsy9xmccyl3eue/product_erd.png)

</details>

### 재고 관리(동시성 이슈) <a name = "stock"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

**관련 정리**

- [재고 감소 로직에서 발생할 수 있는 동시성 이슈](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/concurrency-Issue-1.md)
- [MySQL을 이용한 동시성 이슈를 해결](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/concurrency-Issue-2.md)

재고 관리는 동시성 이슈를 고려하여 로직을 작성해야 합니다.

이 프로젝트에서는 동시성 이슈 문제의 해결을 위해 Redis의 Redisson 클라이언트를 이용한 분산 락과 MySQL의 Named Lock을 이용한 분산 락을 구현하였습니다. 두 코드 모두 템플릿-콜백 패턴을 이용하여 Lock을 획득한 후에, 구현 로직을 호출합니다.

- [MariaDbLock](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/lock/MariaDbLock.java)
- [RedissonLock](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/lock/RedissonLock.java)

```java
@Slf4j
@RequiredArgsConstructor
@Component
public class RedissonLock {
    private final RedissonClient redissonClient;

    public void executeWithLock(String prefix, String id, Runnable runnable) {
        final RLock lock = redissonClient.getLock(prefix + id);
        try {
            boolean isAvailable = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!isAvailable) {
                log.info(prefix + "-" + id + " : redisson getLock timeout");
                return;
            }

            runnable.run();
        } catch (InterruptedException e) {
            log.error("[RedissonLock-InterruptedException] cause = {}, errorMsg = {}", NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
            throw new BaseException(ErrorCode.COMMON_LOCK_FAIL);
        } finally {
            lock.unlock();
        }
    }
}
```
[ItemStockService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/product/application/ItemStockService.java) 은 `RedissonLock` 을 활용하여 Lock을 획득한 뒤에, 물품 재고를 차감합니다.

```java
@RequiredArgsConstructor
@Service
public class ItemStockService {
    private static final String ITEM_LOCK_PREFIX = "ITEM_STOCK_";
    
    private final RedissonLock redissonLock;
    private final ItemRepository itemRepository;
    
    @Transactional
    public void decreaseItemStockWithLock(Long id, Integer quantity){
        redissonLock.executeWithLock(
            ITEM_LOCK_PREFIX,
            id.toString(),
            () -> decreaseItemStock(id, quantity)
        );
    }
    
    private void decreaseItemStock(Long id, Integer quantity) {
        Item item = itemRepository.findById(id)
              .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ITEM_NOT_FOUND));
        item.decreaseStockQuantity(quantity);
        itemRepository.saveAndFlush(item);
    }
}
```

</details>

### 단일 책임 원칙과 URI 설계  <a name = "single-responsibility"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

[REST API 디자인 가이드](https://github.com/eastshine-high/til/blob/main/web/http/rest/api/resource-modeling.md) 를 따라 ‘상품 조회 API’를 설계하면, 일반적으로 다음과 같을 것입니다.

```
GET /api/v1/products/{id}
```

그런데 사이트 방문자(Guest)가 조회할 상품 정보와 판매자(Seller)가 조회할 상품 정보는 다릅니다. 따라서 이를 구분할 필요가 있었습니다. 이 경우에는 단일 책임 원칙(”하나의 모듈은 하나의, 오직 하나의 액터에 대해서만 책임져야 한다”)을 URI에 적용해 볼 수 있었습니다. 다음과 같이 액터를 URI에 추가하여 표현합니다.

방문자 상품 조회 URI : `/guest-api/v1/products/{id}`

판매자 상품 조회 URI : `/seller-api/v1/products/{id}`

> 이 프로젝트에서 `guest-api` 는 편의상 `guest` 를 생략하여 `api` 로 표현하였습니다.
>

실제 [쿠팡](https://developers.coupangcorp.com/hc/ko/articles/360033877853-%EC%83%81%ED%92%88-%EC%83%9D%EC%84%B1) 에서도 다음과 같이 액터를 구분하여 URI를 설계하는 것을 확인해 볼 수 있었습니다.

```
/v2/providers/seller_api/apis/api/v1/marketplace/seller-products
```
URI 분리와 함께, 클래스 또한 액터에 따라 분리합니다. 이렇게 분리한 클래스들은 더욱 단일 책임 원칙을 준수하는 것을 확인할 수 있었습니다.

또한 액터에 따른 분리는 [CQRS](https://github.com/eastshine-high/til/blob/main/domain-driven-design/cqrs.md) 의 기준이 될 수도 있었습니다. 상품에 대한 방문자의 주요 관심사는 조회(Query)이며 판매자의 주요 관심사는 데이터의 조작(Command)입니다. 따라서 액터의 분리가 자연스럽게 CQRS의 기준이 되었습니다.

이 프로젝트에서는 복잡한 로직이 필요하지 않은 방문자 API의 컨트롤러가 리포지토리에 직접 의존하므로써 간단한 형태의 CQRS를 적용하였습니다. 이를 통해 서비스 레이어의 구현을 생략함으로써 조회 로직을 간소화 시킬 수 있었습니다.

</details>

### Main-Sub 엔터티 vs 계층 구조 엔터티 <a name = "entity-design"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

아래의 쇼핑몰 카테고리를 설계할 때는 Main-Sub 엔터티 구조와 자기 자신을 참조하는 계층 구조의 엔터티로의 설계를 고려해 볼 수 있습니다.

![https://velog.velcdn.com/images/eastshine-high/post/bde225b5-4d69-4eb4-87c8-facf09c17ea6/image.png](https://velog.velcdn.com/images/eastshine-high/post/bde225b5-4d69-4eb4-87c8-facf09c17ea6/image.png)

무엇이 좋은 방법일지를 고민하면서 얻은 결론은 “**설계에 정답있는 것은 아니며 Trade off의 과정이다**”라는 점을 배울 수 있었습니다. 따라서 설계에 따른 Trade off를 생각해 봅니다.

**Main-Sub Entity 구조**

- (장점) 데이터를 관리(CRUD)하기 쉽습니다.
- (단점) 엔티티의 계층적 확장 측면에서 유연하지 못합니다.

**계층(재귀) 구조**

- (장점) 엔티티의 계층적 확장 측면에서 유연합니다.
- (단점) 데이터를 관리(CRUD)하기 어렵습니다.

결론적으로 추가적인 Sub Entity의 확장을 고려하여 `Category` 엔티티의 설계는 재귀 구조로 결정하였습니다.

![https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png](https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png)

</details>

### 상품 검색 <a name = "searching-product"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

**상품 검색 API의 문제점**

상품 검색은 RDB의 SQL문 `LIKE '%Keyword%'` 을 사용하여 검색합니다. 이 SQL 문은 Index Range Scan을 할 수 없고, **Index Full Scan을 수행하기 때문에 조회 성능이 좋지 못합니다**. 만약 상품 검색 요청이 자주 발생할 경우, 서비스 성능이 저하될 수 있는 부분입니다.

**개선 방안**

이러한 문제를 개선하기 위한 방법으로 Elasticsearch를 검색 엔진으로 활용해보는 것을 검토해 볼 수 있습니다. Elasticsearch는 특정 문장을 입력받으면, 파싱을 통해 문장을 단어 단위로 분리하여 저장합니다. 검색을 할 때는 분리된 단어를 기반으로 역으로 인덱스(Reverted Index)를 찾아가는 방식으로 검색을 수행합니다. 따라서 RDB의 Keyword 검색에 수행하는 Index Full Scan 만큼의 시간을 아낄 수 있습니다.

또한 간단한 방법으로 MySQL의 경우, 전문 검색 Index를 사용할 수 있습니다. 전문 검색 Index 또한 Elasticsearch처럼 분리된 단어를 기반으로 인덱스를 찾아갑니다. 다만 이 방법을 통한 서비스 사례는 찾을 수 없었습니다.

</details>


## 사용자 도메인 <a name = "user"></a>

### 모델링

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

### Class diagram

![http://dl.dropbox.com/s/tuldntbumwo0608/user_diagram.png](http://dl.dropbox.com/s/tuldntbumwo0608/user_diagram.png)

- 권한 (`Role`)클래스는 JPA의 [복합 식별자](https://github.com/eastshine-high/til/blob/main/java/specification/jpa-hibernate/domain-model/composite-identifiers.md) (`RoleId`)를 이용하여 구성하였습니다. 권한 설계는 JPA의 ManyToMany 관계로도 구성해볼 수 있는데, 이 때는 권한의 종류(`RoleType`)를 ENUM이 아닌 Table로 관리하게 됩니다. 따라서 권한에 대한 변경이 자주 발생할 경우에는 ManyToMany 로 설계하는 것이 더 나은 방식으로 볼 수 있습니다.
- `Seller` 는 `User` 를 상속합니다. RDB에서는 `JOIN` 관계로 표현합니다.

### ERD

![http://dl.dropbox.com/s/xid2l7fou0j88p8/user](http://dl.dropbox.com/s/xid2l7fou0j88p8/user)

</details>

### 인증(JWT) <a name = "jwt"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

사용자가 접근이 제한되어 있는 API의 리소스에 접근할 때는, 먼저 로그인을 통해 자신이 누구인지를 인증하고 리소스 접근에 대한 인가를 받아 리소스에 접근할 수 있습니다.

하지만 웹의 기반인 HTTP 프로토콜은 [무상태성(Stateless)](https://github.com/eastshine-high/til/blob/main/web/http/rest/stateless.md) 과 비연결성(Connectionless)이란 특성을 가지고 있습니다. 따라서 모든 요청마다 인증이 필요합니다. 하지만 모든 요청마다 아이디와 비밀번호를 통해 인증할 수는 없으므로 로그인을 통해 인증한 다음에는 쿠키, 세션, JWT 등의 수단을 이용하여 이를 대신합니다.

이 프로젝트에서는 인증을 대신하는 수단으로 JWT를 사용하였습니다. JWT에 대한 설명과 이를 자바 코드로 표현하는 과정은 [Github](https://github.com/eastshine-high/til/blob/main/web/jwt.md) 을 통해 정리하였습니다.

먼저, JWT를 발급하고 파싱할 수 있는 빈(Bean)을 만듭니다.

```java
@Component
public class JwtUtil {
    public static final String KEY_OF_USER_ID = "userId";

    private final Key signKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        signKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String encode(Long userId) {
        return Jwts.builder()
                .claim(KEY_OF_USER_ID, userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (60 * 1000) * 100)) // 토큰 유효 일시(+100분)
                .signWith(signKey)
                .compact();
    }

    public Claims decode(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (SignatureException | MalformedJwtException e) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(ErrorCode.EXPIRED_TOKEN);
        }
    }
}
```

- `claim(KEY_OF_USER_ID, userId)` : [JWT 문서](https://jwt.io/introduction)에서는 토큰의 요소들을 모든 사람이 읽을 수 있음에 유의해야 한다고 설명합니다. 실제로 [JWT debugger](https://jwt.io/#debugger-io) 등을 이용하여 토큰을 확인할 수 있으므로 사용자의 식별자 이외의 정보를 페이로드에 담지 않았습니다(사용자의 식별자 또한 토큰화한다면 더 좋습니다).
- `setExpiration(new Date(System.currentTimeMillis() + (60 * 1000) * 100))` : 토큰은 보안 이슈(위.변조 등)가 발생하지 않도록 필요 이상으로 유지하지 않습니다([JWT의 공식 문서 권장](https://jwt.io/introduction)). 따라서 토큰의 유효 시간은 100분으로 설정하였습니다.

이제 로그인을 통해 인증한 사용자에게 `JwtUtil` 을 이용하여 JWT를 발행할 수 있습니다.

```java
package com.eastshine.auction.user.application;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserInfoRedisRepository userInfoRedisRepository;

    @Transactional(readOnly = true)
    public String login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.USER_LOGIN_FAIL));

        if (!user.authenticate(password, passwordEncoder)) {
            throw new AuthenticationException(ErrorCode.USER_LOGIN_FAIL);
        }

        userInfoRedisRepository.save(user.toUserInfo());
        return jwtUtil.encode(user.getId());
    }
}
```

- `jwtUtil.encode(user.getId())` : 사용자가 인증에 성공한 경우, 주입 받은 `JwtUtil` 의존성을 이용하여 토큰을 발급합니다.
- `userInfoRedisRepository.save(user.toUserInfo())` : 토큰을 발급 사용자가 이를 이용해 다른 리소스에 접근할 때는, 식별 정보 외에 권한 정보 등이 필요합니다. 하지만 토큰에는 사용자 식별 정보만 담겨있으므로 데이터베이스에 접근하여 사용자 정보를 조회해야 합니다. 이러한 조회는 매 요청마다 발생하므로 매우 자주 발생합니다. 따라서 관계형 데이터베이스보다는 인메모리 데이터베이스를 활용하는 것이 서비스 성능에 유리합니다.

</details>

### 외래키와 복합키 사용에 대하여 <a name = "constraints"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

개인적으로 참여했던 실무 프로젝트에서는 개발 편의성과 유연성을 이유로 외래키와 복합키를 잘 사용하지 않았습니다. 이번 토이 프로젝트를 기회로 이를 직접 경험해 보고 관련 글들을 읽어보면서, 이에 대한 생각을 정리해 볼 수 있었습니다.

외래키 사용에 대하여

- **무결성과 정합성** : 외래키 사용의 가장 큰 장점이라고 생각합니다. 외래키가 설정되어있는 테이블 또는 데이터를 변경할 때, 참조 무결성에 위배되는 데이터가 있을 경우, 즉시 오류가 발생하므로 해당 작업을 수행할 수 없습니다. 따라서 변경 작업 전에 해당 문제가 해결이 되어야 데이터 또는 테이블을 변경할 수 있습니다. 이는 인지하지 못했던 데이터 오류를 사전에 확인하고 조치할 수 있도록 합니다.
- **관리포인트 증가** : 외래키를 설정하면서 `RESTRICT` , `ON UPDATE SET NULL` , `ON DELETE CASCADE`와 같은 옵션을 넣든 넣지 않든, 어느 쪽이든 신경 써야 할 부분이 늘어납니다. 데이터의 양이 더 많아지고 관계가 복잡해질수록 신경 써야 할 부분은 더욱 많아질 수 있습니다. 특히 `ON DELETE CASCADE` 와 같은 옵션은 매우 주의해서 사용해야 합니다.
- **개발 편의성과 변경의 유연성에 대하여** : 위의 두 가지 등의 이유로 외래키의 사용은 개발 편의성과 변경의 유연성이 떨어집니다. 즉, 개발 편의성과 변경의 유연성은 무결성 그리고 정합성과 트레이드 오프 관계로 볼 수 있습니다. 특히 변경이 자주 발생하는 개발 초기 단계에서는 무결성 문제로 인해 변경 작업에 어려움을 겪을 수 있기 때문에, 개발이 안정화 되는 단계에서 외래키를 적용하는 것도 하나의 방법이 될 수 있습니다.
- **인덱스** : 데이터베이스는 외래키를 설정하는 테이블의 칼럼에 자동으로 인덱스를 생성합니다. 따라서 외래키를 사용하지 않지만 해당 칼럼으로 테이블 조인이 자주 발생한다면, 인덱스 생성이 강력히 권장됩니다.
- **성능** : 외래키 제약조건이 있는 테이블의 경우, 부모-자식 관계로 정의된 컬럼에 대해서 두 테이블 데이터가 일치해야 하기 때문에, 외래키로 정의된 동일 데이터(레코드)에 대해 DML 작업이 발생하게 되면, Lock으로 인해 대기해야 하는 상황이 발생합니다.  따라서 대량의 트랜잭션이 발생하는 경우라면 외래키 사용을 지양해야 할 필요가 있습니다. 성능의 차이에 대해서는 다음 [블로그](https://martin-son.github.io/Martin-IT-Blog/mysql/foreign) 를 참고해 볼 수 있습니다.

복합키 사용에 대하여

- **주의 사항** : 복합키를 정의할 때는, 복합키를 구성하는 칼럼의 순서에 주의할 필요가 있었습니다. DBMS는 자동으로 복합키를 구성하는 칼럼의 순서대로 인덱스를 생성합니다. 이 때, 복합 인덱스의 선두 칼럼의 카디널리티에 따라서 인덱스의 성능 차이가 발생할 수 있습니다. 따라서 카디널리티가 높은 칼럼의 순서대로 복합키의 순서를 구성하는 것이 좋습니다.
- **인덱스** : 만약 복합키를 사용하지 않고 인조 식별자를 기본키를 사용한다면, 복합키로 선언하지 않은 칼럼들은 인덱스로 구성하는 것을 고려할 필요가 있습니다.

</details>

## 코드 개선하기

### 테스트 코드 작성을 통한 올바른 책임의 이해(캡슐화) <a name = "test-responsibility"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />



테스트 코드를 작성하다보면, 객체에 책임을 잘못 할당한 것을 깨닫게 되는 경우가 있습니다. 잘못 할당한 객체의 책임은 테스트 코드 작성에도 영향을 주기 때문입니다. 이를 고치는 과정에서 객체의 책임을 더 잘 이해할 수 있었습니다.

“물품 정보는 물품 정보를 생성한 사용자만 수정할 수 있다”는 권한 검사를 예로 들어보겠습니다. 이 책임을 수행하기 좋은 객체는 `컨트롤러`, `서비스`, `도메인 객체` 중에 `도메인 객체`입니다.

처음에, 저는 이 권한 검사를 `컨트롤러`에서 처리하도록 하였습니다. 이렇게 했던 이유로는, 컨트롤러에서 접근 검증을 마친다면 서비스와 도메인 엔터티에서는 이를 신경쓸 필요가 없기 때문입니다. 또한 서비스 메소드에 접근하려는 사용자의 정보를 전달할 필요가 없으므로, 서비스 메소드의 아규먼트 갯수를 줄일 수 있습니다(클린코드에서는 메소드의 아규먼트가 적을 수록 좋다고 합니다).

위의 이유로, 아래와 같이 접근 검증을 컨트롤러에서 수행하였습니다.

```java
@PatchMapping("/items/{itemId}")
@PreAuthorize("hasAuthority('SELLER')")
@ResponseStatus(HttpStatus.OK)
public void patchItem(
        @PathVariable Long itemId,
        @RequestBody JsonMergePatch patchDocument,
        Authentication authentication
) {
    Item item = itemService.findItem(itemId);
    validateAccessableItem(item, authentication);

    itemService.updatePatchedItem(patchDocument, item);
}

private void validateAccessableUser(Item item, Authentication authentication) {
		UserInfo userInfo = (UserInfo) authentication.getPrincipal();
    if(item.getCreatedBy() != userInfo.getId()){
        throw new UnauthorizedException(ErrorCode.ITEM_UNACCESSABLE);
    }
}
```

하지만 이에 대한 테스트 코드를 작성하는 과정에서 어려움을 겪게 되었습니다. 그 이유는 컨트롤러의 메소드 아규먼트에서는 ‘물품 정보를 생성한 사용자의 정보’가 없고, 인증 정보(`Authentication`)가 있기 때문입니다. 이는 컨트롤러에 너무 많은 책임을 할당했다고 볼 수 있습니다.

**캡슐화**

“물품 정보는 물품 정보를 생성한 사용자만 수정할 수 있다”는 책임을 수행하기 가장 적합한 객체는 ‘물품 정보를 생성한 사용자’를 알고 있는 객체입니다. 따라서 ‘도메인 객체’에서 접근을 검증하는 책임을 수행합니다.

```java
@Entity
public class Item {
    ...

    private Long createdBy;

    public void validateAccessibleUser(Long userId) {
        if(createdBy != userId){
            throw new UnauthorizedException(ErrorCode.ITEM_INACCESSIBLE);
        }
    }
}
```

이를 통해 캡슐화를 지키며 접근 검증을 수행할 수 있습니다. 사실, 초기에 작성한 컨트롤러 코드에서 `item.getCreatedBy` 는 `Item` 객체의 `createdBy` 필드를 외부에 노출시킵니다. 따라서 캡슐화를 위배하기도 합니다.

또한 테스트를 작성하기가 매우 쉬워졌습니다.

```java
@Nested
@DisplayName("validateAccessibleUser 메소드는")
class Describe_validateAccessibleUser{

    @Test
    @DisplayName("물품을 생성한 사용자가 아닐 경우, InvalidArgumentException 예외를 던진다.")
    void contextWithInaccessibleUser() {
        Item item = new Item();
        Long creatorId = 21L;
        Long accessorId = 2000L;
        ReflectionTestUtils.setField(item, "createdBy", creatorId);

        assertThatThrownBy(
                () -> item.validateAccessibleUser(accessorId)
        )
                .isExactlyInstanceOf(UnauthorizedException.class)
                .hasMessage(ErrorCode.ITEM_INACCESSIBLE.getErrorMsg());
    }
    
    @Test
    @DisplayName("물품을 생성한 사용자일 경우, 예외를 던지지 않는다.")
    void contextWithAccessibleUser() {
        Item item = new Item();
        Long creatorId = 21L;
        Long accessorId = 21L;
        ReflectionTestUtils.setField(item, "createdBy", creatorId);

        item.validateAccessibleUser(accessorId);
    }
}
```
- `validateAccessibleUser` 메소드의 아규먼트인 `userId` 의 값에 변화를 주면, 쉽게 테스트가 가능합니다.

</details>

### 관심사의 분리 <a name = "separation-of-concern"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

다음은 카테고리(`Category`)를 등록하는 서비스 코드입니다. 단순히 요청 객체(DTO)의 값을 확인하고 도메인 객체로 매핑한 뒤에, 이를 리포지토리에 저장하는 로직입니다.

```java
@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category registerCategory(CategoryRegistrationRequest request) {
        Category parentCategory = null;
        if(Objects.nonNull(request.getParentId())) {
            parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(CategoryEntityNotFoundException::new);
        }
    
        Category category = Category.builder()
                .id(request.getId())
                .parent(parentCategory)
                .ordering(request.getOrdering())
                .name(request.getName())
                .build();
        
        return categoryRepository.save(category);
    }
}
```

간단한 로직이지만 코드의 길이가 길어지면서 코드의 가독성이 떨어집니다. 이렇게 코드가 복잡해진 이유는 DTO 객체의 필드를 도메인 객체로 매핑하는 책임을 서비스가 가지고 있기 때문입니다.

매핑하는 책임은 매핑할 정보를 알고 있는 DTO 객체에서 수행하는 것이 조금 더 적합해 보입니다. 따라서 매핑하는 책임을 DTO 객체에 위임하여 관심사를 분리합니다.

```java

public class CategoryRegistrationRequest {

    @NotNull
    private Integer id;
    private Integer parentId;

    @NotBlank
    private String name;

    @NotNull
    private Integer ordering;

    public Category toEntity(Category parentCategory) {
        return Category.builder()
                .id(id)
                .parent(parentCategory)
                .name(name)
                .ordering(ordering)
                .build();
    }
}
```

매핑에 대한 책임을 DTO가 가져가면서 서비스 코드의 가독성이 개선된 것을 확인할 수 있습니다. 또한 DTO는 getter와 같은 메소드를 필요 이상으로 만들지 않을 수 있습니다.

```java
@Transactional
public Category registerCategory(CategoryRegistrationRequest request) {
    Category parentCategory = null;
    if(Objects.nonNull(request.getParentId())) {
        parentCategory = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_PARENT_ENTITY_NOT_FOUND));
    }

    return categoryRepository.save(request.toEntity(parentCategory));
}
```

</details>

## 기술 사용 배경 <a name = "why-use"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

### **Flyway**

<img src="http://dl.dropbox.com/s/6xz92p0s6nigdvw/flyway.png" width="30%">

도메인을 개발하면서 변경이 발생하면, 데이터베이스의 스키마 또한 변경 사항에 맞게 반영해 주어야 합니다. 다만 이 과정에서 서비스 운영에서 중요한 부분 중의 하나인 데이터베이스를 수동으로 변경하며 관리하는 것에 불안전함을 느꼈습니다. 따라서 이에 대한 관리 방법을 찾아 보았고, Flyway라는 도구에 대해 알게되었습니다. 그리고 이를 적용하여 **데이터베이스의 변경 사항에 대한 이력을 관리**함으로써 데이터베이스를 좀 더 안정적으로 관리할 수 있었습니다.

이력 관리 디렉토리: [resources/db/migration/**](https://github.com/eastshine-high/auction-backend/tree/main/app/src/main/resources/db/migration)

### **Redis**

1. 캐시(Look-aside)

카테고리 엔터티는 다음과 같이 재귀 구조로 설계되어 있습니다.

![https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png](https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png)

이는 JPA를 통해 조회를 할 경우 N + 1 문제가 발생할 수 밖에 없는 구조입니다. 따라서 쇼핑몰 메인페이지에서 조회하는 카테고리와 같이, 요청이 자주 들어오는 API에는 캐싱 처리를 하여 조회 성능을 개선하였습니다.

```java
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @GetMapping("/api/v1/display/categories")
    @Cacheable(value = "displayCategories", cacheManager = "cacheManager")
    public List<CategoryDto.DisplayMain> getDisplayCategories() {
        List<Category> categories = categoryRepository.findDisplayCategories();
        return categories.stream()
                .map(CategoryDto.DisplayMain::new)
                .collect(Collectors.toList());
    }
}
```

2. 분산락

멀티 쓰레드 구조의 관계형 DB와 달리 Redis는 싱글 쓰레드이면서 In-memory 저장소라는 특징을 가지고 있습니다. 따라서 [동시성 이슈](#stock) 를 처리하기 위한 분산락을 구현하기 좋은 저장소로 볼 수 있습니다.

3. In-memory 저장소

로그인 인증에 성공한 경우, 사용자에게 세션 용도의 JWT를 발급합니다(이는 [좋은 방식이 아닐](https://velog.io/@park2348190/API-서버의-인증-수단으로-JWT를-사용하는-것이-옳은가) 수 있습니다). 이 때, JWT의 페이로드는 모든 사람이 읽을 수 있음에 유의해야 하기 때문에([공식 문서 권장](https://jwt.io/introduction)) JWT의 페이로드에는 사용자의 식별자만 보관하였습니다. 따라서 접근이 제한되어있는 API에 대한 모든 HTTP 요청마다, 사용자 권한을 조회하기 위한 데이터베이스 접근이 발생합니다. 이 때, 인증에 성공한 사용자 정보를 Redis(In-memory 저장소)에 보관하여 API의 성능을 개선합니다.

4. 현재 Redis 사용의 개선점

Redis를 캐시 이외의 용도로 사용한다면, 적절한 데이터 백업이 필요합니다. 그 이유는 하나의 Redis만 사용할 때, Redis가 죽어버리면 Redis를 사용하는 로직들에 문제가 생기기 때문입니다. 따라서, 현재 하나의 Redis만 운용중인 서버에 추가적인 백업 Redis 운용이 필요합니다.

### **Kafka**

주문 도메인은 업무 특성상 다른 도메인과의 협력이 많이 필요합니다. 이 때, 이벤트를 활용하면 도메인 간의 결합도를 낮추며 협력할 수 있습니다. 따라서 [주문 및 주문 취소 업무](#order-process) 에서는 Kafka를 이용한 비동기 이벤트 처리를 통해 도메인 간의 결합도를 낮추었습니다. 사실 MSA가 아닌 모놀리틱 아키텍처에서 비동기 이벤트 처리는 Spring이 지원하는 이벤트 기능만으로 충분합니다. Kafka는 도메인 주도 개발을 공부하면서 관심이 커져, 기술 사용 연습 차원에서 도입하였습니다.

### **JSON Merge Patch**

[JSON Merge Patch를 이용한 PATCH API 구현하기](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/json-merge-patch.md)

리소스의 값을 변경하는 REST API를 구현할 때, 도메인 레이어에서는 다음과 같이 리소스의 값을 변경하는 메소드를 작성할 수 있습니다.

```java
@Entity
public class Product
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private boolean onSale;

    public void changeWith(Product source) {
        name = source.name;
        price = source.price;
        stockQuantity = source.stockQuantity;
        onSale = source.onSale;
    }
}
```

이 때 메소드 아규먼트 `source` 에 속성 값을 전달하지 않을 경우, 해당 객체의 속성의 값은 `null` 로 변경됩니다. 따라서 위와 같이 구현된 API를 이용해 리소스를 변경할 때는 리소스를 모두 표현(Representation)하여 변경을 요청해야 합니다. 이와 같은 방식을 HTTP에서는 `PUT` 메소드라고 합니다.

하지만 HTTP의 `PUT` 메소드를 사용하면 리소스의 단일 필드를 수정해야 하는 경우에도 리소스의 전체 표현을 보내야 하므로 다소 불편합니다. 따라서 `PUT` 이 아닌 `PATCH` HTTP 메소드를 지원하는 API를 구현해 보기로 했습니다.

먼저, 가장 단순한 방법으로 각 속성을 변경하기 전에 `if` 문을 추가하면 `PATCH` HTTP 메소드를 지원하는 API의 구현이 가능합니다.

```java
@Entity
public class Product
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private boolean onSale;

    public void changeWith(Product source) {
        if(source.name != null){
            name = source.name;
        }
        if(source.price != null){
            price = source.price;
        }
        ...
    }
}
```

하지만 개발자다운 접근은 아닌 것 같습니다.

또 다른 방법을 찾아보면서 JsonPatch([RFC6902](https://datatracker.ietf.org/doc/html/rfc6902)) 와 JsonMergePatch([RFC7396](https://datatracker.ietf.org/doc/html/rfc7386)) 에 대해서 알게 되었습니다. 이에 대해 정리해 보면서 [JsonMergePatch 를 이용해 PATCH API를 구현](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/json-merge-patch.md) 해 볼 수 있었습니다.

</details>
