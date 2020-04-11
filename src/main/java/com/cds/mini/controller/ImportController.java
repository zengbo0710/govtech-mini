package com.cds.mini.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Base64;

@RestController
@RequestMapping("/load")
@Slf4j
public class ImportController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("importCSVJob")
    private Job importCSVJob;

    @GetMapping
    public BatchStatus importUsers() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder()
                .addString("timestamp", LocalDateTime.now().toString());
        JobExecution jobExecution = jobLauncher.run(importCSVJob, builder.toJobParameters());
//        JobExecution jobExecution = jobLauncher.run(importCSVJob, new JobParameters());
        log.info("Start the import-csv-job");
        while (jobExecution.isRunning()) {
            System.out.println("in progress");
        }

        log.info("Job is done");

        return jobExecution.getStatus();
    }

    @GetMapping("/base64")
    public BatchStatus importUsersFromBase64(@RequestParam(name = "data") String base64Data) throws Exception {

        String data = new String(Base64.getDecoder().decode(base64Data));

        JobParametersBuilder builder = new JobParametersBuilder()
                .addString("data", data)
                .addString("timestamp", LocalDateTime.now().toString());
        JobExecution jobExecution = jobLauncher.run(importCSVJob, builder.toJobParameters());
        log.info("Start the import-csv-job");
        while (jobExecution.isRunning()) {
            System.out.println("in progress");
        }

        log.info("Job is done");

        return jobExecution.getStatus();
    }
}
