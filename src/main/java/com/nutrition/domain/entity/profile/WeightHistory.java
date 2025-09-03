package com.nutrition.domain.entity.profile;

import com.nutrition.domain.entity.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weight_history",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "recorded_date"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal weight; // em quilogramas

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Método para calcular diferença com peso anterior
    public BigDecimal calculateWeightDifference(BigDecimal previousWeight) {
        if (previousWeight == null) {
            return BigDecimal.ZERO;
        }
        return weight.subtract(previousWeight);
    }

    // Método para verificar se é uma medição recente (últimos 7 dias)
    public boolean isRecentMeasurement() {
        return recordedDate.isAfter(LocalDate.now().minusDays(7));
    }
}