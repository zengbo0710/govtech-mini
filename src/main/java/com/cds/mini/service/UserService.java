package com.cds.mini.service;

import com.cds.mini.Constants;
import com.cds.mini.entity.Account;
import com.cds.mini.entity.User;
import com.cds.mini.error.Errors;
import com.cds.mini.error.ServiceException;
import com.cds.mini.repository.AccountRepository;
import com.cds.mini.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class UserService {

    private static final BigDecimal UPPER_BOUND_SALARY = BigDecimal.valueOf(4000);
    private static final String USER_ID_SORT_NAME = "UserId";
    public static final String NAME_SORT_NAME = "Name";
    public static final String SALARY_SORT_NAME = "Salary";
    private final UserRepository userRepository;
    private final SpringValidator<User> userSpringValidator;

    public UserService(UserRepository userRepository,
                       @Qualifier("beanValidator") org.springframework.validation.Validator validator) {
        this.userRepository = userRepository;
        userSpringValidator = new SpringValidator<>();
        userSpringValidator.setValidator(validator);
    }

    public List<com.cds.mini.model.User> getUsers() {
        return userRepository.findAll().stream()
                .map(UserService::convertToDTO).collect(Collectors.toList());
    }

    public List<com.cds.mini.model.User> getUsersWithSalaryRange(String sortName, String sortDirection) {
        Sort sort = null;
        if (SALARY_SORT_NAME.equalsIgnoreCase(sortName) || NAME_SORT_NAME.equalsIgnoreCase(sortName) || USER_ID_SORT_NAME.equalsIgnoreCase(sortName)) {
            Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, sortName);
        }
        List<User> userList;
        if (sort == null) {
            userList = userRepository.findBySalaryBetween(BigDecimal.ZERO, UPPER_BOUND_SALARY);
        } else {
            userList = userRepository.findBySalaryBetween(BigDecimal.ZERO, UPPER_BOUND_SALARY, sort);
        }

        return userList.stream()
                .map(UserService::convertToDTO).collect(Collectors.toList());
    }

    public com.cds.mini.model.User getUser(String userId) {
        User user = getExistingUser(userId);
        if (user != null) {
            return convertToDTO(user);
        }
        return null;
    }

    public static com.cds.mini.model.User convertToDTO(User user) {
        return new com.cds.mini.model.User()
                .userId(user.getUserId())
                .name(user.getName())
                .salary(user.getSalary().doubleValue());
    }

    private User convertToEntity(com.cds.mini.model.User user) {
        User entity = new User();
        entity.setUserId(user.getUserId());
        entity.setName(user.getName());
        entity.setSalary(BigDecimal.valueOf(user.getSalary()));
        entity.setCreatedBy(Constants.DATA_USER);
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        entity.setCreatedDate(today);
        entity.setUpdatedBy(Constants.DATA_USER);
        entity.setUpdatedDate(today);
        return entity;
    }

    public void createUser(com.cds.mini.model.User user) {
        if (getExistingUser(user.getUserId()) != null) {
            throw new ServiceException(Errors.USER_ID_EXIST);
        }

        User userEntity = convertToEntity(user);
        validate(userEntity);
        createUser(userEntity);
    }

    private void createUser(User user) {
        userRepository.save(user);
        log.info("User saved successfully {}", user);
    }

    private void validate(User user) {
        try {
            userSpringValidator.validate(user);
        } catch (Exception ex) {
            throw new ServiceException(Errors.USER_DATA_INVALID, ex);
        }
    }

    @Transactional
    public void createUsers(List<User> users) {
        for (User user : users) {
            createUser(user);
        }
    }

    public void updateUser(com.cds.mini.model.User user) {
        User userEntity = getExistingUser(user.getUserId());
        if (userEntity == null) {
            throw new ServiceException(Errors.USER_ID_NOT_EXIST);
        }
        userEntity.setSalary(BigDecimal.valueOf(user.getSalary()));
        userEntity.setUpdatedBy(Constants.DATA_USER);
        userEntity.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));

        validate(userEntity);
        userRepository.save(userEntity);
        log.info("User updated successfully {}", user);
    }

    public void deleteUser(String userId) {
        User user = getExistingUser(userId);
        if (user == null) {
            throw new ServiceException(Errors.USER_ID_NOT_EXIST);
        }
        userRepository.delete(user);
        log.info("User deleted successfully {}", user);
    }

    private User getExistingUser(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public void createAccounts() {
        User user = userRepository.findByUserId("0001").orElse(null);
        if (user != null) {
            IntStream.rangeClosed(1, 10).mapToObj(index -> {
                Account account = new Account();
                account.setUser(user);
                account.setAccountNumber("00000" + index);

                return account;
            }).forEach(account -> accountRepository.save(account));
        }
    }
    @Transactional
    public void listAccounts() {
        userRepository.findByUserId("0001")
                .ifPresent(user -> user.getAccounts().forEach(account -> System.out.println(account.getAccountNumber())));
    }
}
