# 사용자 도메인

### Class diagram

![http://dl.dropbox.com/s/tuldntbumwo0608/user_diagram.png](http://dl.dropbox.com/s/tuldntbumwo0608/user_diagram.png)

- 사용자 권한 (`UserRole`)클래스는 JPA의 [복합 식별자](https://github.com/eastshine-high/til/blob/main/java/specification/jpa-hibernate/domain-model/composite-identifiers.md) (`UserRoleId`)를 이용하여 구성하였습니다.
- 판매자(`Seller`)는 사용자(`User`)를 상속합니다. RDB에서는 `JOIN` 관계로 표현됩니다.

### ERD

![http://dl.dropbox.com/s/ex33g8bzkd37fhi/user-erd.png](http://dl.dropbox.com/s/ex33g8bzkd37fhi/user-erd.png)
