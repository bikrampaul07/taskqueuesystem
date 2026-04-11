package com.bikram.springbootproject.service;

import com.bikram.springbootproject.dto.EmailPayload;
import com.bikram.springbootproject.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Slf4j
@Component
public class EmailServiceHandler {
    private final ObjectMapper objectMapper;
    private EmailService emailService;

    public String handle(Task task) throws Exception{
        EmailPayload payload = objectMapper.readValue(task.getPayload(), EmailPayload.class);
        log.info("  EMAIL_SEND task [{}]", task.getId());
        log.info("  To      : {}", payload.getTo());
        log.info("  Subject : {}", payload.getSubject());
        log.info("  Body    : {}", payload.getBody());
        try {
            emailService.mailSend(payload);
            log.info("Real email delivered to {}", payload.getTo());
        } catch (Exception e) {
            log.warn("Mail server unavailable — falling back to mock. Reason: {}", e.getMessage());
            // Spec: mock = sleep 2-5 seconds then succeed
            int delayMs = ThreadLocalRandom.current().nextInt(2000, 5001);
            log.info("Mock email: sleeping {}ms ...", delayMs);
            Thread.sleep(delayMs);
            log.info("Mock email: simulated delivery complete ✓");
        }

        return "Email send to : " + payload.getTo();
    }
}
