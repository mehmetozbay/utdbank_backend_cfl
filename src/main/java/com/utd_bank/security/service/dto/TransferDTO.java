package com.utd_bank.security.service.dto;



import java.sql.Timestamp;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.utd_bank.domain.AccountNumber;
import com.utd_bank.domain.User;
import com.utd_bank.domain.enumeration.CurrencyCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {

    private Long id;

    @NotNull(message = "Please write correct receiver account id")
    private AccountNumber fromAccountId;

    @NotNull(message = "Please write correct sender account id")
    private AccountNumber toAccountId;

    private User userId;

    @NotNull(message = "Please specify transaction amount")
    @DecimalMin("10")
    private Double transactionAmount;

    private Double newBalance;

    @NotNull(message = "Please enter currency code")
    private CurrencyCode currencyCode;

    private Timestamp transactionDate;

    @NotNull(message = "Please write description")
    private String description;
}
