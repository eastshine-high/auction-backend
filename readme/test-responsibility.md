# 테스트 코드 작성을 통한 올바른 책임의 이해(캡슐화)

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
