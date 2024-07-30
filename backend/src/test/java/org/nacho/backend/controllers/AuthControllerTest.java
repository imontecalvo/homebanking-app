package org.nacho.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nacho.backend.dtos.auth.AuthResponse;
import org.nacho.backend.dtos.auth.LoginRequest;
import org.nacho.backend.dtos.auth.RegisterRequest;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.roles_authorities.RoleEnum;
import org.nacho.backend.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthControllerTest {
    @MockBean
    IUserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testRegisterWhenSuccess() throws Exception {
        //Arrange
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("test_user")
                .password("test_password")
                .confirmPassword("test_password")
                .roles(List.of(RoleEnum.USER))
                .currency(Currency.USD)
                .build();
        AuthResponse expectedResponse = AuthResponse.builder()
                .username(registerRequest.getUsername())
                .token("test_token")
                .build();
        when(userService.newUser(registerRequest)).thenReturn(expectedResponse);

        //Act & Assert
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testRegisterWhenFieldsAreMissing() throws Exception {
        //Arrange
        String registerRequestUsernameMissing = "{\n" +
                "\"password\":\"test_password\",\n" +
                "\"confirmPassword\":\"test_password\",\n" +
                "\"roles\":[\"USER\"],\n" +
                "\"currency\":\"USD\"\n" +
                "}";
        String registerRequestPasswordMissing = "{\n" +
                "\"username\":\"test_username\",\n" +
                "\"confirmPassword\":\"test_password\",\n" +
                "\"roles\":[\"USER\"],\n" +
                "\"currency\":\"USD\"\n" +
                "}";
        String registerRequestConfirmPasswordMissing = "{\n" +
                "\"username\":\"test_username\",\n" +
                "\"password\":\"test_password\",\n" +
                "\"roles\":[\"USER\"],\n" +
                "\"currency\":\"USD\"\n" +
                "}";
        String registerRequestRolesMissing = "{\n" +
                "\"username\":\"test_username\",\n" +
                "\"password\":\"test_password\",\n" +
                "\"confirmPassword\":\"test_password\",\n" +
                "\"currency\":\"USD\"\n" +
                "}";
        String registerRequestCurrencyMissing = "{\n" +
                "\"username\":\"test_username\",\n" +
                "\"password\":\"test_password\",\n" +
                "\"confirmPassword\":\"test_password\",\n" +
                "\"roles\":[\"USER\"]\n" +
                "}";

        //Act & Assert
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestUsernameMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestPasswordMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestConfirmPasswordMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestRolesMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestCurrencyMissing))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterWhenCurrencyIsInvalid() throws Exception {
        //Arrange
        String registerRequest = "{\n" +
                "\"username\":\"test_username\",\n" +
                "\"password\":\"test_password\",\n" +
                "\"confirmPassword\":\"test_password\",\n" +
                "\"roles\":[\"USER\"],\n" +
                "\"currency\":\"INVALID\"\n" +
                "}";

        //Act & Assert
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterWhenPasswordLengthIsWrong() throws Exception {
        //Arrange
        String registerRequestPasswordTooShort = "{\n" +
                "\"username\":\"test_username\",\n" +
                "\"password\":\"short\",\n" +
                "\"confirmPassword\":\"short\",\n" +
                "\"roles\":[\"USER\"],\n" +
                "\"currency\":\"USD\"\n" +
                "}";
        String registerRequestPasswordTooLong = "{\n" +
                "\"username\":\"test_username\",\n" +
                "\"password\":\"longlonglonglonglonglong\",\n" +
                "\"confirmPassword\":\"longlonglonglonglonglong\",\n" +
                "\"roles\":[\"USER\"],\n" +
                "\"currency\":\"USD\"\n" +
                "}";

        //Act & Assert
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestPasswordTooShort))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestPasswordTooLong))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterWhenNumberOfRolesIsInvalid() throws Exception {
        //Arrange
        String registerRequestPasswordTooShort = "{\n" +
                "\"username\":\"test_username\",\n" +
                "\"password\":\"test_password\",\n" +
                "\"confirmPassword\":\"test_password\",\n" +
                "\"roles\":[],\n" +
                "\"currency\":\"USD\"\n" +
                "}";
        String registerRequestPasswordTooLong = "{\n" +
                "\"username\":\"test_username\",\n" +
                "\"password\":\"test_password\",\n" +
                "\"confirmPassword\":\"test_password\",\n" +
                "\"roles\":[\"USER\",\"INVITED\",\"ADMIN\",\"USER\"],\n" +
                "\"currency\":\"USD\"\n" +
                "}";

        //Act & Assert
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestPasswordTooShort))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestPasswordTooLong))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginWhenSuccess() throws Exception {
        //Arrange
        LoginRequest loginRequest = LoginRequest.builder()
                .username("test_user")
                .password("test_password")
                .build();

        AuthResponse expectedResponse = AuthResponse.builder()
                .username(loginRequest.getUsername())
                .token("test_token")
                .build();
        when(userService.login(loginRequest)).thenReturn(expectedResponse);

        //Act & Assert
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testLoginWhenFieldsAreMissing() throws Exception {
        //Arrange
        String loginRequestUsernameMissing = "{\n\"password\":\"test_password\"\n}";
        String loginRequestPasswordMissing = "{\n\"username\":\"test_user\"\n}";

        //Act & Assert
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestUsernameMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestPasswordMissing))
                .andExpect(status().isBadRequest());
    }
}
