package com.logitex.notifierbot;

import com.logitex.notifierbot.model.bot.Kid;
import com.logitex.notifierbot.model.bot.User;
import com.logitex.notifierbot.model.bot.UserKid;
import com.logitex.notifierbot.model.perco.Staff;
import com.logitex.notifierbot.service.BotService;
import com.logitex.notifierbot.service.PercoService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    @Getter
    @Value("${bot.username}")
    private String botUsername;

    @Getter
    @Value("${bot.token}")
    private String botToken;
    private final BotService botService;
    private final PercoService percoService;

    private String startCommand = "/start";

    private String deleteEmoji = "❌";
    private String checkMarkEmoji = "✔";
    private String addEmohi = "\uD83D\uDCDD";

    private String numberRequestInlineButton = "Зарегистрироваться";
    private String enterKidTabelID = addEmohi + " Ввести табельный номер ребенка " + addEmohi;
    private String deleteKid = deleteEmoji + " Отменить привязку " + deleteEmoji;
    private String kidDeleted = "Привязка отменена " + checkMarkEmoji;
    private String cancel = "Отменить";
    private String delete = "Удалить";

    private String enterKidTabelIDResponse = "Пожалуйста введите табельный номер вашего ребенка";
    private String numberRequest = "Для продолжения работы нужна регистрация";
    private String welcome = "Регистрация завершена! Добро пожаловать!";
    private String pleaseUserRegistrationButton = "Пожалуйста используйте кнопку \n'" + numberRequestInlineButton + "'";
    private String notFound = "Не найдено совпадений";
    private String found = "Ребенок найден!";
    private String subscribing = "Начинаем наблюдение...";
    private String emptyList = "Список наблюдаемых детей пуст.";

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sm = new SendMessage();
        Long chatID = null;

        if (update.getMessage() != null) {
            chatID = update.getMessage().getChatId();
            sm.setChatId(String.valueOf(chatID));

            if (update.getMessage().getContact() != null) {
                if (update.getMessage().getContact().getUserId() != null && update.getMessage().getContact().getUserId().equals(update.getMessage().getFrom().getId())) {
                    String phoneNumber = update.getMessage().getContact().getPhoneNumber();
                    botService.setPhoneNumber(chatID, phoneNumber);
                    sm.setText(welcome);
                    sm.setReplyMarkup(getReplyKeyboardByUser());
                    try {
                        execute(sm);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
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
                String tabelId = StringUtils.leftPad(message, 20);
                String replyMessage = update.getMessage().getReplyToMessage().getText();
                if (replyMessage.equals(enterKidTabelIDResponse)) {
                    Kid kid = botService.getKidByTabelId(tabelId);
                    if (kid == null) {
                        Staff staff = percoService.getStaffByTabelId(tabelId);
                        if (staff != null) {
                            botService.insertKid(staff);
                            kid = botService.getKidByTabelId(tabelId);
                        }
                    }
                    if (kid != null) {
                        sm.setText(found + "\n" + kid.getFull_fio() + "\n" + subscribing);
                        botService.insertUserKid(chatID, kid.getID());
                    } else {
                        sm.setText(notFound);
                    }
                    try {
                        sm.setReplyMarkup(getReplyKeyboardByUser());
                        execute(sm);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else if (update.getMessage().getText() != null) {
                String messageText = update.getMessage().getText();
                if (messageText.equals(startCommand)) {
                    registerInDB(update, sm);
                } else if (messageText.equals(enterKidTabelID)) {
                    sm.setText(enterKidTabelIDResponse);
                    sm.setReplyMarkup(new ForceReplyKeyboard());
                    try {
                        sendApiMethod(sm);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if (messageText.equals(deleteKid)) {
                    List<UserKid> userKids = botService.getUserKids(chatID);
                    if (userKids.isEmpty()) {
                        sm.setText(emptyList);
                        try {
                            execute(sm);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    for (UserKid userKid : userKids) {
                        String fio = userKid.getKid().getFull_fio();
                        SendMessage sendUserKids = new SendMessage();
                        sendUserKids.setChatId(String.valueOf(chatID));
                        sendUserKids.setText(fio);
                        sendUserKids.setReplyMarkup(oneButtonInlineMarkup(cancel, delete + userKid.getKid().getTabelID()));
                        try {
                            sendApiMethod(sendUserKids);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    sm.setText("Неизвестная команда");
                    try {
                        execute(sm);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (update.getCallbackQuery() != null) {
            chatID = update.getCallbackQuery().getMessage().getChat().getId();
            Integer messageID = update.getCallbackQuery().getMessage().getMessageId();
            if (update.getCallbackQuery().getData() != null) {
                String data = update.getCallbackQuery().getData();
                sm.setChatId(String.valueOf(chatID));

                if (data.contains(delete)) {
                    deleteMessage(chatID, messageID);
                    data = data.replace(delete, "");
                    Kid kid = botService.getKidByTabelId(data);
                    botService.deleteUserKid(chatID, kid);
                    sm.setText(kidDeleted);
                }
            }
        }
    }

    public void sendMessageToUser(User user, Kid kid, Integer passType, String time) {
        String type;

        if (passType == 1) {
            type = "Зафиксирован вход в школу";
        } else {
            type = "Зафиксирован выход из школы";
        }

        SendMessage sm = new SendMessage();
        sm.setChatId(String.valueOf(user.getID()));
        sm.setText(kid.getTabelID() + "\n" + kid.getFull_fio() + "\n" + type + "\n" + time);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboard getReplyKeyboardByUser() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(enterKidTabelID));
        row1.add(new KeyboardButton(deleteKid));
        keyboard.add(row1);

        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);

        return markup;
    }

    private void deleteMessage(Long chatID, Integer messageID) {
        DeleteMessage deleteMessage = new DeleteMessage();

        deleteMessage.setChatId(String.valueOf(chatID));
        deleteMessage.setMessageId(messageID);

        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void registerInDB(Update update, SendMessage sm) {

        Long chatID = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();
        String lastName = update.getMessage().getFrom().getLastName();

        if (botService.getUser(chatID) == null) {
            botService.registration(chatID, firstName, lastName);
        }
        sm.setChatId(String.valueOf(chatID));
        sm.setText(numberRequest);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(numberRequestInlineButton);
        keyboardButton.setRequestContact(true);
        row.add(keyboardButton);

        keyboard.add(row);

        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);

        sm.setReplyMarkup(markup);

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup oneButtonInlineMarkup(String text, String callBackData) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callBackData);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(inlineKeyboardButton);
        keyboard.add(row);

        markup.setKeyboard(keyboard);

        return markup;
    }
}
