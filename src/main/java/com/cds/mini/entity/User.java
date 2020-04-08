package com.cds.mini.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @NotBlank
    @Column(name = "UserId", nullable = false, unique = true, length = 100)
    private String userId;

    @NotEmpty(message = "Name cannot be null or empty")
    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Salary", nullable = false)
    @NotNull
    @Min(value = 0, message = "Salary must be at least {value}")
    @Max(value = 1000000, message = "Salary cannot be greater than {value}")
    private BigDecimal salary;

    @Column(name = "CreatedDate", updatable = false, nullable = false)
    private Timestamp createdDate;

    @Column(name = "CreatedBy", updatable = false, nullable = false)
    private int createdBy;

    @Column(name = "UpdatedDate", nullable = false)
    private Timestamp updatedDate;

    @Column(name = "UpdatedBy", nullable = false)
    private int updatedBy;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Account> accounts = new ArrayList<>();

}
