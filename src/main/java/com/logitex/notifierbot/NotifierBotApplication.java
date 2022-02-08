package com.logitex.notifierbot;

import com.logitex.notifierbot.model.bot.Kid;
import com.logitex.notifierbot.model.bot.User;
import com.logitex.notifierbot.model.bot.UserKid;
import com.logitex.notifierbot.model.perco.Staff;
import com.logitex.notifierbot.model.perco.TabelIntermediadate;
import com.logitex.notifierbot.service.BotService;
import com.logitex.notifierbot.service.PercoService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
@EnableScheduling
public class NotifierBotApplication implements CommandLineRunner {
    private final Bot bot;
    private final BotService botService;
    private final PercoService percoService;

    public static void main(String[] args) {
        SpringApplication.run(NotifierBotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            List<TabelIntermediadate> entries = percoService.getEntries();
            if (!entries.isEmpty()) {
                for (TabelIntermediadate tabelIntermediadate : entries) {
                    Staff staff = percoService.getStaffById(tabelIntermediadate.getStaffID());
                    Kid kid = botService.getKidByTabelId(staff.getTabelID());
                    List<UserKid> userKids = botService.getByKid(kid);
                    String time = percoService.getTime(tabelIntermediadate);
                    Integer passType = tabelIntermediadate.getType();
                    for (UserKid userKid : userKids) {
                        bot.sendMessageToUser(userKid.getUser(), userKid.getKid(), passType, time);
                    }
                }
            }
            Thread.sleep(1000*60);
        }
    }
}
