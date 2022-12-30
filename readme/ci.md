# Github Actions, Docker를 활용한 CI 구축

이번 섹션에서는 **Github Action과 Docker를 이용한 CI 구축**에 대해 설명하겠습니다.
Auction Backend의 CI/CD 구성도는 다음과 같습니다. 최대한 여러 도구들을 활용해 보는 것을 목표로 두다 보니, 아래와 같은 구성하게 되었습니다.

![http://dl.dropbox.com/s/m5u0e1r8w6uahlc/ci-cd.png](http://dl.dropbox.com/s/m5u0e1r8w6uahlc/ci-cd.png)

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
