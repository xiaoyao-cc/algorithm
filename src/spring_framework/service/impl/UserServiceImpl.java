package spring_framework.service.impl;

import spring_framework.dao.UserDao;
import spring_framework.dao.impl.UserDaoImpl;
import spring_framework.service.UserService;

public class UserServiceImpl implements UserService {
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void doService() {
        userDao.query();
        System.out.println("服务执行...");
    }
}
