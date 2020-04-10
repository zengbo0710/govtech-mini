package com.cds.mini.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        JobParameters jobParameters = new JobParameters();
        JobExecution jobExecution = jobLauncher.run(importCSVJob, jobParameters);

        log.info("Start the import-csv-job");
        while (jobExecution.isRunning()) {
            System.out.println("in progress");
        }

        log.info("Job is done");

        return jobExecution.getStatus();
    }
}
