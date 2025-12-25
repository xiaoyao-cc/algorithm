package main;

import spring_framework.circular_dependency.A;
import spring_framework.circular_dependency.B;
import spring_framework.circular_dependency.impl.AImpl;
import spring_framework.circular_dependency.impl.BImpl;
import spring_framework.container.MiniIocContainer;
import spring_framework.mix_inject.Driver;
import spring_framework.proxy.LogAspect;
import spring_framework.service.UserService;

public class SpringMain {
    public static void main(String[] args) throws Exception{
        test5();
    }

    private static void test5() throws Exception {
        MiniIocContainer container = MiniIocContainer.getInstance(); // 修改为你的路径
        Driver driver = (Driver) container.getBean("personDriver");
        driver.drive();
    }

    private static void test4() throws Exception{
        MiniIocContainer container = MiniIocContainer.getInstance();
        A a = (A)container.getBean("a");
        a.test();
        B b = (B)container.getBean("b");
        b.test();
    }

    private static void test3() throws Exception{
        MiniIocContainer container = MiniIocContainer.getInstance(); // 修改为你的路径
        Driver driver = (Driver) container.getBean("driver");
        driver.drive();
    }

    private static void test1() throws Exception {
        MiniIocContainer container = MiniIocContainer.getInstance();
        UserService service = (UserService) container.getBean("userService");
        service.doService();
    }
    private static void test2() throws Exception {
        MiniIocContainer container = MiniIocContainer.getInstance();
        AImpl a = (AImpl)container.getBean("a");
        a.test();
        BImpl b = (BImpl)container.getBean("b");
        b.test();
    }
}
