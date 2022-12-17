package com.projects.braintreespringboot.dto;

import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.Transaction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionInfo {
    private boolean isSuccess;
    private Transaction transaction;
    private CreditCard creditCard;
    private Customer customer;
}
