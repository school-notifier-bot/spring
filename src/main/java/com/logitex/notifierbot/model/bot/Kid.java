package com.logitex.notifierbot.model.bot;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "kid")
@Data
public class Kid {
    @Id
    @Column(name = "staff_id")
    private Long ID;

    @Column(name = "full_fio")
    private String full_fio;

    @Column(name = "tabel_id")
    private String tabelID;
}
