package ru.toropin.spx.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.toropin.spx.model.Customer;
import ru.toropin.spx.model.RubAccountException;
import ru.toropin.spx.service.PaymentContext;
import ru.toropin.spx.service.PaymentState;

@RestController
@RequestMapping("/api")
public final class MainController {
    //Зависимости
    private final PaymentState onlinePaymentState;
    private final PaymentState shopPaymentState;
    private final PaymentContext paymentContext;
    //Ответы по операциям
    private final String WRONG_SHOPTYPE_ERR_MSG = "Вы ввели неверное место покупки, повторите ввод - shop | online";
    private final String CURRENT_CUSTOMER_BALANCE_RUB = "Текущий баланс клиента - %.2f руб.";
    private final String CURRENT_CUSTOMER_BALANCE_BONUS = "Текущий баланс бонусов клиента - %.2f Б";

    /**
     * делаем внедрение зависимостей через конструктор, для более легкого тестирования
     *
     * @param onlinePaymentState логика для Online платежей
     * @param shopPaymentState   логика для Shop платежей
     * @param paymentContext     контекст состояний
     */
    public MainController(@Qualifier("onlinePaymentState") PaymentState onlinePaymentState,
                          @Qualifier("shopPaymentState") PaymentState shopPaymentState,
                          @Qualifier("paymentContext") PaymentContext paymentContext) {
        this.onlinePaymentState = onlinePaymentState;
        this.shopPaymentState = shopPaymentState;
        this.paymentContext = paymentContext;
    }

    /**
     * Произвести оплату
     *
     * @param shopType shop/online
     * @param amount   сумма оплаты
     * @return Возвращаемый ответ, обернутый в TransactionResult для красивого представления json
     */
    @GetMapping("/payment/{shopType}/{amount}")
    public ResponseEntity makePayment(@PathVariable String shopType, @PathVariable double amount) {
        String operationResult;
        // для перестраховки -  исп. IgnoreCase
        if (shopType.equalsIgnoreCase("online")) {
            paymentContext.setPaymentState(onlinePaymentState);
            operationResult = paymentContext.makePayment(amount);
        } else if (shopType.equalsIgnoreCase("shop")) {
            paymentContext.setPaymentState(shopPaymentState);
            operationResult = paymentContext.makePayment(amount);
        } else {
            return ResponseEntity.badRequest().body(new TransactionResult(false, null, WRONG_SHOPTYPE_ERR_MSG));
        }

        return ResponseEntity.ok(new TransactionResult(true, operationResult, null));
    }

    //Получить баланс рублевого счета клиента
    @GetMapping("/money")
    public TransactionResult getAmountOfCustomersMoney() {
        Customer customer = Customer.getInstance();
        return new TransactionResult(true, String.format(CURRENT_CUSTOMER_BALANCE_RUB, customer.getRubAccount()), null);
    }

    //Получить баланс бонусного счета клиента
    @GetMapping("/bankAccountOfEMoney")
    public TransactionResult getAmountOfCustomersBonuses() {
        Customer customer = Customer.getInstance();
        return new TransactionResult(true, String.format(CURRENT_CUSTOMER_BALANCE_BONUS, customer.getBonusAccount()), null);
    }

    //Обработка ошибки некорректного ввода параметра - "amount"
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TransactionResult handleWrongNumberFormat() {
        return new TransactionResult(false, null, "Ваш ввод некорректен, повторите попытку");
    }

    //Обработка ошибок входного параметра "amount" > "0", остатка ДС на счету меньше суммы оплаты.
    @ExceptionHandler(RubAccountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TransactionResult handleBusinessLogicError(RuntimeException e) {
        return new TransactionResult(false, null, e.getMessage());
    }

}

