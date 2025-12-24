package spring_framework.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDefinition {
    public String id;
    public String className;
    public Map<String, String> properties = new HashMap<>();  // property name -> value or ref
    public Map<String, String> refs = new HashMap<>();// property name -> ref bean id
    public List<ConstructorArg> constructorArgs = new ArrayList<>();

    public static class ConstructorArg{
        public String ref;        // 指向的 bean id
        public Object value;      // 构造参数的值
        public boolean lazy = false;  // 默认不 lazy
    }
}
