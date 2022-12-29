# 관심사의 분리

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
