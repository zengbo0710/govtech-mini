package com.cds.mini.service;

import com.cds.mini.entity.User;
import com.cds.mini.error.Errors;
import com.cds.mini.error.ServiceException;
import com.cds.mini.repository.UserRepository;
import com.cds.mini.utils.EntityBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator validator;

    private UserService userService;
    private User user1 = EntityBuilder.createUser(1, "0001", "Name 1", BigDecimal.valueOf(1000));
    ;
    private User user2 = EntityBuilder.createUser(2, "0002", "Name 2", BigDecimal.valueOf(2000));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(userRepository, validator);
    }

    @Test
    void getUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<com.cds.mini.model.User> users = userService.getUsers();

        List<com.cds.mini.model.User> expectedUsers = Arrays.asList(UserService.convertToDTO(user1), UserService.convertToDTO(user2));
        assertEquals(expectedUsers, users);
        verify(userRepository).findAll();
    }

    @Test
    void getUsersWithSalaryRange() {
        when(userRepository.findBySalaryBetween(any(), any())).thenReturn(Arrays.asList(user1, user2));

        List<com.cds.mini.model.User> users = userService.getUsersWithSalaryRange(null, null);

        List<com.cds.mini.model.User> expectedUsers = Arrays.asList(UserService.convertToDTO(user1), UserService.convertToDTO(user2));
        assertEquals(expectedUsers, users);
        verify(userRepository).findBySalaryBetween(BigDecimal.valueOf(0), BigDecimal.valueOf(4000));
    }

    @Test
    void getUsersWithSalaryRange_WithSort() {
        when(userRepository.findBySalaryBetween(any(), any(), any())).thenReturn(Arrays.asList(user1, user2));

        List<com.cds.mini.model.User> users = userService.getUsersWithSalaryRange("salary", "asc");

        List<com.cds.mini.model.User> expectedUsers = Arrays.asList(UserService.convertToDTO(user1), UserService.convertToDTO(user2));
        assertEquals(expectedUsers, users);
        verify(userRepository).findBySalaryBetween(BigDecimal.valueOf(0), BigDecimal.valueOf(4000), Sort.by(Sort.Direction.ASC, "salary"));
    }

    @Test
    void getUser() {
        when(userRepository.findByUserId(any())).thenReturn(Optional.of(user1));

        com.cds.mini.model.User user = userService.getUser("0001");

        com.cds.mini.model.User expectedUser = UserService.convertToDTO(user1);
        assertEquals(expectedUser, user);
        verify(userRepository).findByUserId("0001");
    }

    @Test
    void getUser_NotFound() {
        when(userRepository.findByUserId(any())).thenReturn(Optional.empty());

        com.cds.mini.model.User user = userService.getUser("0001");

        assertNull(user);
        verify(userRepository).findByUserId("0001");
    }

    @Test
    void createUser() {
        com.cds.mini.model.User user = new com.cds.mini.model.User().userId("0001").name("John").salary(1000.50d);
        when(validator.supports(any())).thenReturn(true);

        userService.createUser(user);

        verify(validator).supports(User.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User userEntity = userArgumentCaptor.getValue();
        assertEquals("0001", userEntity.getUserId());
        assertEquals("John", userEntity.getName());
        assertEquals(BigDecimal.valueOf(1000.50), userEntity.getSalary());
        assertEquals(1, userEntity.getCreatedBy());
        assertNotNull(userEntity.getCreatedDate());
        assertEquals(1, userEntity.getUpdatedBy());
        assertNotNull(userEntity.getUpdatedDate());
    }

    @Test
    void createUser_AlreadyExists() {
        com.cds.mini.model.User user = new com.cds.mini.model.User().userId("0001").name("John").salary(1000.50d);
        when(userRepository.findByUserId("0001")).thenReturn(Optional.of(user1));

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.createUser(user));

        assertEquals(Errors.USER_ID_EXIST, exception.getError());
        verify(validator, times(0)).supports(any());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void createUsers() {
        userService.createUsers(Arrays.asList(user1, user2));

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(userArgumentCaptor.capture());
        List<User> calledUsers = userArgumentCaptor.getAllValues();
        assertEquals(user1, calledUsers.get(0));
        assertEquals(user2, calledUsers.get(1));
    }

    @Test
    void updateUser() {
        com.cds.mini.model.User user = new com.cds.mini.model.User().userId("0001").name("John").salary(1500.50d);
        when(userRepository.findByUserId("0001")).thenReturn(Optional.of(user1));
        when(validator.supports(any())).thenReturn(true);

        userService.updateUser(user);

        verify(userRepository).findByUserId("0001");
        verify(validator).supports(User.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User userEntity = userArgumentCaptor.getValue();
        assertEquals(BigDecimal.valueOf(1500.50), userEntity.getSalary());
    }

    @Test
    void updateUser_NotExist() {
        com.cds.mini.model.User user = new com.cds.mini.model.User().userId("0001").name("John").salary(1500.50d);
        when(userRepository.findByUserId("0001")).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.updateUser(user));

        assertEquals(Errors.USER_ID_NOT_EXIST, exception.getError());
        verify(userRepository).findByUserId("0001");
        verify(validator, times(0)).supports(any());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void updateUser_InvalidData() {
        com.cds.mini.model.User user = new com.cds.mini.model.User().userId("0001").name("John").salary(1500000.50d);
        when(userRepository.findByUserId("0001")).thenReturn(Optional.of(user1));
        when(validator.supports(any())).thenReturn(true);
        Mockito.doAnswer(invocation -> {
            BeanPropertyBindingResult errors = (BeanPropertyBindingResult) invocation.getArguments()[1];
            errors.addError(new ObjectError("salary", "salary is too big"));
            return null;
        }).when(validator).validate(any(), any());

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.updateUser(user));

        assertEquals(Errors.USER_DATA_INVALID, exception.getError());
        verify(userRepository).findByUserId("0001");
        verify(validator).supports(User.class);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteUser() {
        when(userRepository.findByUserId("0001")).thenReturn(Optional.of(user1));

        userService.deleteUser("0001");

        verify(userRepository).findByUserId("0001");
        verify(userRepository).delete(user1);
    }

    @Test
    void deleteUser_NotExist() {
        when(userRepository.findByUserId("0001")).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.deleteUser("0001"));

        assertEquals(Errors.USER_ID_NOT_EXIST, exception.getError());
        verify(userRepository).findByUserId("0001");
        verify(userRepository, times(0)).delete(any());
    }
}