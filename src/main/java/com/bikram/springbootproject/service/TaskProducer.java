package com.bikram.springbootproject.service;


import com.bikram.springbootproject.config.RabbitMQConfig;
import com.bikram.springbootproject.dto.TaskMessage;
import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TaskProducer {
    public final RabbitTemplate rabbitTemplate;

    public void publishTask(Task task){
        String routingKey = resolveRoutingKey(task.getType());
        TaskMessage taskMessage = new TaskMessage(
                task.getId(),
                task.getType(),
                task.getPriority(),
                task.getRetryCount()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TASK_EXCHANGE,
                routingKey,
                taskMessage
        );

        log.info("Task [{}] published to exchange='{}' routingKey='{}'",
                task.getId(), RabbitMQConfig.TASK_EXCHANGE, routingKey);
    }

    private String resolveRoutingKey(TaskType taskType){
        return switch (taskType){
            case EMAIL_SEND -> RabbitMQConfig.EMAIL_ROUTING_KEY;
            case CSV_IMPORT -> RabbitMQConfig.CSV_IMPORT_ROUTING_KEY;
            case REPORT_GENERATION -> RabbitMQConfig.REPORT_ROUTING_KEY;
            case OTHERS -> RabbitMQConfig.OTHERS_ROUTING_KEY;
        };
    }
}
