# Main-Sub 엔터티 vs 계층 구조 엔터티

아래의 쇼핑몰의 카테고리를 설계할 때는 Main-Sub 구조 엔터티와 계층(재귀) 구조 엔터티를 고려할 수 있습니다.

![https://velog.velcdn.com/images/eastshine-high/post/bde225b5-4d69-4eb4-87c8-facf09c17ea6/image.png](https://velog.velcdn.com/images/eastshine-high/post/bde225b5-4d69-4eb4-87c8-facf09c17ea6/image.png)

'무엇이 좋은 방법일까'를 고민하고 알아보면서 “**설계에 정답있는 것은 아니며 Trade off를 고려하는 과정이다**”는 결론을 낼 수 있었습니다. 따라서 설계에 따른 Trade off를 생각합니다.

### Main-Sub Entity 구조

- 장점
    - 직관적입니다.
    - 데이터를 다루기(CRUD)가 쉽습니다.
- 단점
    - 엔티티의 계층적 확장 측면에서 유연하지 못합니다. 엔터티를 추가해야할 수도 있습니다.

### 계층(재귀) 구조

- 장점
    - 엔티티의 계층적 확장 측면에서 유연합니다.
- 단점
    - 직관적이지 못합니다.
    - 데이터를 다루기(CRUD)가 어렵습니다.

결론적으로 추가적인 Sub Entity의 확장을 고려하여 `Category` 엔티티의 설계는 재귀 구조로 결정하였습니다.

![https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png](https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png)
