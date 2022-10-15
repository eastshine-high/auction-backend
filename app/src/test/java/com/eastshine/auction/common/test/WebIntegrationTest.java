package com.eastshine.auction.common.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(RestDocumentationExtension.class)
public class WebIntegrationTest extends IntegrationTest{
    protected static final String ACCESS_TOKEN = "Bearer ${ACCESS_TOKEN}";

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp(final WebApplicationContext context,
               final RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation
                        .documentationConfiguration(provider)
                        .operationPreprocessors()
                            .withRequestDefaults(
                                    prettyPrint())
                            .withResponseDefaults(
                                    prettyPrint(),
                                    removeHeaders("Vary"),
                                    removeHeaders("X-XSS-Protection"))
                        .and()
                        .uris()
                            .withScheme("http")
                            .withHost("3.36.136.227")
                            .withPort(80)
                )
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print()) // andDo(print()) 코드 포함
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
                .build();
    }
}

