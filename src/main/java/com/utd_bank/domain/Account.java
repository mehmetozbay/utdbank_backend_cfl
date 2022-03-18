package com.utd_bank.domain;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.utd_bank.domain.enumeration.AccountStatusType;
import com.utd_bank.domain.enumeration.AccountType;
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
@Table(name = "tbl_accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "account_no", referencedColumnName = "id", unique = true, nullable = false)
    private AccountNumber accountNo;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @Size(max = 250, message = "Size exceeded")
    @NotNull(message = "Please write description")
    @Column(nullable = false, length = 250)
    private String description;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please enter currency code")
    @Column(nullable = false)
    private CurrencyCode currencyCode;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please choose account type")
    @Column(nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatusType accountStatusType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "acc_modify_inf_id", referencedColumnName = "id")
    private AccountModifyInformation accModInfId;
}
