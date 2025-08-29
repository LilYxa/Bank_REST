package com.example.bankcards.repository;

import com.example.bankcards.entity.Token;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByToken(String token);

    List<Token> findAllByUserAndRevokedFalseAndExpiredFalse(User user);

    void deleteAllByUser(User user);
}
