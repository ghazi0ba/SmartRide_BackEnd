package esprit.driver.messaging;

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
 * Configuration RabbitMQ — driver-service (consommateur).
 *
 * Consommateur de : reservation.created (file driver.reservation-created.queue)
 *
 * Échange et clé de routage partagés avec les autres services. Le convertisseur
 * JSON est en TypePrecedence INFERRED : le type est déduit du paramètre du
 * @RabbitListener, ce qui permet la désérialisation même si l'événement est
 * publié depuis un package différent (reservation-service).
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "smartride.exchange";
    public static final String RK_RESERVATION_CREATED = "reservation.created";
    public static final String RK_RESERVATION_CANCELLED = "reservation.cancelled";
    public static final String Q_RESERVATION_CREATED = "driver.reservation-created.queue";
    public static final String Q_RESERVATION_CANCELLED = "driver.reservation-cancelled.queue";

    @Bean
    public TopicExchange smartrideExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue reservationCreatedQueue() {
        return new Queue(Q_RESERVATION_CREATED, true);
    }

    @Bean
    public Queue reservationCancelledQueue() {
        return new Queue(Q_RESERVATION_CANCELLED, true);
    }

    @Bean
    public Binding bindReservationCreated(Queue reservationCreatedQueue, TopicExchange smartrideExchange) {
        return BindingBuilder.bind(reservationCreatedQueue).to(smartrideExchange).with(RK_RESERVATION_CREATED);
    }

    @Bean
    public Binding bindReservationCancelled(Queue reservationCancelledQueue, TopicExchange smartrideExchange) {
        return BindingBuilder.bind(reservationCancelledQueue).to(smartrideExchange).with(RK_RESERVATION_CANCELLED);
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
