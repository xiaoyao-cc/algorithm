package spring_framework.mix_inject;

public class PersonDriver implements Driver{
    private final Car car;        // 核心依赖：车（构造函数注入）
    private Integer age;      // 次要配置：昵称（setter 注入）

    public PersonDriver(Car car) {
        this.car = car;
    }

    public PersonDriver(Car car, Integer age) {
        this.car = car;
        this.age = age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public void drive() {
        System.out.println("年龄为:"+ age +"的司机" + "正在驾驶 "+ car.getDescription());
        car.start();
        System.out.println("出发啦！今天也要开心开车～");
    }
}
