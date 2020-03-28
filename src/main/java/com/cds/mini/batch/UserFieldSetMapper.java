package com.cds.mini.batch;

import com.cds.mini.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.validator.Validator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * The class is to parse and validate the csv data.
 * The <code>stopOnError</code> is to allow the parsing and validating be continued if the error occurs.
 */
@Slf4j
public class UserFieldSetMapper implements FieldSetMapper<User> {

    private final Timestamp timestamp;
    private final int loggedInUser;
    private final boolean stopOnError;
    private final Validator<User> validator;

    public UserFieldSetMapper(Validator<User> validator, int loggedInUser, boolean stopOnError) {
        this.validator = validator;
        this.loggedInUser = loggedInUser;
        this.stopOnError = stopOnError;
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
    }

    @Override
    public User mapFieldSet(FieldSet fieldSet) {
        try {
            String userId = fieldSet.readString(BatchConfig.USER_ID_FIELD);
            String name = fieldSet.readString(BatchConfig.NAME_FIELD);
            BigDecimal salary = fieldSet.readBigDecimal(BatchConfig.SALARY_FIELD);

            User user = new User();
            user.setUserId(userId);
            user.setName(name);
            user.setSalary(salary);
            user.setCreatedBy(loggedInUser);
            user.setCreatedDate(timestamp);
            user.setUpdatedBy(loggedInUser);
            user.setUpdatedDate(timestamp);

            validator.validate(user);

            return user;
        } catch (Exception ex) {
            if (stopOnError) {
                throw ex;
            } else {
                throw new FlatFileParseException(ex.getMessage(), fieldSet.toString());
            }
        }
    }
}
