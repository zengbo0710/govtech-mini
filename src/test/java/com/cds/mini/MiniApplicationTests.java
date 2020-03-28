package com.cds.mini;

import com.cds.mini.controller.UserController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MiniApplicationTests {

    @Autowired
    private UserController userController;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertNotNull(userController);
    }

}
