package spring_framework.container;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import spring_framework.bean.BeanDefinition;
import spring_framework.proxy.Aspect;
import spring_framework.proxy.LazyInvocationHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class MiniIocContainer {
    //饿汉式创建单例
    private static final MiniIocContainer INSTANCE;
    static {
        try {
            INSTANCE = new MiniIocContainer("/mix-beans.xml");  // 你的 XML 路径
        } catch (Exception e) {
            throw new RuntimeException("创建 MiniIocContainer 单例失败", e);
        }
    }
    // 原型池
    private final Map<String, BeanDefinition> beanDefs = new HashMap<>();
    // 成品池
    private final Map<String, Object> singletonBeans = new HashMap<>();
    // 正在创建中的 beanId
    private final Set<String> creatingBeans = new HashSet<>();
    // 存放提前暴露的原始对象
    private final Map<String, Object> earlyBeans = new HashMap<>();
    // 代理列表
    private final List<Aspect> aspects = new ArrayList<>();

    // 公共静态方法获取唯一实例
    public static MiniIocContainer getInstance() {
        return INSTANCE;
    }
    // 新增公共方法，让用户手动注册切面
    public void registerAspect(Aspect aspect) {
        aspects.add(aspect);
    }

    private MiniIocContainer(String xmlPath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document doc = documentBuilder.parse(MiniIocContainer.class.getResourceAsStream(xmlPath));

        Element root = doc.getDocumentElement();
        NodeList beans = root.getElementsByTagName("bean");
        //解析bean
        for (int i = 0; i < beans.getLength(); i++) {
            Element bean = (Element) beans.item(i);
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.id = bean.getAttribute("id");
            beanDefinition.className = bean.getAttribute("class");
            //解析bean内的property
            NodeList properties = bean.getElementsByTagName("property");
            for (int j = 0; j < properties.getLength(); j++) {
                Element property = (Element) properties.item(j);
                String name = property.getAttribute("name");
                if (property.hasAttribute("value")) {
                    beanDefinition.properties.put(name, property.getAttribute("value"));
                } else if (property.hasAttribute("ref")) {
                    beanDefinition.refs.put(name, property.getAttribute("ref"));
                }
            }
            //解析构造函数
            NodeList constructorArgs = bean.getElementsByTagName("constructor-arg");
            for (int j = 0; j < constructorArgs.getLength(); j++) {
                Element constructorArgElement = (Element) constructorArgs.item(j);
                BeanDefinition.ConstructorArg constructorArg = new BeanDefinition.ConstructorArg();
                if (constructorArgElement.hasAttribute("ref")) {
                    constructorArg.ref = constructorArgElement.getAttribute("ref");
                }
                if (constructorArgElement.hasAttribute("value")) {
                    constructorArg.value = constructorArgElement.getAttribute("value");
                }
                if (constructorArgElement.hasAttribute("ref") && constructorArgElement.hasAttribute("value")) {
                    throw new Exception("constructor-arg标签的ref和value属性不能同时存在");
                }
                if (!constructorArgElement.hasAttribute("ref") && !constructorArgElement.hasAttribute("value")) {
                    throw new Exception("constructor-arg标签必须有ref或者value属性");
                }
                if (constructorArgElement.hasAttribute("lazy")) {
                    if (constructorArgElement.getAttribute("lazy").equals("true")) {
                        constructorArg.lazy = true;
                    } else {
                        throw new Exception("lazy属性只能为true/false");
                    }
                }
                beanDefinition.constructorArgs.add(constructorArg);
            }
            //保存到原型池
            beanDefs.put(beanDefinition.id, beanDefinition);
        }
    }

    public Object getBean(String id) throws Exception {
        // 如果成品池里已经有完整的了，直接返回
        if (singletonBeans.containsKey(id)) {
            return singletonBeans.get(id);
        }
        // 检查这个beanId是否正在创建中,如果有直接返回半成品,防止依赖循环
        if (creatingBeans.contains(id)) {
            return earlyBeans.get(id);
        }
        // 开始正式创建bean
        // 标记正在创建
        creatingBeans.add(id);
        BeanDefinition bd = beanDefs.get(id);
        if (bd == null) {
            throw new Exception("没有id为:" + id + "的bean存在于容器内.......");
        }
        //反射创建实例
        Class<?> clazz = Class.forName(bd.className);
        Object instance = null;
        if (!bd.constructorArgs.isEmpty()) {
            Object[] args = new Object[bd.constructorArgs.size()];
            Constructor<?>[] constructors = clazz.getConstructors();
            Constructor<?> rightConstructor = null;
            //----------------构造器匹配for开始-------------------
            for (Constructor<?> constructor : constructors) {
                boolean isMatch = true;
                //先按照参数数量匹配
                if (constructor.getParameterCount() != bd.constructorArgs.size()) {
                    continue;
                }
                //尽管参数数量匹配，但是参数类型甚至参数的顺序也会导致匹配失败
                Class<?>[] types = constructor.getParameterTypes();
                for (int i = 0; i < types.length; i++) {
                    //接下来检查参数类型是否匹配,参数顺序目前先按照xml配置顺序
                    String ref = bd.constructorArgs.get(i).ref;
                    if (ref != null) {
                        boolean lazy = bd.constructorArgs.get(i).lazy;
                        if(lazy){
                            //懒加载解决循环依赖
                           args[i] = createProxy(ref, types[i],this);
                        } else {
                            Object bean = getBean(ref);
                            if (!types[i].isAssignableFrom(bean.getClass())) {
                                isMatch = false;
                                break;
                            }
                            args[i] = bean;
                        }
                    }
                    Object value = bd.constructorArgs.get(i).value;
                    if (value != null) {
                        //类型转换
                        Object parm = null;
                        if (types[i].equals(int.class) || types[i].equals(Integer.class)) {
                            try {
                                parm = Integer.parseInt((String) value);
                                args[i] = parm;
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("字符串:" + value + "不能转换为int类型");
                            }
                        } else if (types[i].equals(double.class) || types[i].equals(Double.class)) {
                            try {
                                parm = Double.parseDouble((String) value);
                                args[i] = parm;
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("字符串:" + value + "不能转换为double类型");
                            }
                        } else if (types[i].equals(long.class) || types[i].equals(Long.class)) {
                            try {
                                parm = Long.parseLong((String) value);
                                args[i] = parm;
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("字符串:" + value + "不能转换为long类型");
                            }
                        } else if (types[i].equals(boolean.class) || types[i].equals(Boolean.class)) {
                            try {
                                parm = Boolean.parseBoolean((String) value);
                                args[i] = parm;
                            } catch (Exception e) {
                                throw new RuntimeException("字符串:" + value + "不能转换为boolean类型");
                            }
                        } else {
                            //走这里应该是String类型
                            args[i] = value;
                        }
                    }
                }
                if (isMatch) {
                    rightConstructor = constructor;
                }
            }
            //----------------构造器匹配for结束-------------------
            if (rightConstructor == null) {
                throw new Exception(clazz.getName() + "没有找到期望的构造器");
            }
            instance = rightConstructor.newInstance(args);
            //提前暴露半成品
            earlyBeans.put(id, instance);
        } else {
            instance = clazz.newInstance();
            //提前暴露半成品
            earlyBeans.put(id, instance);
        }
        // 注入属性（value 和 ref）
        for (Map.Entry<String, String> entry : bd.properties.entrySet()) {
            String setter = "set" + entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
            Method method = clazz.getMethod(setter, String.class);
            method.invoke(instance, entry.getValue());
        }
        for (Map.Entry<String, String> entry : bd.refs.entrySet()) {
            String setter = "set" + entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
            Object refBean = getBean(entry.getValue());  // 递归注入依赖
            Class<?> paramType;
            //检查引用的bean是否实现了接口
            if (refBean.getClass().getInterfaces().length > 0) {
                paramType = refBean.getClass().getInterfaces()[0];
            } else {
                paramType = refBean.getClass();
            }
            Method method = clazz.getMethod(setter, paramType);
            method.invoke(instance, refBean);
        }
        singletonBeans.put(id, instance);
        //清理环节
        creatingBeans.remove(id);
        earlyBeans.remove(id);
        //判断是否需要返回代理类
        Object current = instance;
        for (Aspect aspect : aspects) {
            if (aspect.matches(current.getClass())) {
                InvocationHandler handler = aspect.getHandler(current);
                current = Proxy.newProxyInstance(current.getClass().getClassLoader(), current.getClass().getInterfaces(),handler);
            }
        }
        return current;
    }

    private Object createProxy(String beanId, Class<?> type,MiniIocContainer container) throws Exception {
        return Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type}, new LazyInvocationHandler(container, beanId));
    }
}
