package com.logitex.notifierbot.model.perco;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "TABEL_INTERMEDIADATE")
@Data
public class TabelIntermediadate {
    @Id
    @Column(name = "ID_TB_IN")
    private Long ID;

    @Column(name = "STAFF_ID")
    private Long staffID;

    @Column(name = "DATE_PASS")
    private Date date;

    @Column(name = "TIME_PASS")
    private Time time;

    @Column(name = "TYPE_PASS")
    private Integer type;
}
