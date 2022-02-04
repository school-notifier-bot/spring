package com.logitex.notifierbot.repository.perco;

import com.logitex.notifierbot.model.perco.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByDateAndTimeAfter(Date date, Time time);
}
