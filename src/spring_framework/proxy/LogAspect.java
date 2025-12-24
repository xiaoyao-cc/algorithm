package spring_framework.proxy;

import spring_framework.mix_inject.Driver;

import java.lang.reflect.InvocationHandler;

public class LogAspect implements Aspect{
    @Override
    public boolean matches(Class<?> beanClass) {
        // 主动选择：所有实现了 Driver 接口的 Bean
        return Driver.class.isAssignableFrom(beanClass);
    }

    @Override
    public InvocationHandler getHandler(Object target) {
        return new LogInvocationHandler(target);
    }
}
