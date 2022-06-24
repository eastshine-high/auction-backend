package com.eastshine.auction.common.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
    protected static final String VALID_AUTHENTICATION = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJ1c2VySW5mbyI6eyJpZCI6MSwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsIm5pY2tuYW1lIjoibmlja25hbWUiLCJyb2xlcyI6WyJVU0VSIiwiQURNSU4iXX19" +
            ".2s3sfdLWmcdyT4FeXz8wzKeODyPmxkLHJSF8jmGwOPI";
    protected static final String INVALID_AUTHENTICATION = "INVALID_TOKEN";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String createJson(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }
}
