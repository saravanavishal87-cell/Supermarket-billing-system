package com.supermarket.payment;

/**
 * Concrete Cash Payment class demonstrating Polymorphism.
 */
public class CashPayment extends AbstractPayment {
    private double cashTendered;
    private double changeReturned;

    public CashPayment() {}

    public CashPayment(double cashTendered) {
        this.cashTendered = cashTendered;
    }

    @Override
    public String getModeName() {
        return "CASH";
    }

    @Override
    public boolean processPayment(double amount) {
        if (amount < 0) return false;
        long amountCents = Math.round(amount * 100.0);
        long cashCents = Math.round(cashTendered * 100.0);
        if (cashCents > 0 && cashCents < amountCents) {
            return false;
        }
        if (cashCents >= amountCents) {
            this.changeReturned = (cashCents - amountCents) / 100.0;
        } else {
            this.changeReturned = 0.0;
        }
        return true;
    }

    @Override
    public String getPaymentDetails() {
        if (cashTendered > 0) {
            return String.format("Cash Received: Rs. %.2f | Change Returned: Rs. %.2f", cashTendered, changeReturned);
        }
        return "Paid via Cash";
    }

    public double getCashTendered() { return cashTendered; }
    public void setCashTendered(double cashTendered) { this.cashTendered = cashTendered; }

    public double getChangeReturned() { return changeReturned; }
}
