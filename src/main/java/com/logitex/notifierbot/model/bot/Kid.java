package com.logitex.notifierbot.model.bot;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "kid")
@Data
public class Kid {
    @Id
    @Column(name = "id")
    private Long ID;
    @Column(name = "name")
    private String name;
    @Column(name = "tabel_id")
    private Long tabelID;
    @Column(name = "staff_id")
    private Long staffID;

}
