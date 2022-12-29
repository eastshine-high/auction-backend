# 사용자 도메인

### Class diagram

![http://dl.dropbox.com/s/tuldntbumwo0608/user_diagram.png](http://dl.dropbox.com/s/tuldntbumwo0608/user_diagram.png)

- 권한 (`Role`)클래스는 JPA의 [복합 식별자](https://github.com/eastshine-high/til/blob/main/java/specification/jpa-hibernate/domain-model/composite-identifiers.md) (`RoleId`)를 이용하여 구성하였습니다.
- `Seller` 는 `User` 를 상속합니다. RDB에서는 `JOIN` 관계로 표현합니다.

### ERD

![http://dl.dropbox.com/s/xid2l7fou0j88p8/user](http://dl.dropbox.com/s/xid2l7fou0j88p8/user)
