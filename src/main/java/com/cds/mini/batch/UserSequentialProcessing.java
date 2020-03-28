package com.cds.mini.batch;

import com.cds.mini.Constants;
import com.cds.mini.entity.User;
import com.cds.mini.error.Errors;
import com.cds.mini.error.ServiceException;
import com.cds.mini.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnProperty("user.sequential.enabled")
@Slf4j
@Deprecated
/**
 * This class for processing the import sequentially. It's no longer needed.
 */
public class UserSequentialProcessing {

    private final Resource csvResource;
    private final UserService userService;
    private final boolean stopOnError;
    private final org.springframework.validation.Validator validator;

    public UserSequentialProcessing(@Qualifier("beanValidator") org.springframework.validation.Validator validator,
                                    @Value("${user.batch.csv}") Resource csvResource,
                                    @Value("${user.batch.stop-on-error:false}") boolean stopOnError,
                                    UserService userService) {
        this.csvResource = csvResource;
        this.stopOnError = stopOnError;
        this.userService = userService;
        this.validator = validator;
    }

    @PostConstruct
    public void init() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("user-sequential");
        executor.execute(this::start);
    }

    private void start() {
        FlatFileItemReader<User> itemReader = flatFileItemReader();
        try {
            User user;
            log.info("Start importing the user data");
            Timestamp today = Timestamp.valueOf(LocalDateTime.now());
            List<User> users = new ArrayList<>();
            while ((user = itemReader.read()) != null) {
                if (validate(user)) {
                    user.setCreatedBy(Constants.DATA_USER);
                    user.setCreatedDate(today);
                    user.setUpdatedBy(Constants.DATA_USER);
                    user.setUpdatedDate(today);
                    users.add(user);
                }
            }
            userService.createUsers(users);
            log.info("Finish importing the user data");
        } catch (Exception ex) {
            log.error("Unexpected error during importing the data.", ex);
            throw new ServiceException(Errors.USER_IMPORT_ERROR);
        }
    }

    private boolean validate(User user) throws ValidationException {
        try {
            springValidator().validate(user);
            return true;
        } catch (ValidationException ex) {
            if (stopOnError) {
                throw ex;
            }
        }
        return false;
    }

    private FlatFileItemReader<User> flatFileItemReader() {
        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("userId", "name", "salary");
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new UserFieldSetMapper(springValidator(), 1, stopOnError));

        FlatFileItemReader<User> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(csvResource);
        itemReader.setLineMapper(lineMapper);
        itemReader.setLinesToSkip(1);
        itemReader.open(new ExecutionContext());

        return itemReader;
    }

    @Bean
    public Validator<User> springValidator() {
        SpringValidator<User> springValidator = new SpringValidator<>();
        springValidator.setValidator(validator);
        return springValidator;
    }
}
