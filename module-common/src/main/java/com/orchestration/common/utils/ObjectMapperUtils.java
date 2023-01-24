package com.orchestration.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils extends ObjectMapper {
    private ObjectMapperUtils() {
//        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        this.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
//        this.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);
    }

    public static ObjectMapperUtils getInstance() {
        return new ObjectMapperUtils();
    }
}
