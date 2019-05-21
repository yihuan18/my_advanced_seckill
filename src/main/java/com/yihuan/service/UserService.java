package com.yihuan.service;

import com.yihuan.dao.UserDao;
import com.yihuan.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User getById(int id){
        return userDao.queryById(id);
    }

    @Transactional
    public boolean testTx(){
        User user1 = new User();
        user1.setId(2);
        user1.setName("choco");
        userDao.insert(user1);

        User user2 = new User();
        user2.setId(1);
        user2.setName("yihuan again");
        userDao.insert(user2);

        return true;
    }
}
