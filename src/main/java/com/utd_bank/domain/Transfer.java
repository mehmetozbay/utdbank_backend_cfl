package com.utd_bank.domain;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.utd_bank.domain.enumeration.CurrencyCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transfers")
public class Transfer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "from_account_id", referencedColumnName = "id")
    private Account fromAccountId;

    @NotNull(message = "Please write correct sender account id")
    private Long toAccountId;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @NotNull(message = "Please specify transaction amount")
    @DecimalMin("10")
    @Column(nullable = false)
    private Double transactionAmount;

    private Double newBalance;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please enter currency code")
    @Column(nullable = false)
    private CurrencyCode currencyCode;

    private Timestamp transactionDate;

    @NotNull(message = "Please write description")
    @Column(nullable = false)
    private String description;

    public Transfer(Account fromAccountId, Long toAccountId, User userId, Double transactionAmount, Double newBalance,
                    CurrencyCode currencyCode, Timestamp transactionDate, String description) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.userId = userId;
        this.transactionAmount = transactionAmount;
        this.newBalance = newBalance;
        this.currencyCode = currencyCode;
        this.transactionDate = transactionDate;
        this.description = description;
    }
}
