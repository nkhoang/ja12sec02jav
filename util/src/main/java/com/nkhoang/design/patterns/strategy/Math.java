package com.nkhoang.design.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Math {
    private static Logger LOG = LoggerFactory.getLogger(Math.class.getCanonicalName());
    public int[] calculate(int a, int b, CalculationStrategy ... strategies) {
        int result[] = new int[strategies.length];
        int count = 0;
        for (CalculationStrategy strategy: strategies) {
            result[count++] = strategy.calculate(a, b);
        }

        return result;
    }

    public static void main(String args[]) {
        Math math = new Math();
        int[] result = math.calculate(10, 20, new AddStrategy(), new SubstractStrategy());

        for (int i : result) {
            LOG.info(i + "");
        }
    }
}
