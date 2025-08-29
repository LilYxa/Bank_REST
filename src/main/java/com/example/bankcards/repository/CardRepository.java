package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    Page<Card> findByUser(User user, Pageable pageable);

    Page<Card> findByUserAndStatus(User user, CardStatus status, Pageable pageable);

    Optional<Card> findByIdAndUser(UUID uuid, User user);

    Page<Card> findByUserAndLastFourDigits(User user, String lastFourDigits, Pageable pageable);

    Page<Card> findByUserAndCardOwnerContainingIgnoreCase(User user, String cardOwner, Pageable pageable);

    List<Card> findByStatus(CardStatus status);

    @Query("SELECT c FROM cards c WHERE c.user = :user AND " +
            "(c.lastFourDigits = :search OR " +
            "LOWER(c.cardOwner) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Card> searchByUser(@Param("user") User user,
                            @Param("search") String search,
                            Pageable pageable);

    boolean existsByLastFourDigitsAndUser(String lastFourDigits, User user);

}
