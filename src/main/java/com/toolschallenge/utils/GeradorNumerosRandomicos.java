package com.toolschallenge.utils;

import java.util.random.RandomGenerator;

public class GeradorNumerosRandomicos {

    private static final RandomGenerator generator = RandomGenerator.getDefault();

    public static long gerarLong() {
        return generator.nextLong(999999999+1) ;
    }

    public static int gerarInteger(int max) {
        return generator.nextInt(max);
    }
}
