package spring_framework.circular_dependency.impl;

import spring_framework.circular_dependency.A;
import spring_framework.circular_dependency.B;

public class AImpl implements A {
    private B b;
    public AImpl(B b){
        this.b = b;
    }
    public void test() {
        System.out.println("A 已创建，持有的 B 是: " + b);
    }
}
