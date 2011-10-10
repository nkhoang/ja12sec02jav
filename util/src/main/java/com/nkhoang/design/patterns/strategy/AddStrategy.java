package com.nkhoang.design.patterns.strategy;

public class AddStrategy implements CalculationStrategy{
    public int calculate(int a, int b) {
        return a + b;
    }
}
