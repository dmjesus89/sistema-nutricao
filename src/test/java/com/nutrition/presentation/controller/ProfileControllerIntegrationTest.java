package com.nutrition.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutrition.application.dto.auth.RegisterRequest;
import com.nutrition.application.dto.profile.CreateProfileRequest;
import com.nutrition.application.dto.profile.WeightUpdateRequest;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.infrastructure.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ProfileControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("João")
                .lastName("Silva")
                .email("joao.profile@teste.com")
                .password("senha123456")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        User user = userRepository.findByEmail("joao.profile@teste.com").orElseThrow();
        user.setEnabled(true);
        user.setEmailConfirmed(true);
        userRepository.save(user);
    }

    @Test
    @DisplayName("Should create profile successfully")
    @WithMockUser(username = "joao.profile@teste.com")
    void shouldCreateProfileSuccessfully() throws Exception {
        CreateProfileRequest request = CreateProfileRequest.builder()
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender("MALE")
                .height(BigDecimal.valueOf(180.0))
                .currentWeight(BigDecimal.valueOf(80.0))
                .targetWeight(BigDecimal.valueOf(75.0))
                .activityLevel("MODERATELY_ACTIVE")
                .goal("LOSE_WEIGHT")
                .build();

        mockMvc.perform(post("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())


                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Perfil criado com sucesso")))
                .andExpect(jsonPath("$.data.id",notNullValue()))
                .andExpect(jsonPath("$.data.age", is(35)))
                .andExpect(jsonPath("$.data.gender", is("MALE")))
                .andExpect(jsonPath("$.data.height", is(180.0)))
                .andExpect(jsonPath("$.data.goal", is("LOSE_WEIGHT")))
                .andExpect(jsonPath("$.data.basalMetabolicRate", is(1755.0)))
                .andExpect(jsonPath("$.data.totalDailyEnergyExpenditure", is(2720.25)))
                .andExpect(jsonPath("$.data.bodyMassIndex", is(24.69)))
                .andExpect(jsonPath("$.data.birthDate",  is("1990-05-15")))
                .andExpect(jsonPath("$.data.genderDisplay", is("Masculino")))
                .andExpect(jsonPath("$.data.currentWeight", is(80.0)))
                .andExpect(jsonPath("$.data.targetWeight", is(75.0)))
                .andExpect(jsonPath("$.data.activityLevel",  is("MODERATELY_ACTIVE")))
                .andExpect(jsonPath("$.data.goalDisplay", is("Perder peso")))
                .andExpect(jsonPath("$.data.dailyCalories", is(2220)))
                .andExpect(jsonPath("$.data.targetWeight", is(75.0)))
                .andExpect(jsonPath("$.data.bodyMassIndexCategory", is("Peso normal")))
                .andExpect(jsonPath("$.data.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("Should not create duplicate profile")
    @WithMockUser(username = "joao.profile@teste.com")
    void shouldNotCreateDuplicateProfile() throws Exception {
        // Create first profile
        CreateProfileRequest request = CreateProfileRequest.builder()
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender("MALE")
                .height(BigDecimal.valueOf(180.0))
                .currentWeight(BigDecimal.valueOf(80.0))
                .targetWeight(BigDecimal.valueOf(75.0))
                .activityLevel("MODERATELY_ACTIVE")
                .goal("LOSE_WEIGHT")
                .build();

        mockMvc.perform(post("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Try to create second profile
        mockMvc.perform(post("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("já possui perfil")));
    }

    @Test
    @DisplayName("Should validate profile creation fields")
    @WithMockUser(username = "joao.profile@teste.com")
    void shouldValidateProfileCreationFields() throws Exception {
        CreateProfileRequest request = CreateProfileRequest.builder()
                .birthDate(LocalDate.now().plusDays(1)) // Future date - invalid
                .gender("INVALID_GENDER")
                .height(BigDecimal.valueOf(50.0)) // Too low
                .currentWeight(BigDecimal.valueOf(20.0)) // Too low
                .build();

        mockMvc.perform(post("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update weight successfully")
    @WithMockUser(username = "joao.profile@teste.com")
    void shouldUpdateWeightSuccessfully() throws Exception {
        // First create profile
        createTestProfile();

        WeightUpdateRequest request = WeightUpdateRequest.builder()
                .weight(BigDecimal.valueOf(79.5))
                .notes("Progresso semanal")
                .build();

        mockMvc.perform(post("/api/v1/profile/weight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.weight", is(79.5)))
                .andExpect(jsonPath("$.data.notes", is("Progresso semanal")))
                .andExpect(jsonPath("$.data.recordedDate", notNullValue()));
    }

    @Test
    @DisplayName("Should get profile successfully")
    @WithMockUser(username = "joao.profile@teste.com")
    void shouldGetProfileSuccessfully() throws Exception {
        // First create profile
        createTestProfile();

        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.gender", is("MALE")))
                .andExpect(jsonPath("$.data.height", is(180.0)))
                .andExpect(jsonPath("$.data.basalMetabolicRate", notNullValue()));
    }

    @Test
    @DisplayName("Should get weight history successfully")
    @WithMockUser(username = "joao.profile@teste.com")
    void shouldGetWeightHistorySuccessfully() throws Exception {
        // First create profile and add weight
        createTestProfile();
        addTestWeight();

        mockMvc.perform(get("/api/v1/profile/weight/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", isA(java.util.List.class)))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Should get TotalDailyEnergyExpenditure calculation successfully")
    @WithMockUser(username = "joao.profile@teste.com")
    void shouldGetTotalDailyEnergyExpenditureCalculationSuccessfully() throws Exception {
        // First create profile
        createTestProfile();

        mockMvc.perform(get("/api/v1/profile/totalDailyEnergyExpenditure"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.basalMetabolicRate", notNullValue()))
                .andExpect(jsonPath("$.data.totalDailyEnergyExpenditure", notNullValue()))
                .andExpect(jsonPath("$.data.dailyCalories", notNullValue()))
                .andExpect(jsonPath("$.data.calculationMethod", is("Mifflin-St Jeor")));
    }

    // Helper methods
    private void createTestProfile() throws Exception {
        CreateProfileRequest request = CreateProfileRequest.builder()
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender("MALE")
                .height(BigDecimal.valueOf(180.0))
                .currentWeight(BigDecimal.valueOf(80.0))
                .targetWeight(BigDecimal.valueOf(75.0))
                .activityLevel("MODERATELY_ACTIVE")
                .goal("LOSE_WEIGHT")
                .build();

        mockMvc.perform(post("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private void addTestWeight() throws Exception {
        WeightUpdateRequest request = WeightUpdateRequest.builder()
                .weight(BigDecimal.valueOf(79.5))
                .notes("Peso de teste")
                .build();

        mockMvc.perform(post("/api/v1/profile/weight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }


}