package com.bikram.springbootproject.config;


import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.Query;

@Configuration
public class RabbitMQConfig {
    public static final String TASK_EXCHANGE = "task.exchange";

    public static final String EMAIL_QUEUE = "task.email.queue";
    public static final String CSV_IMPORT_QUEUE = "task.csv.import.queue";
    public static final String REPORT_QUEUE = "task.report.queue";
    public static final String OTHERS_QUEUE = "task.others.queue";

    public static final String EMAIL_DLQ           = "task.email.dlq";
    public static final String CSV_IMPORT_DLQ      = "task.csv.import.dlq";
    public static final String REPORT_DLQ          = "task.report.dlq";
    public static final String OTHERS_DLQ          = "task.others.dlq";

    public static final String EMAIL_ROUTING_KEY           = "task.email";
    public static final String CSV_IMPORT_ROUTING_KEY      = "task.csv.import";
    public static final String REPORT_ROUTING_KEY          = "task.report";
    public static final String OTHERS_ROUTING_KEY          = "task.others";

    public static final String TASK_DLX = "task.dlx";   // -->Dead letter exchange

    @Bean
    public TopicExchange taskExchange(){
        return new TopicExchange(TASK_EXCHANGE,true,false);
    }

    @Bean
    public DirectExchange deadLetterExchange(){
        return new DirectExchange(TASK_DLX,true,false);

    }

    @Bean
    public Queue emailQueue(){
        return QueueBuilder
                .durable(EMAIL_QUEUE)
                .withArgument("dead-letter-exchange",TASK_DLX)
                .withArgument("dead-letter-routing-key",EMAIL_ROUTING_KEY + ".dead")
                .build();
    }

    @Bean
    public Queue csvReportQueue(){
        return QueueBuilder
                .durable(CSV_IMPORT_QUEUE)
                .withArgument("dead-letter-exchange",TASK_DLX)
                .withArgument("dead-letter-routing-key",CSV_IMPORT_ROUTING_KEY + ".dead")
                .build();
    }

    @Bean
    public Queue reportQueue(){
        return QueueBuilder
                .durable(REPORT_QUEUE)
                .withArgument("dead-letter-exchange",TASK_DLX)
                .withArgument("dead-letter-routing-key",REPORT_ROUTING_KEY + ".dead")
                .build();
    }

    @Bean
    public Queue othersQueue(){
        return QueueBuilder
                .durable(OTHERS_QUEUE)
                .withArgument("dead-letter-queue",TASK_DLX)
                .withArgument("dead-letter-routing-key",OTHERS_ROUTING_KEY + ".dead")
                .build();
    }

    @Bean
    public Queue emailDlq(){
        return QueueBuilder.durable(EMAIL_DLQ).build();
    }
    @Bean
    public Queue csvImportDlq(){
        return QueueBuilder.durable(CSV_IMPORT_DLQ).build();
    }@Bean
    public Queue reportDlq(){
        return QueueBuilder.durable(REPORT_DLQ).build();
    }@Bean
    public Queue othersDlq(){
        return QueueBuilder.durable(OTHERS_DLQ).build();
    }

    @Bean
    public Binding emailBinding(){
        return BindingBuilder.bind(emailQueue()).to(taskExchange()).with(EMAIL_ROUTING_KEY);
    }
    @Bean
    public Binding csvImportBinding(){
        return BindingBuilder.bind(csvReportQueue()).to(taskExchange()).with(CSV_IMPORT_ROUTING_KEY);
    }@Bean
    public Binding reportBinding(){
        return BindingBuilder.bind(reportQueue()).to(taskExchange()).with(REPORT_ROUTING_KEY);
    }@Bean
    public Binding othersBinding(){
        return BindingBuilder.bind(othersQueue()).to(taskExchange()).with(OTHERS_ROUTING_KEY);
    }

    @Bean
    public Binding emailDlqBinding(){
        return BindingBuilder.bind(emailDlq()).to(deadLetterExchange()).with(EMAIL_ROUTING_KEY + ".dead");
    }
    @Bean
    public Binding csvImportDlqBinding(){
        return BindingBuilder.bind(csvImportDlq()).to(deadLetterExchange()).with(CSV_IMPORT_ROUTING_KEY + ".dead");
    }@Bean
    public Binding reportDlqBinding(){
        return BindingBuilder.bind(reportDlq()).to(deadLetterExchange()).with(REPORT_ROUTING_KEY + ".dead");
    }@Bean
    public Binding othersDlqBinding(){
        return BindingBuilder.bind(othersDlq()).to(deadLetterExchange()).with(OTHERS_ROUTING_KEY + ".dead");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
    @Bean
   public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate((org.springframework.amqp.rabbit.connection.ConnectionFactory) connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory((org.springframework.amqp.rabbit.connection.ConnectionFactory) connectionFactory);
        simpleRabbitListenerContainerFactory.setMessageConverter(jsonMessageConverter());
        simpleRabbitListenerContainerFactory.setConcurrentConsumers(2);
        simpleRabbitListenerContainerFactory.setMaxConcurrentConsumers(5);
        simpleRabbitListenerContainerFactory.setDefaultRequeueRejected(false);
        return simpleRabbitListenerContainerFactory;
    }




}
