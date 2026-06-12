package com.example.smartridetrajetservice.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ — trajet-service (producteur).
 *
 * Échange et clés de routage partagés (mêmes valeurs dans tous les services).
 * Le convertisseur JSON est configuré en TypePrecedence INFERRED : le type est
 * déduit du paramètre du @RabbitListener côté consommateur, ce qui permet la
 * communication entre services même si les classes d'événement ont des packages différents.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "smartride.exchange";
    public static final String RK_TRAJET_STATUS = "trajet.status.changed";

    @Bean
    public TopicExchange smartrideExchange() {
        return new TopicExchange(EXCHANGE, true, false);
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
