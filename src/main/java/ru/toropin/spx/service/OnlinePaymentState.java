package ru.toropin.spx.service;

import ru.toropin.spx.model.Customer;
import org.springframework.stereotype.Service;
import ru.toropin.spx.model.RubAccountException;

//Реализация для входного параметра "online"
@Service
public final class OnlinePaymentState implements PaymentState {
    //Ответы по операциям
    private final String NOT_ENOUGH_MONEY_ERR = "На счету клиента не хватает ДС. Баланс клиента: %.2f руб. Сумма платежа: %.2f руб.";
    private final String REPORT_OPERATION_RESULT = "Потрачено  в Online %.2f руб. Баланс клиента: %.2f руб. Баланс бонусов: %.2f Б.";
    private final String AMOUNT_EQUALS_NULL_ERR = "Сумма платежа не должна быть 0 <=, вы ввели  %.2f . Повторите ввод";

    @Override
    public String makePayment(double amountOfPayment) {
        //проверка входного параметра "amount" на "0"
        if (amountOfPayment <= 0) {
            throw new RubAccountException(String.format(AMOUNT_EQUALS_NULL_ERR, amountOfPayment));
        }
        Customer customer = Customer.getInstance();

        //Операции по рублевому счету
        //Проверка на остаток ДС на РУБ счете клиента,для совершения платежа
        if (!customer.canPayInRub(amountOfPayment)) {
            throw new RubAccountException(String.format(NOT_ENOUGH_MONEY_ERR, customer.getRubAccount(), amountOfPayment));
        }
        customer.payInRub(amountOfPayment);
        //Операции по бонусному счету
        double bonusesToAdd = amountOfPayment * getBonusPercentage(amountOfPayment);
        customer.addBonus(bonusesToAdd);

        return String.format(REPORT_OPERATION_RESULT, amountOfPayment, customer.getRubAccount(), customer.getBonusAccount());
    }

    //Получение процентов бонуса вынесено в отдельный метод
    private static double getBonusPercentage(double amountOfPayment) {
        if (amountOfPayment < 20) {
            return 0.17;
            /**
             *  getComissionToBank(amountOfPayment);
             *  В ТЗ указано условие - "Покупки менее 20 рублей возвращаются комиссией 10% в Банк",
             *  которое необходимо уточнить у заказчика.
             *  Данный метод создан для учета этой комисси, когда данное условие ТЗ будет прояснено.
             */
        } else if (amountOfPayment <= 300) {
            return 0.17;
        } else {
            return 0.30;
        }
    }


}
