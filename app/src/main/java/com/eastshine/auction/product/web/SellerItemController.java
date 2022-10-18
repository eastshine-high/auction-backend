package com.eastshine.auction.product.web;

import com.eastshine.auction.common.model.UserInfo;
import com.eastshine.auction.product.application.SellerItemService;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemMapper;
import com.eastshine.auction.product.web.dto.SellerItemDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonValue;
import java.net.URI;

@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SELLER')")
@RequestMapping("/seller-api/items")
@RestController
public class SellerItemController {
    private final SellerItemService sellerItemService;
    private final ObjectMapper objectMapper;

    /**
     * 아이템을 등록 후, 등록된 아이템의 URI를 응답한다.
     *
     * @param sellerItemRegistrationRequest 등록할 아이템 정보.
     * @return 등록된 상품의 URI.
     */
    @PostMapping
    public ResponseEntity registerItem(@RequestBody @Validated SellerItemDto.RegistrationRequest sellerItemRegistrationRequest) {
        Item registeredItem = sellerItemService.registerItem(sellerItemRegistrationRequest);
        return ResponseEntity.created(URI.create("/seller-api/items/" + registeredItem.getId())).build();
    }

    @GetMapping("/{id}")
    public SellerItemDto.Info getItem(
            @PathVariable Long id,
            Authentication authentication
    ) {
        UserInfo userInfo = (UserInfo)authentication.getPrincipal();
        Item itemInfo = sellerItemService.getItem(id, userInfo.getId());
        return ItemMapper.INSTANCE.toDto(itemInfo);
    }

    /**
     * 아이템 정보를 패치하고 패치 결과를 응답합니다.
     *
     * @param itemId 패치할 아이템 정보의 식별자.
     * @param sellerItemPatchRequest 패치 요청 정보.
     * @param authentication 인증 정보.
     */
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void patchItem(
            @PathVariable Long itemId,
            @RequestBody SellerItemDto.PatchRequest sellerItemPatchRequest,
            Authentication authentication
    ) {
        JsonValue jsonValue = objectMapper.convertValue(sellerItemPatchRequest, JsonValue.class);
        JsonMergePatch patchDocument = Json.createMergePatch(jsonValue);
        UserInfo userInfo = (UserInfo)authentication.getPrincipal();
        sellerItemService.patchItem(itemId, patchDocument, userInfo.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteItem(
            @PathVariable Long id,
            Authentication authentication
    ) {
        UserInfo userInfo = (UserInfo)authentication.getPrincipal();
        sellerItemService.deleteItem(id, userInfo.getId());
    }
}
