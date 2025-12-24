package spring_framework.proxy;

import java.lang.reflect.InvocationHandler;

public interface Aspect {
    // 是否匹配这个 Bean（类级别匹配）
    boolean matches(Class<?> beanClass);

    // 返回一个 InvocationHandler 来增强目标对象
    InvocationHandler getHandler(Object target);
}
