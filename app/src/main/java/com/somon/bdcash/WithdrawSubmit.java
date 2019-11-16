package com.somon.bdcash;

public class WithdrawSubmit {

   private String withDrawDate;
   private String paymentMethod;
   private String requestNumber;
   private String Amount;

    public WithdrawSubmit(String withDrawDate, String paymentMethod, String requestNumber, String amount) {
        this.withDrawDate = withDrawDate;
        this.paymentMethod = paymentMethod;
        this.requestNumber = requestNumber;
        Amount = amount;
    }

    public WithdrawSubmit() {
    }

    public String getWithDrawDate() {
        return withDrawDate;
    }

    public void setWithDrawDate(String withDrawDate) {
        this.withDrawDate = withDrawDate;
    }


    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
