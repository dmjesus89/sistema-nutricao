package com.nutrition.infrastructure.repository;

import com.nutrition.domain.entity.auth.User;
import com.nutrition.domain.entity.food.Food;
import com.nutrition.domain.entity.food.UserFoodPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFoodPreferenceRepository extends JpaRepository<UserFoodPreference, Long> {


    Optional<UserFoodPreference> findByUserAndFood(User user, Food food);


    @Query("SELECT ufp FROM UserFoodPreference ufp WHERE ufp.user = :user AND ufp.preferenceType = 'FAVORITE'")
    Collection<UserFoodPreference> findByUserAndPreferenceTypeFavorite(@Param("user") User user);

    @Query("SELECT ufp FROM UserFoodPreference ufp WHERE ufp.user = :user AND " +
            "ufp.preferenceType IN :preferenceTypes ORDER BY ufp.createdAt DESC")
    List<UserFoodPreference> findByUserAndPreferenceTypes(@Param("user") User user,
                                                          @Param("preferenceTypes") List<UserFoodPreference.PreferenceType> preferenceTypes);

    @Modifying
    void deleteByUserAndFood(User user, Food food);


}
