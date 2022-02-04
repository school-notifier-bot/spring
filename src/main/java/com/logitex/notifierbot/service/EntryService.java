package com.logitex.notifierbot.service;

import com.logitex.notifierbot.model.perco.Entry;
import com.logitex.notifierbot.repository.perco.EntryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
public class EntryService {
    private final EntryRepository repository;
    private List<Entry> entries;

    @Scheduled(fixedRate = 1000 * 60)
    public void updateData() {
        Date date = Date.valueOf(LocalDate.now());
        Time time = new Time((System.currentTimeMillis() / 1000) / 60 - 1);
        entries = repository.findByDateAndTimeAfter(date, time);
        log.info("Entries: {}", entries);
    }
}
