package com.logitex.notifierbot;

import com.logitex.notifierbot.config.BotConfig;
import com.logitex.notifierbot.model.bot.Kid;
import com.logitex.notifierbot.model.bot.UserKid;
import com.logitex.notifierbot.model.perco.Staff;
import com.logitex.notifierbot.service.BotService;
import com.logitex.notifierbot.service.PercoService;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

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
    private String cancel = "Отменить";
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

                if (update.getMessage().getContact() != null) {
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
                }
                else if (update.getMessage().getReplyToMessage() != null) {
                    String message = update.getMessage().getText();
                    String replyMessage = update.getMessage().getReplyToMessage().getText();
                    Long tabelId = Long.parseLong(message);
                    if (replyMessage.equals(enterKidTabelIDResponse)) {
                        Kid kid = botService.getKidByTabelId(tabelId);
                        if (kid == null) {
                            Staff staff = percoService.getStaffByTabelId(tabelId);
                            if (staff != null) {
                                botService.insertKid(staff);
                            }
                        }

                        if (kid != null) {
                            sm.setText(found + "\n" + kid.getFull_fio());
                            botService.insertUserKid(chatID, kid.getID());
                        } else {
                            sm.setText(notFound);
                        }
                    }
                }
                else if (update.getMessage().getText() != null) {
                    String messageText = update.getMessage().getText();
                    if (messageText.equals(enterKidTabelID)) {
                        sm.setText(enterKidTabelIDResponse);
                        sm.setReplyMarkup(new ForceReplyKeyboard());
                        try {
                            sendApiMethod(sm);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (messageText.equals(deleteKid)) {
                        List<UserKid> userKids = botService.getUserKids(chatID);
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
                    } else if (messageText.equals("/start")) {
                        registerInDB(update);
                        return;
                    } else {
                        sm.setText("Неизвестная команда");
                    }
                }
            }
            else if (update.getCallbackQuery() != null) {
                chatID = update.getCallbackQuery().getMessage().getChat().getId();
                Integer messageID = update.getCallbackQuery().getMessage().getMessageId();
                if (update.getCallbackQuery().getData() != null) {
                    String data = update.getCallbackQuery().getData();
                    sm.setChatId(String.valueOf(chatID));

                    if (data.contains(delete)) {
                        deleteMessage(chatID, messageID);
                        data = data.replace(delete, "");
                        Kid kid = botService.getKidByTabelId(Long.parseLong(data));
                        botService.deleteUserKid(chatID, kid);
                        sm.setText(kidDeleted);
                    }
                }
            }
            sm.setReplyMarkup(getReplyKeyboardByUser(chatID));

            try {
                sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }
    }

    private ReplyKeyboard getReplyKeyboardByUser(Long chatID) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(enterKidTabelID);
        row1.add(keyboardButton);
        keyboard.add(row1);

        if (group.equals(parent)) {
            KeyboardRow row2 = new KeyboardRow();

            row2.add(new KeyboardButton().setText(deleteKid));

            keyboard.add(row2);
        }

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

    private void registerInDB(Update update) {
        SendMessage sm = new SendMessage();

        Long chatID = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();
        String lastName = update.getMessage().getFrom().getLastName();
        String phoneNumber = update.getMessage().getContact().getPhoneNumber();

        if (botService.getUser(chatID) == null) {
            botService.registration(chatID, firstName, lastName, phoneNumber);
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

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}
