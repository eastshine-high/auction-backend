# Auction backend

## 사용 기술

- Java 11, Gradle
- JPA, Querydsl, Junit5, MapStruct, JWT
- Spring Boot, Spring Data JPA, Spring REST Docs, Spring Security
- MariaDB 10, Redis, Kafka
- Devops : AWS EC2, Github Action, Jenkins, Nginx, Docker
- Design : 도메인 주도 설계, REST API

## 프로젝트 개요

쇼핑몰의 REST API를 개발한 개인 프로젝트입니다. 서비스 가능한 서버를 목표로 설계, 구현, 테스트, 배포 과정을 고민하고 부딪히면서 개발 능력을 향상시킵니다.

## 목차
- [프로젝트를 통해 무엇을 할 수 있게 되었는가](https://github.com/eastshine-high/auction-backend/blob/main/readme/i-am-able-to.md)
- [프로젝트 문서](https://github.com/eastshine-high/auction-backend/blob/main/readme/document.md)
- [테스트](https://github.com/eastshine-high/auction-backend/blob/main/readme/test.md)
- [프로젝트 ERD](#entity-relationship-diagram)
- 지속적 통합 및 배포
    - [Github Actions, Docker를 활용한 CI 구축](https://github.com/eastshine-high/auction-backend/blob/main/readme/ci.md)
    - [Jenkins, Docker, Nginx를 활용한 무중단 CD 구축](https://github.com/eastshine-high/auction-backend/blob/main/readme/cd.md)
- 기능 설명 및 설계, 구현 과정에서 배운 것들
    - 공통 기능
        - [REST API의 예외(Exception) 처리](https://github.com/eastshine-high/auction-backend/blob/main/readme/exception.md)
        - [API의 보안(Security)](https://github.com/eastshine-high/auction-backend/blob/main/readme/security.md)
        - [Auditing](https://github.com/eastshine-high/auction-backend/blob/main/readme/auditing.md)
    - 주문
        - [도메인 모델링](https://github.com/eastshine-high/auction-backend/blob/main/readme/order.md)
        - [주문 프로세스(도메인 이벤트)](https://github.com/eastshine-high/auction-backend/blob/main/readme/order-process.md)
        - [Hibernate - MultipleBagFetchException 해결하기](https://github.com/eastshine-high/auction-backend/blob/main/readme/multiple-bag-fetch-exception.md)
    - 상품
        - [도메인 모델링](https://github.com/eastshine-high/auction-backend/blob/main/readme/product.md)
        - [재고 관리(동시성 이슈)](https://github.com/eastshine-high/auction-backend/blob/main/readme/stock.md)
        - [단일 책임 원칙과 URI 설계](https://github.com/eastshine-high/auction-backend/blob/main/readme/single-responsibility.md)
        - [Main-Sub 구조 엔터티 VS 계층(재귀) 구조 엔터티](https://github.com/eastshine-high/auction-backend/blob/main/readme/entity-design.md)
        - [상품 검색](#searching-product)
    - 사용자
        - [모델링](https://github.com/eastshine-high/auction-backend/blob/main/readme/user.md)
        - [로그인(JWT 발행하기)](https://github.com/eastshine-high/auction-backend/blob/main/readme/jwt.md)
        - [외래키와 복합키 사용에 대하여](https://github.com/eastshine-high/auction-backend/blob/main/readme/constraints.md)

- 코드 개선하기
    - [테스트 코드 작성을 통한 올바른 책임의 이해(캡슐화)](https://github.com/eastshine-high/auction-backend/blob/main/readme/test-responsibility.md)
    - [관심사의 분리](https://github.com/eastshine-high/auction-backend/blob/main/readme/separation-of-concern.md)
- [기술 사용 배경](https://github.com/eastshine-high/auction-backend/blob/main/readme/why-use.md)
    - Flyway
    - Redis
    - JSON Merge Patch
    - Kafka

## 프로젝트를 통해 무엇을 할 수 있게 되었는가 <a name = "i-am-able-to"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/i-am-able-to.md)

## 프로젝트 문서 <a name = "document"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/document.md)

## 테스트 <a name = "test"></a>

[본문 확인(👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/test.md)

## 프로젝트 ERD <a name = "entity-relationship-diagram"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

![](http://dl.dropbox.com/s/sglg7cini7s8g7s/auction-erd.png)

</details>

## 지속적 통합 및 배포

### Github Actions, Docker를 활용한 CI 구축

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/ci.md)

### Jenkins, Docker, Nginx를 이용한 무중단 CD 구축

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/cd.md)

## 기능 설명 및 설계, 구현 과정에서 배운 것들

## 공통 기능

### REST API의 예외(Exception) 처리 <a name = "exception"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/exception.md)

### API의 보안(Security) <a name = "security"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/security.md)

### Auditing <a name = "auditing"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/auditing.md)

## 주문 도메인 <a name = "order"></a>

### 도메인 모델링

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/)

### 주문 프로세스(비동기 이벤트)<a name = "order-process"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/order-process.md)

### Hibernate - MultipleBagFetchException 해결하기 <a name = "multiple-bag-fetch-exception"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/multiple-bag-fetch-exception.md)

## 상품 도메인 <a name = "product"></a>

### 도메인 모델링

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/product.md)

### 재고 관리(동시성 이슈) <a name = "stock"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/stock.md)

### 단일 책임 원칙과 URI 설계  <a name = "single-responsibility"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/single-responsibility.md)

### Main-Sub 엔터티 vs 계층 구조 엔터티 <a name = "entity-design"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/entity-design.md)

### 상품 검색 <a name = "searching-product"></a>

<details>
   <summary> 본문 확인 (👈Click)</summary>
<br />

**상품 검색 API의 문제점**

상품 검색은 RDB의 SQL문 `LIKE '%Keyword%'` 을 사용하여 검색합니다. 
이 SQL 문은 Index Range Scan이 아닌, **Index Full Scan을 수행하기 때문에 조회 성능이 좋지 못합니다**. 
만약 상품 검색 요청이 자주 들어온다면, 서비스 성능이 저하될 수 있습니다.

**개선 방안**

이러한 문제를 개선하기 위한 방법으로 Elasticsearch를 검색 엔진으로 활용해보는 것을 검토해 볼 수 있습니다. 
Elasticsearch는 특정 문장을 입력받으면, 파싱을 통해 문장을 단어 단위로 분리하여 저장합니다. 
검색을 할 때는 분리된 단어를 기반으로 역으로 인덱스(Reverted Index)를 찾아가는 방식으로 검색을 수행합니다. 
따라서 RDB의 Keyword 검색을 할 때 수행하는 Index Full Scan 만큼의 시간을 아낄 수 있습니다.

또한 간단한 방법으로 MySQL의 경우, 전문 검색 Index를 사용할 수 있습니다. 
전문 검색 Index 또한 Elasticsearch처럼 분리된 단어를 기반으로 인덱스를 찾아갑니다. 
이 방법을 통한 서비스 사례는 찾아볼 수 없었습니다.

</details>


## 사용자 도메인 <a name = "user"></a>

### 모델링

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/user.md)

### 로그인(JWT 발행하기) <a name = "jwt"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/jwt.md)

### 외래키와 복합키 사용에 대하여 <a name = "constraints"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/constraints.md)

## 코드 개선하기

### 테스트 코드 작성을 통한 올바른 책임의 이해(캡슐화) <a name = "test-responsibility"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/test-responsibility.md)

### 관심사의 분리 <a name = "separation-of-concern"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/separation-of-concern.md)

## 기술 사용 배경 <a name = "why-use"></a>

[본문 확인 (👈Click)](https://github.com/eastshine-high/auction-backend/blob/main/readme/why-use.md)
