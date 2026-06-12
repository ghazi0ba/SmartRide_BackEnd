package com.smartride.smartride_payment_service.messaging;

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
 * Configuration RabbitMQ — payment-service.
 * Consommateur de : reservation.confirmed  (file payment.reservation-confirmed.queue)
 * Producteur de  : payment.completed
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "smartride.exchange";

    public static final String RK_RESERVATION_CONFIRMED = "reservation.confirmed";
    public static final String RK_PAYMENT_COMPLETED      = "payment.completed";

    public static final String Q_RESERVATION_CONFIRMED = "payment.reservation-confirmed.queue";

    @Bean
    public TopicExchange smartrideExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue reservationConfirmedQueue() {
        return new Queue(Q_RESERVATION_CONFIRMED, true);
    }

    @Bean
    public Binding bindReservationConfirmed(Queue reservationConfirmedQueue, TopicExchange smartrideExchange) {
        return BindingBuilder.bind(reservationConfirmedQueue).to(smartrideExchange).with(RK_RESERVATION_CONFIRMED);
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
