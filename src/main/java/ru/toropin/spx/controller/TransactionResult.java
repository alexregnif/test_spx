package ru.toropin.spx.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
//Кастомный класс для ответов по операциям

//Аннотация для правильного отображения результатов со значением "null"
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TransactionResult {

    private boolean success;
    private String report;
    private String error;

    public TransactionResult(boolean success, String report, String error) {
        this.success = success;
        this.report = report;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
