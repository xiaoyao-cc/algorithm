package spring_framework.proxy;

import spring_framework.mix_inject.Driver;

import java.lang.reflect.InvocationHandler;

public class LogAspect implements Aspect{
    @Override
    public boolean matches(Class<?> beanClass) {
        // 目前这种写法只支持对Driver接口实现类的代理
        return Driver.class.isAssignableFrom(beanClass);
    }

    @Override
    public InvocationHandler getHandler(Object target) {
        return new LogInvocationHandler(target);
    }
}
