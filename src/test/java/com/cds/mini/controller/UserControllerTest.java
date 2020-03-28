package com.cds.mini.controller;

import com.cds.mini.entity.User;
import com.cds.mini.model.Error;
import com.cds.mini.model.UserResponse;
import com.cds.mini.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void listUsers() throws Exception {
        MvcResult result = mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        UserResponse userResponse = objectMapper.readValue(content, UserResponse.class);
        List<com.cds.mini.model.User> results = userResponse.getResults();
        assertEquals(2, results.size());
        assertEquals(new com.cds.mini.model.User().userId("0002").name("John").salary(4000d), results.get(0));
        assertEquals(new com.cds.mini.model.User().userId("0003").name("Peter").salary(1000.50d), results.get(1));
    }

    @Test
    @Order(1)
    void listUsers_WithSalarySort() throws Exception {
        MvcResult result = mvc.perform(get("/users?sortName=salary&sortDir=ASC")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        UserResponse userResponse = objectMapper.readValue(content, UserResponse.class);
        List<com.cds.mini.model.User> results = userResponse.getResults();
        assertEquals(2, results.size());
        assertEquals(new com.cds.mini.model.User().userId("0003").name("Peter").salary(1000.50d), results.get(0));
        assertEquals(new com.cds.mini.model.User().userId("0002").name("John").salary(4000d), results.get(1));
    }

    @Test
    @Order(1)
    void listUsers_WithUserIdSort() throws Exception {
        MvcResult result = mvc.perform(get("/users?sortName=userId&sortDir=DESC")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        UserResponse userResponse = objectMapper.readValue(content, UserResponse.class);
        List<com.cds.mini.model.User> results = userResponse.getResults();
        assertEquals(2, results.size());
        assertEquals(new com.cds.mini.model.User().userId("0003").name("Peter").salary(1000.50d), results.get(0));
        assertEquals(new com.cds.mini.model.User().userId("0002").name("John").salary(4000d), results.get(1));
    }

    @Test
    @Order(2)
    void getUser() throws Exception {
        MvcResult result = mvc.perform(get("/users/0002")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        com.cds.mini.model.User user = objectMapper.readValue(content, com.cds.mini.model.User.class);
        assertEquals(new com.cds.mini.model.User().userId("0002").name("John").salary(4000d), user);
    }

    @Test
    @Order(3)
    void createUser() throws Exception {
        assertFalse(userRepository.findByUserId("0001").isPresent());

        com.cds.mini.model.User user = new com.cds.mini.model.User().userId("0001").name("Mary").salary(2000d);

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(userRepository.findByUserId("0001").isPresent());
    }

    @Test
    @Order(4)
    void createUser_AlreadyExist() throws Exception {
        com.cds.mini.model.User user = new com.cds.mini.model.User().userId("0002").name("Mary").salary(2000d);

        MvcResult result = mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        Error error = objectMapper.readValue(result.getResponse().getContentAsString(), Error.class);
        assertEquals(new Error().code("00001").message("User Id already exists"), error);
    }

    @Test
    @Order(5)
    void updateUser() throws Exception {
        User userEntity = userRepository.findByUserId("0002").orElse(null);
        assertNotNull(userEntity);
        assertEquals(4000d, userEntity.getSalary().doubleValue(), 0.0);

        com.cds.mini.model.User user = new com.cds.mini.model.User().userId("0002").name("John").salary(20000d);
        mvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        userEntity = userRepository.findByUserId("0002").orElse(null);
        assertNotNull(userEntity);
        assertEquals(20000d, userEntity.getSalary().doubleValue(), 0.0);
    }

    @Test
    @Order(6)
    void deleteUser() throws Exception {
        assertEquals(3, userRepository.findAll().size());

        mvc.perform(delete("/users/0002")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(2, userRepository.findAll().size());
    }
}