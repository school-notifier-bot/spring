package com.logitex.notifierbot.repository.perco;

import com.logitex.notifierbot.model.perco.TabelIntermediadate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface TabelIntermediadateRepository extends JpaRepository<TabelIntermediadate, Long> {
    @Query("SELECT t FROM TabelIntermediadate t WHERE t.date > :date or (t.date >= :date and t.time >= :time)")
    List<TabelIntermediadate> findAllByDateAndTimeAfter(
            @Param("date") Date date,
            @Param("time") Time time
    );
}
