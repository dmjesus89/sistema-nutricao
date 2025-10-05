package com.nutrition.application.service;

import com.nutrition.application.dto.tracking.DailyWaterSummary;
import com.nutrition.application.dto.tracking.WaterIntakeRequest;
import com.nutrition.application.dto.tracking.WaterIntakeResponse;
import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.tracking.WaterIntake;
import com.nutrition.infrastructure.repository.WaterIntakeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WaterIntakeService {

    private final WaterIntakeRepository waterIntakeRepository;

    // Meta diária padrão (pode vir do perfil do usuário futuramente)
    private static final BigDecimal DEFAULT_DAILY_TARGET = new BigDecimal("2000"); // 2L

    @Transactional
    public WaterIntakeResponse addWaterIntake(User user, WaterIntakeRequest request) {
        WaterIntake waterIntake = WaterIntake.builder()
                .user(user)
                .amountMl(request.getAmount())
                .intakeDate(LocalDate.now())
                .intakeTime(LocalDateTime.now())
                .notes(request.getNotes())
                .build();

        WaterIntake saved = waterIntakeRepository.save(waterIntake);

        log.info("Water intake added for user {}: {}ml", user.getId(), request.getAmount());

        return mapToResponse(saved);
    }

    public DailyWaterSummary getTodaySummary(User user) {
        return getDailySummary(user, LocalDate.now());
    }


    // Updated WaterIntakeService.java methods
    public DailyWaterSummary getDailySummary(User user, LocalDate date) {
        List<WaterIntake> intakes = waterIntakeRepository
                .findByUserAndIntakeDateOrderByIntakeTimeDesc(user, date);

        BigDecimal totalIntake = waterIntakeRepository
                .getTotalIntakeByUserAndDate(user, date);

        BigDecimal target = DEFAULT_DAILY_TARGET; // 2000ml

        double progressPercentage = totalIntake.divide(target, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();

        List<WaterIntakeResponse> intakeResponses = intakes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return DailyWaterSummary.builder()
                .date(date)
                .totalAmount(new BigDecimal(totalIntake.intValue()))
                .targetAmount(new BigDecimal(target.intValue()))
                .percentageOfTarget(Math.min(progressPercentage, 100.0))
                .intakeCount(intakes.size())
                .intakes(intakeResponses)
                .build();
    }

    public Page<DailyWaterSummary> getDailyHistory(User user, int page, int size) {
        // Para histórico dos últimos 10 dias, ignore a paginação e busque diretamente
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(9); // 10 dias incluindo hoje

        List<DailyWaterSummary> summaries = new ArrayList<>();

        // Cria um resumo para cada um dos últimos 10 dias
        for (LocalDate date = endDate; !date.isBefore(startDate); date = date.minusDays(1)) {
            DailyWaterSummary summary = getDailySummaryForHistory(user, date);
            summaries.add(summary);
        }

        return new PageImpl<>(summaries, PageRequest.of(0, 10), summaries.size());
    }

    private DailyWaterSummary getDailySummaryForHistory(User user, LocalDate date) {
        BigDecimal totalIntake = waterIntakeRepository.getTotalIntakeByUserAndDate(user, date);
        int intakeCount = waterIntakeRepository.countByUserAndIntakeDate(user, date);
        BigDecimal target = DEFAULT_DAILY_TARGET;

        double progressPercentage = totalIntake.divide(target, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();

        return DailyWaterSummary.builder()
                .date(date)
                .totalAmount(totalIntake)
                .targetAmount(target)
                .percentageOfTarget(Math.min(progressPercentage, 100.0))
                .intakeCount(intakeCount)
                .intakes(List.of()) // Vazio no histórico
                .build();
    }

    @Transactional
    public void deleteIntake(User user, Long intakeId) {
        WaterIntake intake = waterIntakeRepository.findById(intakeId)
                .orElseThrow(() -> new RuntimeException("Registro de água não encontrado"));

        if (!intake.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado ao registro");
        }

        waterIntakeRepository.delete(intake);
        log.info("Water intake deleted: {} for user {}", intakeId, user.getId());
    }

    private WaterIntakeResponse mapToResponse(WaterIntake intake) {
        return WaterIntakeResponse.builder()
                .id(intake.getId())
                .amount(intake.getAmountMl())
                .intakeDate(intake.getIntakeDate())
                .intakeTime(intake.getIntakeTime())
                .notes(intake.getNotes())
                .createdAt(intake.getCreatedAt())
                .build();


    }
}