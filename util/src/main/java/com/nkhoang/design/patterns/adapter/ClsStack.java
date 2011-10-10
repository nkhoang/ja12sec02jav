package com.nkhoang.design.patterns.adapter;

public class ClsStack implements Stack {
    public void push(String s) {
        System.out.println("Pushed element : " + s);
    }
}
