package com.logitex.notifierbot.service;

import com.logitex.notifierbot.model.bot.User;
import com.logitex.notifierbot.model.bot.UserKid;
import com.logitex.notifierbot.repository.bot.KidRepository;
import com.logitex.notifierbot.repository.bot.UserKidRepository;
import com.logitex.notifierbot.repository.bot.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
public class BotService {
    private final UserRepository userRepository;
    private final KidRepository kidRepository;
    private final UserKidRepository userKidRepository;

    public void registration(String firstName, String lastName, String phoneNumber) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
    }

    public boolean checkRegistered(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public void unsubscribe(Long chatID) {
        userRepository.delete(userRepository.getById(chatID));
        userKidRepository.deleteAll(userKidRepository.findByUser(userRepository.getById(chatID)));
    }
}
