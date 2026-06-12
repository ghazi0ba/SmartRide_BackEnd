package com.example.smartridereservationservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishConfirmed(ReservationConfirmedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RK_RESERVATION_CONFIRMED,
                event
        );
        log.info("[RabbitMQ →] ReservationConfirmedEvent publié (reservationId={}, montant={})",
                event.getReservationId(), event.getMontant());
    }
}
