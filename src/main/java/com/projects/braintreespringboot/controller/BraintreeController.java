package com.projects.braintreespringboot.controller;

import com.braintreegateway.*;
import com.projects.braintreespringboot.BraintreeSpringBootApplication;
import com.projects.braintreespringboot.dto.TransactionInfo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Arrays;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/braintree")
public class BraintreeController {

    private final BraintreeGateway gateway = BraintreeSpringBootApplication.gateway;
//    Generate a client token
//    ClientTokenRequest clientTokenRequest = new ClientTokenRequest()
//            .customerId(aCustomerId);
//    // pass clientToken to your front-end
//    String clientToken = gateway.clientToken().generate(clientTokenRequest);

    private final Transaction.Status[] TRANSACTION_SUCCESS_STATUSES = new Transaction.Status[] {
            Transaction.Status.AUTHORIZED,
            Transaction.Status.AUTHORIZING,
            Transaction.Status.SETTLED,
            Transaction.Status.SETTLEMENT_CONFIRMED,
            Transaction.Status.SETTLEMENT_PENDING,
            Transaction.Status.SETTLING,
            Transaction.Status.SUBMITTED_FOR_SETTLEMENT
    };


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root(Model model) {

        return "redirect:checkouts";
    }

//    Generate a client token and Send a client token to your client
    @RequestMapping(value = "/checkouts", method = RequestMethod.GET)
    public String checkout(Model model) {
        String clientToken = gateway.clientToken().generate();
        model.addAttribute("clientToken", clientToken);

        return "checkouts/new";
    }

//    Receive a payment method nonce from your client
    @RequestMapping(value = "/checkouts", method = RequestMethod.POST)
    public String postForm(@RequestParam("amount") String amount, @RequestParam("payment_method_nonce") String nonce, Model model, final RedirectAttributes redirectAttributes) {
        BigDecimal decimalAmount;
        try {
            decimalAmount = new BigDecimal(amount);
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorDetails", "Error: 81503: Amount is an invalid format.");
            return "redirect:checkouts";
        }

        //Create a transaction
        TransactionRequest request = new TransactionRequest()
                .amount(decimalAmount)
                .paymentMethodNonce(nonce)
                .options().storeInVaultOnSuccess(true)
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();
            return "redirect:checkouts/" + transaction.getId();
        } else if (result.getTransaction() != null) {
            Transaction transaction = result.getTransaction();
            return "redirect:checkouts/" + transaction.getId();
        } else {
            StringBuilder errorString = new StringBuilder();
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
                errorString.append("Error: ").append(error.getCode()).append(": ").append(error.getMessage()).append("\n");
            }
            redirectAttributes.addFlashAttribute("errorDetails", errorString.toString());
            return "redirect:checkouts";
        }

//        @RequestParam("email") String email,
//        String customerEmail;
//        customerEmail = new String(email);
//        CustomerRequest requestCus = new CustomerRequest()
//                .email(customerEmail)

    }

    //Get transaction
    @RequestMapping(value = "/checkouts/{transactionId}", method = RequestMethod.GET)
    public TransactionInfo getTransaction(@PathVariable String transactionId, TransactionInfo transactionInfo) {
        System.out.println("transaction");
        Transaction transaction;
        CreditCard creditCard;
        Customer customer;

        try {
            transaction = gateway.transaction().find(transactionId);
            creditCard = transaction.getCreditCard();
            customer = transaction.getCustomer();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return null;
        }

        transactionInfo.setSuccess(Arrays.asList(TRANSACTION_SUCCESS_STATUSES).contains(transaction.getStatus()));
        transactionInfo.setTransaction(transaction);
        transactionInfo.setCreditCard(creditCard);
        transactionInfo.setCustomer(customer);
//        model.addAttribute("isSuccess", Arrays.asList(TRANSACTION_SUCCESS_STATUSES).contains(transaction.getStatus()));
//        model.addAttribute("transaction", transaction);
//        model.addAttribute("creditCard", creditCard);
//        model.addAttribute("customer", customer);

        return transactionInfo;
    }


}
