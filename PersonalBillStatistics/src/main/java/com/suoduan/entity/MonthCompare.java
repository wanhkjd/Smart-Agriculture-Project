package com.suoduan.entity;

import java.math.BigDecimal;

public class MonthCompare {
    private String month;
    private BigDecimal income;
    private BigDecimal expense;

    public MonthCompare() {}

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }
    public BigDecimal getExpense() { return expense; }
    public void setExpense(BigDecimal expense) { this.expense = expense; }
}
