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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_supplement_preferences",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "supplement_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSupplementPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplement_id", nullable = false)
    private Supplement supplement;

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
        FAVORITE("Favorito", "Suplemento favorito do usuário"),
        CURRENTLY_USING("Usando atualmente", "Suplemento em uso atual"),
        USED_BEFORE("Já usei", "Suplemento usado anteriormente"),
        WANT_TO_TRY("Quero experimentar", "Suplemento de interesse"),
        RESTRICTION("Restrição", "Suplemento com restrição ou contraindicação"),
        NOT_SUITABLE("Não adequado", "Suplemento não adequado para o usuário");

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
            return this == RESTRICTION || this == NOT_SUITABLE;
        }

        public boolean isPositive() {
            return this == FAVORITE || this == CURRENTLY_USING || this == WANT_TO_TRY;
        }
    }
}
