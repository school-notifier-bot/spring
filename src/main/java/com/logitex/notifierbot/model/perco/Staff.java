package com.logitex.notifierbot.model.perco;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STAFF")
@Data
public class Staff {
    @Id
    @Column(name = "ID_STAFF")
    private Long ID;

    @Column(name = "FULL_FIO")
    private String fullFio;

    @Column(name = "TABEL_ID")
    private String tabelID;
}
