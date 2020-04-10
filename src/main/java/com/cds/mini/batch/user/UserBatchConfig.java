package com.cds.mini.batch.user;

import com.cds.mini.entity.User;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.validation.ValidationException;

@Configuration
@EnableBatchProcessing
public class UserBatchConfig {

    @Bean(name = "importCSVJob")
    public Job importCSVJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                            ItemReader<User> itemReader, ItemReadListener<User> itemReadListener,
                            ItemProcessor<User, User> itemProcessor,
                            ItemWriter<User> itemWriter) {

        Step step = stepBuilderFactory.get("import-csv-step")
                .<User, User>chunk(3)
                .reader(itemReader)
                .listener(itemReadListener)
                .processor(itemProcessor)
                .writer(itemWriter)
                .faultTolerant()
                .skipLimit(10)
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .build();

        return jobBuilderFactory.get("import-csv-job")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();

    }

    @Bean
    public ItemReader<User> itemReader(@Value("${user.batch.csv}") Resource resource) {
        FlatFileItemReader<User> itemReader = new FlatFileItemReader<>();

        itemReader.setLinesToSkip(1);
        itemReader.setResource(resource);
        itemReader.setLineMapper(lineMapper());

        return itemReader;
    }

    @Bean
    public LineMapper<User> lineMapper() {
        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("userId", "name", "salary");
        lineMapper.setLineTokenizer(lineTokenizer);

        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);
        fieldSetMapper.setStrict(true);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }
}
