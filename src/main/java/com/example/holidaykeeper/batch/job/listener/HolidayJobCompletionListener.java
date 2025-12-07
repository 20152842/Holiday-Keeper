package com.example.holidaykeeper.batch.job.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HolidayJobCompletionListener implements JobExecutionListener {
	@Override
	public void afterJob(JobExecution jobExecution) {
		log.info("Holiday Job [{}] finished with status={}",
			jobExecution.getJobInstance().getJobName(),
			jobExecution.getStatus());

		jobExecution.getStepExecutions().forEach(step -> {
			log.info("  Step={} | Read={} | Write={} | Skip={}",
				step.getStepName(),
				step.getReadCount(),
				step.getWriteCount(),
				step.getSkipCount());
		});
	}
}