package com.nkhoang.design.patterns.strategy;

public class SubstractStrategy implements CalculationStrategy{
    public int calculate(int a, int b) {
        return a - b;
    }
}
