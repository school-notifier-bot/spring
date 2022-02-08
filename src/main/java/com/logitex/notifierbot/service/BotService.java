package com.logitex.notifierbot.service;

import com.logitex.notifierbot.model.bot.Kid;
import com.logitex.notifierbot.model.bot.User;
import com.logitex.notifierbot.model.bot.UserKid;
import com.logitex.notifierbot.model.perco.Staff;
import com.logitex.notifierbot.repository.bot.KidRepository;
import com.logitex.notifierbot.repository.bot.UserKidRepository;
import com.logitex.notifierbot.repository.bot.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BotService {
    private final UserRepository userRepository;
    private final KidRepository kidRepository;
    private final UserKidRepository userKidRepository;

    public void registration(Long chatId, String firstName, String lastName) {
        User user = new User();
        user.setID(chatId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber("");
        userRepository.save(user);
    }

    public void setPhoneNumber(Long chatId, String phoneNumber) {
        User user = userRepository.getById(chatId);
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

    public Kid getKidByTabelId(String tabelId) {
        return kidRepository.findByTabelID(tabelId).orElse(null);
    }

    public void insertKid(Staff staff) {
        Kid kid = new Kid();
        kid.setID(staff.getID());
        kid.setFull_fio(staff.getFullFio());
        kid.setTabelID(staff.getTabelID());
        kidRepository.save(kid);
    }

    public void insertUserKid(Long chatId, Long staffId) {
        User user = userRepository.getById(chatId);
        Kid kid = kidRepository.getById(staffId);
        UserKid userKid = new UserKid();
        userKid.setUser(user);
        userKid.setKid(kid);
        userKidRepository.save(userKid);
    }

    public List<UserKid> getUserKids(Long chatId) {
        User user = userRepository.findByID(chatId).orElse(null);
        return userKidRepository.findByUser(user);
    }

    public User getUser(Long chatId) {
        return userRepository.findByID(chatId).orElse(null);
    }

    public void deleteUserKid(Long chatId, Kid kid) {
        User user = userRepository.getById(chatId);
        UserKid userKid = userKidRepository.findByUserAndKid(user, kid);
        userKidRepository.delete(userKid);
    }

    public List<UserKid> getByKid(Kid kid) {
        return userKidRepository.findByKid(kid);
    }
}
