package com.logitex.notifierbot.model.bot;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user_kid")
@Data
public class UserKid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long ID;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "kid_id")
    private Long kidID;
}
