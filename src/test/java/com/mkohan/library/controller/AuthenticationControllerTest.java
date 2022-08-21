package com.mkohan.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkohan.library.dto.AuthenticationRequest;
import com.mkohan.library.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static com.mkohan.library.config.UserDetailsConfiguration.ADMIN_PASSWORD;
import static com.mkohan.library.config.UserDetailsConfiguration.ADMIN_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void login_incorrectCredentials() throws Exception {
        AuthenticationRequest payload = new AuthenticationRequest("random", "password");

        RequestBuilder requestBuilder = post("/login")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    public void login_successful() throws Exception {
        AuthenticationRequest payload = new AuthenticationRequest(ADMIN_USERNAME, ADMIN_PASSWORD);

        RequestBuilder requestBuilder = post("/login")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON);

        String token = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String username = JwtTokenUtils.tryExtractUsername(token).orElseThrow();

        assertThat(username).isEqualTo(ADMIN_USERNAME);
    }
}
