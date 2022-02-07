package com.logitex.notifierbot.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Getter
@NoArgsConstructor
public class BotConfig {
    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;
}
