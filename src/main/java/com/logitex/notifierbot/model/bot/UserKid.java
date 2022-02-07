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

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "kid_id")
    private Kid kid;
}
