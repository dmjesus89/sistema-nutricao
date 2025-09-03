package com.nutrition.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutrition.application.dto.auth.LoginRequest;
import com.nutrition.application.dto.auth.RegisterRequest;
import com.nutrition.infrastructure.repository.EmailConfirmationTokenRepository;
import com.nutrition.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailConfirmationTokenRepository confirmationTokenRepository;

    @Test
    @DisplayName("Should register user successfully")
    @WithMockUser
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("João")
                .lastName("Silva")
                .email("joao@exemplo.com")
                .password("senha123456")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", nullValue()))
                .andExpect(jsonPath("$.data", is("Usuário registrado com sucesso. Verifique seu email para confirmar a conta.")));
    }

    @Test
    @DisplayName("Should not register user with existing email")
    @WithMockUser
    void shouldNotRegisterUserWithExistingEmail() throws Exception {
        // First registration
        RegisterRequest request1 = RegisterRequest.builder()
                .firstName("João")
                .lastName("Silva")
                .email("joao@exemplo.com")
                .password("senha123456")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Second registration with same email
        RegisterRequest request2 = RegisterRequest.builder()
                .firstName("Maria")
                .lastName("Santos")
                .email("joao@exemplo.com")
                .password("outrasenha123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Email já está em uso")));
    }

    @Test
    @DisplayName("Should validate required fields in registration")
    @WithMockUser
    void shouldValidateRequiredFieldsInRegistration() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("email-invalido")
                .password("123") // Too short
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should login successfully with confirmed account")
    @WithMockUser
    void shouldLoginSuccessfullyWithConfirmedAccount() throws Exception {
        // Create and confirm user manually for testing
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("João")
                .lastName("Silva")
                .email("joao@exemplo.com")
                .password("senha123456")
                .build();

        // Register user
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Get confirmation token and confirm
        var user = userRepository.findByEmail("joao@exemplo.com").orElseThrow();
        var token = confirmationTokenRepository.findPendingTokensByUser(user).get(0);

        mockMvc.perform(get("/api/v1/auth/confirm")
                        .param("token", token.getToken()))
                .andExpect(status().isOk());

        // Now login
        LoginRequest loginRequest = LoginRequest.builder()
                .email("joao@exemplo.com")
                .password("senha123456")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Login realizado com sucesso")))
                .andExpect(jsonPath("$.data.user.id",notNullValue()))
                .andExpect(jsonPath("$.data.user.email", is("joao@exemplo.com")))
                .andExpect(jsonPath("$.data.user.role", is("USER")))
                .andExpect(jsonPath("$.data.user.firstName", is("João")))
                .andExpect(jsonPath("$.data.user.lastName", is("Silva")))
                .andExpect(jsonPath("$.data.user.fullName", is("João Silva")))
                .andExpect(jsonPath("$.data.user.emailConfirmed", is(true)))
                .andExpect(jsonPath("$.data.user.createdAt", notNullValue()))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.data.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.data.expiresIn", is(86400)));
    }
}
