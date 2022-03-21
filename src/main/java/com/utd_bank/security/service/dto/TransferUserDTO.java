package com.utd_bank.security.service.dto;


import java.sql.Timestamp;

import com.utd_bank.domain.Transfer;
import com.utd_bank.domain.enumeration.CurrencyCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferUserDTO {

    private Long id;

    private Long fromAccountId;

    private Long toAccountId;

    private Double transactionAmount;

    private CurrencyCode currencyCode;

    private Timestamp transactionDate;

    private String description;

    public TransferUserDTO(Transfer transfer) {
        this.id = transfer.getId();
        this.fromAccountId = transfer.getFromAccountId().getAccountNo().getId();
        this.toAccountId = transfer.getToAccountId();
        this.transactionAmount = transfer.getTransactionAmount();
        this.currencyCode = transfer.getCurrencyCode();
        this.transactionDate = transfer.getTransactionDate();
        this.description = transfer.getDescription();
    }
}
