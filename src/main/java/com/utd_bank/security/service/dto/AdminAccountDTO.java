package com.utd_bank.security.service.dto;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.utd_bank.domain.Account;
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
public class AdminAccountDTO {

    private Long id;

    private Long accountNo;

    private Long userId;

    @Size(max = 250, message = "Size exceeded")
    @NotNull(message = "Please write description")
    private String description;

    @NotNull(message = "Please enter balance")
    private Double balance;

    @NotNull(message = "Please enter currency code")
    private CurrencyCode currencyCode;

    @NotNull(message = "Please choose account type")
    private AccountType accountType;

    @NotNull(message = "Please choose account status type")
    private AccountStatusType accountStatusType;

    private Timestamp createdDate;

    private Timestamp closedDate;

    public AdminAccountDTO(Account account) {
        this.id = account.getId();
        this.accountNo = account.getAccountNo().getId();
        this.userId = account.getUserId().getId();
        this.description = account.getDescription();
        this.balance = account.getBalance();
        this.currencyCode = account.getCurrencyCode();
        this.accountType = account.getAccountType();
        this.accountStatusType = account.getAccountStatusType();
        this.createdDate = account.getAccModInfId().getCreatedDate();
    }
}
