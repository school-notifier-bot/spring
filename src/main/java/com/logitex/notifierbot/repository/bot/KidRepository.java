package com.logitex.notifierbot.repository.bot;

import com.logitex.notifierbot.model.bot.Kid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KidRepository extends JpaRepository<Kid, Long> {
    Optional<Kid> findByTabelID(Long tabelId);
}
