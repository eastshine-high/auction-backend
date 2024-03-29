# 외래키와 복합키 사용에 대하여

개인적으로 참여했던 실무 프로젝트에서는 개발 편의성과 유연성을 이유로 외래키와 복합키를 잘 사용하지 않았습니다. 이번 토이 프로젝트를 기회로 이를 직접 경험하고 관련 글들을 읽어보면서, 이에 대한 생각을 정리해 볼 수 있었습니다.

외래키 사용에 대하여

- **무결성과 정합성** : 외래키 사용의 가장 큰 장점이라고 생각합니다. 외래키가 설정되어있는 테이블 또는 데이터를 변경할 때, 참조 무결성에 위배되는 데이터가 있을 경우, 즉시 오류가 발생하므로 해당 작업을 수행할 수 없습니다. 따라서 변경 작업 전에 해당 문제가 해결이 되어야 데이터 또는 테이블을 변경할 수 있습니다. 이는 인지하지 못했던 데이터 오류를 사전에 확인하고 조치할 수 있도록 합니다.
- **관리포인트 증가** : 외래키를 설정하면서 `RESTRICT` , `ON UPDATE SET NULL` , `ON DELETE CASCADE`와 같은 옵션을 넣든 넣지 않든, 어느 쪽이든 신경 써야 할 부분이 늘어납니다. 데이터의 양이 더 많아지고 관계가 복잡해질수록 신경 써야 할 부분은 더욱 많아질 수 있습니다. 특히 `ON DELETE CASCADE` 와 같은 옵션은 매우 주의해서 사용해야 합니다.
- **개발 편의성과 변경의 유연성에 대하여** : 위의 두 가지 등의 이유로 외래키의 사용은 개발 편의성과 변경의 유연성이 떨어집니다. 즉, 개발 편의성과 변경의 유연성은 무결성 그리고 정합성과 트레이드 오프 관계로 볼 수 있습니다. 특히 변경이 자주 발생하는 개발 초기 단계에서는 무결성 문제로 인해 변경 작업에 어려움을 겪을 수 있기 때문에, 개발이 안정화 되는 단계에서 외래키를 적용하는 것도 하나의 방법이 될 수 있습니다.
- **인덱스** : 데이터베이스는 외래키를 설정하는 테이블의 칼럼에 자동으로 인덱스를 생성합니다. 따라서 외래키를 사용하지 않지만 해당 칼럼으로 테이블 조인이 자주 발생한다면, 인덱스 생성이 강력히 권장됩니다.
- **성능** : 외래키 제약조건이 있는 테이블의 경우, 부모-자식 관계로 정의된 컬럼에 대해서 두 테이블 데이터가 일치해야 하기 때문에, 외래키로 정의된 동일 데이터(레코드)에 대해 DML 작업이 발생하게 되면, Lock으로 인해 대기해야 하는 상황이 발생합니다.  따라서 대량의 트랜잭션이 발생하는 경우라면 외래키 사용을 지양해야 할 필요가 있습니다. 성능의 차이에 대해서는 다음 [블로그](https://martin-son.github.io/Martin-IT-Blog/mysql/foreign) 를 참고해 볼 수 있습니다.

복합키 사용에 대하여

- **주의 사항** : 복합키를 정의할 때는, 복합키를 구성하는 칼럼의 순서에 주의할 필요가 있었습니다. DBMS는 자동으로 복합키를 구성하는 칼럼의 순서대로 인덱스를 생성합니다. 이 때, 복합 인덱스의 선두 칼럼의 카디널리티에 따라서 인덱스의 성능 차이가 발생할 수 있습니다. 따라서 카디널리티가 높은 칼럼의 순서대로 복합키의 순서를 구성하는 것이 좋습니다.
- **인덱스** : 만약 복합키를 사용하지 않고 인조 식별자를 기본키를 사용한다면, 복합키로 선언하지 않은 칼럼들은 인덱스로 구성하는 것을 고려할 필요가 있습니다.
