package com.example.holidaykeeper.batch.job.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HolidayStepCompletionListener implements StepExecutionListener {
	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("Step {} started", stepExecution.getStepName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("Step {} completed: status={}, read={}, write={}, skip={}",
			stepExecution.getStepName(),
			stepExecution.getStatus(),
			stepExecution.getReadCount(),
			stepExecution.getWriteCount(),
			stepExecution.getSkipCount());
		return stepExecution.getExitStatus();
	}
}