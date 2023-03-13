package ru.toropin.spx.model;

import java.util.Objects;

/**
 * Для реализации задачи выбран паттерн Singleton, тк по условиям ТЗ клиент всегда один
 * В класс добавлены методы:
 * canPayInRub - Проверка остатка ДС на РУБ счете клиента, для совершения платежа
 * payInRub - списание ДС с РУБ счета клиента
 * addBonus - Добавление Бонусов на бонусный счет клиента
 */
public final class Customer {
    //Был выбран тип данных double для более читаемого кода в тестовом задании.
    //Понимаю, что в реальном проекте необходимо использовать целочисленные типы, во избежание потери точности, возникающие при работе с плавающей точкой.
    private static final double INITIAL_RUB_ON_ACCOUNT = 5000;
    private static final double INITIAL_BONUS_ON_ACCOUNT = 0;

    private double rubAccount;
    private double bonusAccount;
    private static Customer instance;

    public Customer() {
        this.rubAccount = INITIAL_RUB_ON_ACCOUNT;
        this.bonusAccount = INITIAL_BONUS_ON_ACCOUNT;
    }

    public static Customer getInstance() {
        if (instance == null) {
            instance = new Customer();
        }
        return instance;
    }

    public double getRubAccount() {
        return rubAccount;
    }

    public double getBonusAccount() {
        return bonusAccount;
    }

    @Override
    public String toString() {
        return String.format("Client{rubAccount=%s, bonusAccount=%s}", rubAccount, bonusAccount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(rubAccount, customer.rubAccount) && Objects.equals(bonusAccount, customer.bonusAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rubAccount, bonusAccount);
    }

    public boolean canPayInRub(double amountOfPayment) {
        return this.rubAccount >= amountOfPayment;
    }

    public void payInRub(double amountOfPayment) {
        this.rubAccount -= amountOfPayment;
    }

    public void addBonus(double bonusesToAdd) {
        this.bonusAccount += bonusesToAdd;
    }
}
