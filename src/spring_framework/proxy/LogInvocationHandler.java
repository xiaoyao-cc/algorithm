package spring_framework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogInvocationHandler implements InvocationHandler {
    Object target;
    public LogInvocationHandler(Object target) {
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            System.out.println("【LOG】开始执行 " + method.getName() + " on " + target.getClass().getSimpleName());
            long start = System.currentTimeMillis();
            Object result = method.invoke(target, args);
            long end = System.currentTimeMillis();
            System.out.println("【LOG】结束执行, 耗时"+ (end - start) + "ms");
            return result;
        }catch (Exception e){
            System.out.println("【LOG】结束异常");
            throw new Exception(e);
        }
    }
}
