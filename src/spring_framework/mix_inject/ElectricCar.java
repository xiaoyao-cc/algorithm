package spring_framework.mix_inject;

public class ElectricCar implements Car{
    @Override
    public void start() {
        System.out.println("电动车悄无声息地启动了～");
    }

    @Override
    public String getDescription() {
        return "一辆环保的电动车";
    }
}
