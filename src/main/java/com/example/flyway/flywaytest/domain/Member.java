package com.example.flyway.flywaytest.domain;

import javax.persistence.*;

@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_gen")
    @SequenceGenerator(name = "member_gen", sequenceName = "member_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "nickname")
    private String nickname;
}
