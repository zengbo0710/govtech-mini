package com.cds.mini.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "Account")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "AccountNumber")
    private String accountNumber;
}
