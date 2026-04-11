package com.bikram.springbootproject.worker;

import com.bikram.springbootproject.config.RabbitMQConfig;
import com.bikram.springbootproject.dto.TaskMessage;
import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.model.TaskType;
import com.bikram.springbootproject.repo.TaskRepo;
import com.bikram.springbootproject.service.CsvProcessTaskHandler;
import com.bikram.springbootproject.service.EmailServiceHandler;
import com.bikram.springbootproject.service.ReportGenerationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@AllArgsConstructor
@Component
public class TaskConsumer {
    private final TaskRepo taskRepo;
    private final EmailServiceHandler emailServiceHandler;
    private final CsvProcessTaskHandler csvProcessTaskHandler;
    private final ReportGenerationService reportGenerationService;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consumeEmailTask(TaskMessage taskMessage){
        log.info("EMAIL_QUEUE received task id ={}",taskMessage.getTaskId());
        Task task = findAndMarkRunning(taskMessage);
        if(task == null) return;
        long start = System.currentTimeMillis();
        try {
            String result=emailServiceHandler.handle(task);
            markCompleted(task,result,System.currentTimeMillis()-start);
        } catch (Exception e) {
            handleFailureAndRetry(task,e);
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.CSV_IMPORT_QUEUE)
    public void consumeImportCsvTask(TaskMessage taskMessage){
        log.info("CSV_IMPORT_QUEUE received task id ={}",taskMessage.getTaskId());
        Task task = findAndMarkRunning(taskMessage);
        if(task == null) return;
        long start = System.currentTimeMillis();
        try {
            String result = csvProcessTaskHandler.handle(task);
            markCompleted(task,result,System.currentTimeMillis()-start);
        }catch (Exception e){
            handleFailureAndRetry(task,e);
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.REPORT_QUEUE)
    public void consumeReportTask(TaskMessage taskMessage) {
        log.info("REPORT_QUEUE is generated,Task id={}", taskMessage.getTaskId());
        Task task = findAndMarkRunning(taskMessage);
        if (task == null) return;
        long start = System.currentTimeMillis();
        try {
            String result = reportGenerationService.handle(task);
            markCompleted(task, result, System.currentTimeMillis() - start);
        } catch (Exception e) {
            handleFailureAndRetry(task,e);
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.OTHERS_QUEUE)
    public void consumeOthersTask(TaskMessage taskMessage){
        log.info("[OTHERS_QUEUE] Received task id={}", taskMessage.getTaskId());
        Task task = taskRepo.findById(taskMessage.getTaskId()).orElse(null);
        if(task == null) return;
    }
    private Task findAndMarkRunning(TaskMessage message){
        Task task = taskRepo.findById(message.getTaskId()).orElse(null);
        if(task == null){
            log.warn("Task [{}] not found",message.getTaskId());
        }
        if(task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.CANCELED){
            log.warn("Task [{}] already in terminal state {} — skipping", task.getId(), task.getStatus());
            return null;
        }
        task.setStatus(TaskStatus.RUNNING);
        taskRepo.save(task);
        return task;
    }

    private void markCompleted(Task task,String result,long executionTimeInMs){
        if(task.getType() == TaskType.OTHERS) task.setStatus(TaskStatus.PENDING);
    task.setStatus(TaskStatus.COMPLETED);
    task.setResult(result);
    task.setExecutionTime(executionTimeInMs);
    task.setError(null);
    taskRepo.save(task);
        log.info("Task [{}] COMPLETED in {}ms", task.getId(), executionTimeInMs);
    }
    private void handleFailureAndRetry(Task task,Exception e){
        int newTry = task.getRetryCount()+1;
        task.setRetryCount(newTry);
        if(task.getRetryCount() <= task.getMaxRetries()){
            task.setStatus(TaskStatus.RETRYING);
            task.setError("Attempt :" + newTry + "Failure" + e.getMessage());
            taskRepo.save(task);
            log.warn("Task [{}] RETRYING — attempt {}/{}", task.getId(), newTry, task.getMaxRetries());
        }else {
            task.setStatus(TaskStatus.FAILED);
            task.setError("All retries exhausted. Last error: " + e.getMessage());
            taskRepo.save(task);
            log.error("Task [{}] FAILED after {} retries", task.getId(), task.getMaxRetries());
        }
    }
}
