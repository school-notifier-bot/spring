package com.logitex.notifierbot.model.bot;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_kid")
@Data
public class UserKid {
    @Id
    @Column(name = "id")
    private Long ID;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "kid_id")
    private Long kidID;
}
