package com.logitex.notifierbot;

import com.logitex.notifierbot.repository.perco.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor
class NotifierBotApplicationTests {

    @Autowired
    private final EntryRepository repository;

    @Test
    void contextLoads() {
    }

}
