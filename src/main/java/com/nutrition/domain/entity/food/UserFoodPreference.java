package com.nutrition.domain.entity.food;

import com.nutrition.domain.entity.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_food_preferences",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "food_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFoodPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Enumerated(EnumType.STRING)
    @Column(name = "preference_type", nullable = false)
    private PreferenceType preferenceType;

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

    public enum PreferenceType {
        FAVORITE("Favorito", "Alimento favorito do usuário"),
        RESTRICTION("Restrição", "Alimento com restrição (alergia, intolerância, etc.)"),
        DISLIKE("Não gosta", "Alimento que o usuário não gosta"),
        AVOID("Evitar", "Alimento que o usuário prefere evitar");

        private final String displayName;
        private final String description;

        PreferenceType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isRestriction() {
            return this == RESTRICTION || this == AVOID;
        }

        public boolean isPositive() {
            return this == FAVORITE;
        }
    }
}