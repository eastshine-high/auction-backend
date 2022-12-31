# 프로젝트를 통해 무엇을 할 수 있게 되었는가

- **데이터 모델링** 
  - [데이터에 대한 접근 과정](https://github.com/eastshine-high/til/blob/main/relational-database/data-access/database-storage-structure.md) 을 고려하여 테이블을 설계합니다. 
  - 설계에 정답이 있는 것은 아니며, Trade off를 고려해야 함을 이해합니다.
    - 사례. [Main-Sub 구조 엔터티 VS 계층(재귀) 구조 엔터티](#entity-design)

- **도메인 주도 설계**
  - 도메인 주도 설계를 통해 객체를 의미있게 표현하고 모델링합니다.
    - 사례. [상품 도메인 모델링](https://github.com/eastshine-high/auction-backend/blob/main/readme/product.md)
    - 사례. [주문 도메인 모델링](https://github.com/eastshine-high/auction-backend/blob/main/readme/order.md)
    - 사례. [도메인 언어 탐구](https://github.com/eastshine-high/auction-backend/wiki/%EB%8F%84%EB%A9%94%EC%9D%B8-%EC%96%B8%EC%96%B4-%ED%83%90%EA%B5%AC)
    - 사례. [주문 프로세스(도메인 이벤트)](https://github.com/eastshine-high/auction-backend/blob/main/readme/order-process.md)

- **ORM 활용** 
  - JPA, Spring Data JPA, Querydsl를 이용해 여러 기능들을 구현해 보면서, ORM 사용을 숙련합니다.
    - 사례. [Hibernate - MultipleBagFetchException 해결하기](#multiple-bag-fetch-exception)

- **Spring Security**
  - [Spring Security의 구조](https://github.com/eastshine-high/til/blob/main/spring/spring-security/architecture.md) 와 [Spring Security의 인증 구조](https://github.com/eastshine-high/til/blob/main/spring/spring-security/authentication/servlet-authentication-architecture.md) 를 이해하고 API에 대한 보안 처리를 합니다.
    - 사례. [API 보안](https://github.com/eastshine-high/auction-backend/blob/main/readme/security.md)

- **기술 사용**
  - 프로젝트 과정에서 문제들을 경험하면서, 기술 사용의 이유를 체감하고 도입합니다.
    - 사례. [기술 사용 배경](https://github.com/eastshine-high/auction-backend/blob/main/readme/why-use.md)

- **테스트** 
  - 단위, 통합 테스트 작성을 통해 견고한 소프트웨어를 만들고 기능들을 문서화합니다.
    - 사례. [테스트](https://github.com/eastshine-high/auction-backend/blob/main/readme/test.md)
- **CI/CD**
  - 지속적 통합 및 배포를 구축합니다.
    - 사례. [Github Actions, Docker를 활용한 CI 구축](https://github.com/eastshine-high/auction-backend/blob/main/readme/ci.md)
    - 사례. [Jenkins, Docker, Nginx를 활용한 무중단 CD 구축](https://github.com/eastshine-high/auction-backend/blob/main/readme/cd.md)

- **문서화**
  - Spring REST Docs를 이용해 API를 문서화합니다.
    - 사례. [API 문서(AWS 배포)](http://52.79.43.121/docs/index.html)
  - 유스 케이스를 작성하고 테스트 코드를 작성합니다.
    - 사례. [API 유스 케이스](https://eastshine.notion.site/5802417b375e474380a1a092e07e79fe?v=65b6e4f02626434597726a247cb3bf2e)

- **HTTP 프로토콜**
  - [HTTP 프로토콜](https://github.com/eastshine-high/til/tree/main/web) 에 대한 이해는 REST API 개발, CI/CD 파이프라인 구축 등의 작업을 스스로 학습하고 진행하는 데 많은 도움이 되었습니다.
