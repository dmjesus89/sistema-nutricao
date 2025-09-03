package com.nutrition.domain.entity.food;

import com.nutrition.domain.entity.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_dietary_restrictions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDietaryRestriction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "restriction_type", nullable = false)
    private DietaryRestrictionType restrictionType;

    @Column(name = "severity", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Severity severity = Severity.MODERATE;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum DietaryRestrictionType {
        VEGETARIAN("Vegetariano", "Não consome carne"),
        VEGAN("Vegano", "Não consome produtos de origem animal"),
        LACTOSE_INTOLERANT("Intolerante à lactose", "Não pode consumir lactose"),
        GLUTEN_FREE("Sem glúten", "Não pode consumir glúten"),
        DIABETIC("Diabético", "Precisa controlar açúcar"),
        HYPERTENSIVE("Hipertenso", "Precisa controlar sódio"),
        KIDNEY_DISEASE("Doença renal", "Restrições para função renal"),
        FOOD_ALLERGY("Alergia alimentar", "Alergia a alimentos específicos"),
        LOW_CARB("Low carb", "Dieta restrita em carboidratos"),
        KETO("Cetogênica", "Dieta cetogênica"),
        PALEO("Paleo", "Dieta paleolítica"),
        HALAL("Halal", "Alimentação halal"),
        KOSHER("Kosher", "Alimentação kosher"),
        OTHER("Outra", "Outra restrição específica");

        private final String displayName;
        private final String description;

        DietaryRestrictionType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Severity {
        MILD("Leve", "Preferência, pode consumir ocasionalmente"),
        MODERATE("Moderado", "Evita sempre que possível"),
        SEVERE("Severo", "Nunca deve consumir - risco à saúde");

        private final String displayName;
        private final String description;

        Severity(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }
}
