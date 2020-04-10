package com.cds.mini.batch;

import com.cds.mini.batch.listeners.UserItemReadListener;
import com.cds.mini.batch.listeners.UserItemWriteListener;
import com.cds.mini.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

/**
 * The configuration to import the csv data into the database.
 * The {@link UserFieldSetMapper} is used to parse and validate the csv data before inserting the database.
 */
//@Configuration
@ConditionalOnProperty("user.batch.enabled")
//@EnableBatchProcessing
public class BatchConfig {

    public static final String USER_ID_FIELD = "userId";
    public static final String NAME_FIELD = "name";
    public static final String SALARY_FIELD = "salary";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final Resource inputResource;
    private final DataSource dataSource;
    private final boolean stopOnError;
    private final org.springframework.validation.Validator validator;
    private final int chunkSize;

    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                       @Qualifier("beanValidator") org.springframework.validation.Validator validator,
                       @Value("${user.batch.csv}")
                               Resource inputResource, DataSource dataSource,
                       @Value("${user.batch.stop-on-error:false}")
                               boolean stopOnError,
                       @Value("${user.batch.chunk-size:10}")
                               int chunkSize) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.validator = validator;
        this.inputResource = inputResource;
        this.dataSource = dataSource;
        this.stopOnError = stopOnError;
        this.chunkSize = chunkSize;
    }

    @Bean(name = "csvJob")
    public Job readCSVFileJob() {
        return jobBuilderFactory
                .get("readUserCSVFileJob")
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory
                .get("insert")
                .<User, User>chunk(chunkSize)
                .reader(reader())
                .listener(new UserItemReadListener())
                .writer(writer())
                .listener(new UserItemWriteListener())
                .faultTolerant()
                .skipLimit(10)
                .skip(FlatFileParseException.class)
                .build();
    }

    @Bean
    public FlatFileItemReader<User> reader() {
        FlatFileItemReader<User> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(lineMapper());
        itemReader.setLinesToSkip(1);
        itemReader.setResource(inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<User> lineMapper() {
        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(USER_ID_FIELD, NAME_FIELD, SALARY_FIELD);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new UserFieldSetMapper(springValidator(), 1, stopOnError));
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<User> writer() {
        JdbcBatchItemWriter<User> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO USER (UserId, Name, Salary, CreatedBy, CreatedDate, UpdatedBy, UpdatedDate) VALUES (:userId, :name, :salary, :createdBy, :createdDate, :updatedBy, :updatedDate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return itemWriter;
    }

    @Bean
    public Validator<User> springValidator() {
        SpringValidator<User> springValidator = new SpringValidator<>();
        springValidator.setValidator(validator);
        return springValidator;
    }
}
