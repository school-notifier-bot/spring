package com.logitex.notifierbot;

import com.logitex.notifierbot.config.BotConfig;
import com.logitex.notifierbot.model.bot.Kid;
import com.logitex.notifierbot.model.perco.Staff;
import com.logitex.notifierbot.service.BotService;
import com.logitex.notifierbot.service.PercoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final BotConfig config = new BotConfig();
    private final BotService botService;
    private final PercoService percoService;

    private String deleteEmoji = "❌";
    private String addEmohi = "\uD83D\uDCDD";
    private String checkMarkEmoji = "✔";

    private String numberRequestInlineButton = "Зарегистрироваться";
    private String enterKidTabelID = addEmohi + " Ввести табельный номер ребенка " + addEmohi;
    private String deleteKid = deleteEmoji + " Отменить привязку " + deleteEmoji;
    private String kidDeleted = "Привязка отменена " + checkMarkEmoji;
    private String deleteRus = "Отменить";
    private String delete = "delete";

    private String enterKidTabelIDResponse = "Пожалуйста введите табельный номер вашего ребенка";
    private String numberRequest = "Для продолжения работы нужна регистрация";
    private String welcome = "Регистрация завершена! Добро пожаловать!\nИспользуйте кнопку : \n'" + enterKidTabelID + "'\n для продолжения работы.";
    private String pleaseUserRegistrationButton = "Пожалуйста используйте кнопку \n'" + numberRequestInlineButton + "'";
    private String notFound = "Не найдено совпадений";
    private String found = "Ребенок найден!";

    @Override
    public void onUpdateReceived(Update update) {
        try {
            SendMessage sm = new SendMessage();
            Long chatID = null;

            if (update.getMessage() != null) {
                chatID = update.getMessage().getChatId();
                sm.setChatId(String.valueOf(chatID));

                if (update.getMessage().getContact().getUserId() != null
                        && update.getMessage().getContact().getUserId().equals(update.getMessage().getFrom().getId())) {
                    String phoneNumber = update.getMessage().getContact().getPhoneNumber();
                    String firstName = update.getMessage().getContact().getFirstName();
                    String lastName = update.getMessage().getContact().getLastName();
                    botService.registration(chatID, firstName, lastName, phoneNumber);
                    sm.setText(welcome);
                } else {
                    sm.setText(pleaseUserRegistrationButton);
                    try {
                        execute(sm);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else if (update.getMessage().getReplyToMessage() != null) {
                String message = update.getMessage().getText();
                String replyMessage = update.getMessage().getReplyToMessage().getText();
                Long staffId = Long.parseLong(message);
                if (replyMessage.equals(enterKidTabelIDResponse)) {
                    Kid kid = botService.getKidByStaffId(staffId);
                    if (kid == null) {
                        Staff staff = percoService.getStaffById(staffId);
                        if (staff != null) {
                            botService.insertKid(staff);
                        }
                    }

                    if (kid != null) {
                        sm.setText(found + "\n" + kid.getFull_fio());
                        botService.insertUserKid(chatID, staffId);
                    } else {
                        sm.setText(notFound);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}
