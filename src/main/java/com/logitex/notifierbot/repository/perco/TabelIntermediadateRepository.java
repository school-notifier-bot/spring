package com.logitex.notifierbot.repository.perco;

import com.logitex.notifierbot.model.perco.TabelIntermediadate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface TabelIntermediadateRepository extends JpaRepository<TabelIntermediadate, Long> {
    List<TabelIntermediadate> findByDateAndTimeAfter(Date date, Time time);
}
