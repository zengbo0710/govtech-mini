package com.cds.mini.utils;

import com.cds.mini.entity.User;

import java.math.BigDecimal;

public class EntityBuilder {
    public static User createUser(int id, String userId, String name, BigDecimal salary) {
        User user = new User();
        user.setId(id);
        user.setUserId(userId);
        user.setName(name);
        user.setSalary(salary);

        return user;
    }
}
