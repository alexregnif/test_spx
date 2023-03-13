package ru.toropin.spx.service;

import org.springframework.stereotype.Service;

//Контекст состояний. Устанавливает состояние в зависимости от входного параметра и вызывает метод установленного состояния.
@Service
public class PaymentContext implements PaymentState {

    private PaymentState paymentState;

    public void setPaymentState(PaymentState paymentState) {
        this.paymentState = paymentState;
    }

    @Override
    public String makePayment(double amount) {
        return this.paymentState.makePayment(amount);
    }

}
