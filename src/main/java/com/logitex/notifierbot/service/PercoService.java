package com.logitex.notifierbot.service;

import com.logitex.notifierbot.model.perco.Staff;
import com.logitex.notifierbot.model.perco.TabelIntermediadate;
import com.logitex.notifierbot.repository.perco.StaffRepository;
import com.logitex.notifierbot.repository.perco.TabelIntermediadateRepository;
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
@Slf4j
@RequiredArgsConstructor
public class PercoService {
    private final TabelIntermediadateRepository tabelIntermediadateRepository;
    private final StaffRepository staffRepository;
    @Getter
    private List<TabelIntermediadate> entries;

    @Scheduled(fixedRate = 1000 * 60)
    public void updateData() {
        Date date = Date.valueOf(LocalDate.now());
        Time time = new Time(System.currentTimeMillis() - 1000 * 60);
        entries = tabelIntermediadateRepository.findByDateAndTimeAfter(date, time);
        log.info("Entries: {}", entries);
    }

    public Staff getStaffByTabelId(Long tabelId) {
        return staffRepository.findByTabelID(tabelId).orElse(null);
    }
}
