package com.cds.mini.controller;

import com.cds.mini.api.UsersApi;
import com.cds.mini.model.User;
import com.cds.mini.model.UserResponse;
import com.cds.mini.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController implements UsersApi {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserResponse> listUsers(@Valid String sortName, @Valid String sortDir) throws Exception {
        List<User> users = userService.getUsersWithSalaryRange(sortName, sortDir);
        UserResponse response = new UserResponse()
                .results(users);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<User> getUser(String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @Override
    public ResponseEntity<Void> createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
