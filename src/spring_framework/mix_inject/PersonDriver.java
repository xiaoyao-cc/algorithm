package spring_framework.mix_inject;


import spring_framework.annotation.Autowired;
import spring_framework.annotation.Component;
import spring_framework.annotation.Value;

@Component
public class PersonDriver implements Driver{

    @Autowired(beanName = "electricCar")
    private Car car;// 核心依赖：车（构造函数注入）
    @Value("18")
    private Integer age;      // 次要配置：昵称（setter 注入）

    @Override
    public void drive() {
        System.out.println("年龄为:"+ age +"的司机" + "正在驾驶 "+ car.getDescription());
        car.start();
        System.out.println("出发啦！今天也要开心开车～");
    }
}
