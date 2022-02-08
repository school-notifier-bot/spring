package com.logitex.notifierbot.repository.bot;

import com.logitex.notifierbot.model.bot.Kid;
import com.logitex.notifierbot.model.bot.User;
import com.logitex.notifierbot.model.bot.UserKid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserKidRepository extends JpaRepository<UserKid, Long> {
    List<UserKid> findByUser(User user);
    UserKid findByUserAndKid(User user, Kid kid);
}
