# REST API의 예외(Exception) 처리

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
