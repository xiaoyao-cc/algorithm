package spring_framework.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {
    // 可选的 value 属性，用来自定义 Bean 名称
    // 如果不写，默认用类名首字母小写作为 Bean id
    String value() default "";
}
