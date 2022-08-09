package com.eastshine.auction.product.web;

import com.eastshine.auction.product.CategoryFactory;
import com.eastshine.auction.common.test.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DisplayCategoryControllerTest extends RestDocsTest {

    @Autowired CategoryFactory categoryFactory;

    @BeforeEach
    void setUp() {
        categoryFactory.deleteAllCategory(); // product 커밋한 다음에 개선, 걸쳐있는 게 많다.

        categoryFactory.createCategory(101, "패션/뷰티", 1);
        categoryFactory.createCategory(1010001, 101, "스킨케어", 1);
        categoryFactory.createCategory(1010002, 101, "헤어/바디/미용", 2);
        categoryFactory.createCategory(102, "디지털/가전", 2);
        categoryFactory.createCategory(1020001, 102, "가공식품/즉석/간신", 1);
        categoryFactory.createCategory(103, "가구/생활/자동차", 3);
    }

    @Test
    void getDisplayCategories() throws Exception {
        mockMvc.perform(
                get("/api/display/categories")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("103")))
                .andDo(document("get-displayCategories-200"));
    }
}
