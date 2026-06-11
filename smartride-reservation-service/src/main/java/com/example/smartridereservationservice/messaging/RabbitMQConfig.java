package com.example.smartridereservationservice.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ — reservation-service.
 *
 * Producteur de : reservation.confirmed
 * Consommateur de :
 *   - trajet.status.changed   (file reservation.trajet-status.queue)
 *   - payment.completed       (file reservation.payment-completed.queue)
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "smartride.exchange";

    // Clés de routage
    public static final String RK_TRAJET_STATUS        = "trajet.status.changed";
    public static final String RK_RESERVATION_CONFIRMED = "reservation.confirmed";
    public static final String RK_PAYMENT_COMPLETED    = "payment.completed";

    // Files consommées par reservation-service
    public static final String Q_TRAJET_STATUS     = "reservation.trajet-status.queue";
    public static final String Q_PAYMENT_COMPLETED = "reservation.payment-completed.queue";

    @Bean
    public TopicExchange smartrideExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue trajetStatusQueue() {
        return new Queue(Q_TRAJET_STATUS, true);
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return new Queue(Q_PAYMENT_COMPLETED, true);
    }

    @Bean
    public Binding bindTrajetStatus(Queue trajetStatusQueue, TopicExchange smartrideExchange) {
        return BindingBuilder.bind(trajetStatusQueue).to(smartrideExchange).with(RK_TRAJET_STATUS);
    }

    @Bean
    public Binding bindPaymentCompleted(Queue paymentCompletedQueue, TopicExchange smartrideExchange) {
        return BindingBuilder.bind(paymentCompletedQueue).to(smartrideExchange).with(RK_PAYMENT_COMPLETED);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
