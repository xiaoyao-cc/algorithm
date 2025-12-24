package spring_framework.proxy;

import spring_framework.container.MiniIocContainer;

import java.lang.reflect.InvocationHandler;

public class LazyInvocationHandler implements InvocationHandler {
    private final MiniIocContainer container;
    private String targetBeanId;
    private Object realBean;

    public LazyInvocationHandler(MiniIocContainer container, String targetBeanId){
        this.container = container;
        this.targetBeanId = targetBeanId;
    }
    @Override
    public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
        if(realBean == null){
            System.out.println("懒加载触发：正在创建 bean " + targetBeanId);
            realBean = container.getBean(targetBeanId);
        }
        return method.invoke(realBean, args);
    }
}
