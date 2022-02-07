package com.logitex.notifierbot.repository.bot;

import com.logitex.notifierbot.model.bot.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
