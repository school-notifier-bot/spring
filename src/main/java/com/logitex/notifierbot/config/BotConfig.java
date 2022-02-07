package com.logitex.notifierbot.config;

import org.springframework.beans.factory.annotation.Value;

public class BotConfig {
    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

}
