package com.eastshine.auction.product.application;

import com.eastshine.auction.common.exception.EntityNotFoundException;
import com.eastshine.auction.common.utils.JsonMergePatchMapper;
import com.eastshine.auction.product.domain.item.Item;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import com.eastshine.auction.product.domain.item.option.ItemOptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonValue;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SellerItemOptionService {
    private final ObjectMapper objectMapper;
    private final JsonMergePatchMapper<ItemOption> mergeMapper;
    private final ItemOptionRepository itemOptionRepository;

    public List<ItemOption> patchItemOptions(Item item, List<ItemOption> itemOptions) {
        return itemOptions.stream()
                .map(patchRequest -> {
                    ItemOption itemOption = findProductOption(patchRequest);

                    JsonValue jsonValue = objectMapper.convertValue(patchRequest, JsonValue.class);
                    JsonMergePatch patchDocument = Json.createMergePatch(jsonValue);

                    ItemOption patchedOption = mergeMapper.apply(patchDocument, itemOption);
                    patchedOption.setItem(item);
                    return patchedOption;
                })
                .collect(Collectors.toList());
    }

    private ItemOption findProductOption(ItemOption patchRequest) {
        return itemOptionRepository.findById(patchRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException());
    }
}
