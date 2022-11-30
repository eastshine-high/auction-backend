package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.utils.JsonMergePatchMapper;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.ItemMapper;
import com.eastshine.auction.product.domain.item.ItemRepository;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import com.eastshine.auction.product.web.dto.SellerItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.JsonMergePatch;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SellerItemService {
    private final JsonMergePatchMapper<Item> mergeMapper;
    private final Validator validator;
    private final ItemRepository itemRepository;
    private final SellerItemOptionService sellerItemOptionService;

    @Transactional
    public Item registerItem(SellerItemDto.ItemRegistration registrationRequest) {
        Item item = registrationRequest.toEntity();
        registrationRequest.getItemOptions().stream()
                .forEach(itemOptionRegistration -> item.addItemOption(itemOptionRegistration.toEntity()));
        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public Item getItem(Long itemId, Long accessorId) {
        Item item = itemRepository.findEagerById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ITEM_NOT_FOUND));
        item.validateAccessibleUser(accessorId);
        return item;
    }

    @Transactional
    public Item patchItem(Long itemId, JsonMergePatch patchDocument, Long accessor) {
        Item item = findItem(itemId);
        item.validateAccessibleUser(accessor);

        Item patchedItem = mergeMapper.apply(patchDocument, item);
        List<ItemOption> patchedItemOptions = sellerItemOptionService.patchItemOptions(item, patchedItem.getItemOptions());
        patchedItem.setItemOptions(patchedItemOptions);

        validatePatchedItem(patchedItem);
        return itemRepository.save(patchedItem);
    }

    private void validatePatchedItem(Item patchedItem) {
        SellerItemPatchValidationBean sellerItemPatchValidationBean = ItemMapper.INSTANCE.toValidationBean(patchedItem);
        Set<ConstraintViolation<SellerItemPatchValidationBean>> violations = validator.validate(sellerItemPatchValidationBean);

        if (!violations.isEmpty()) {
            ConstraintViolation<SellerItemPatchValidationBean> constraintViolation = violations.stream().findFirst().get();
            throw new InvalidArgumentException(constraintViolation.getPropertyPath() + " : " +constraintViolation.getMessage());
        }
    }

    @Transactional
    public void deleteItem(Long itemId, Long accessorId) {
        Item item = findItem(itemId);
        item.validateAccessibleUser(accessorId);
        itemRepository.delete(item);
    }

    private Item findItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ITEM_NOT_FOUND));
    }
}
