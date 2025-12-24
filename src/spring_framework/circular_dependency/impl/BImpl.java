package spring_framework.circular_dependency.impl;

import spring_framework.circular_dependency.A;
import spring_framework.circular_dependency.B;

public class BImpl implements B {
    private A a;
    public BImpl(A a) {
        this.a = a;
    }
    public void test() {
        System.out.println("B 已创建，持有的 A 是: " + a);
    }
}
