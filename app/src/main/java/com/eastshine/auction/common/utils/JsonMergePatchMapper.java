package com.eastshine.auction.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.json.JsonMergePatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;

@Component
public class JsonMergePatchMapper<T> {
    private final ObjectMapper objectMapper;

    public JsonMergePatchMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public T apply(JsonMergePatch patch, T targetBean) {
        // Convert the Java bean to a JSON document
        JsonStructure target = objectMapper.convertValue(targetBean, JsonStructure.class);

        // Apply the JSON Patch to the JSON document
        JsonValue patched = patch.apply(target);

        // Convert the JSON document to a Java bean and return it
        return objectMapper.convertValue(patched, (Class<T>)targetBean.getClass());
    }
}
