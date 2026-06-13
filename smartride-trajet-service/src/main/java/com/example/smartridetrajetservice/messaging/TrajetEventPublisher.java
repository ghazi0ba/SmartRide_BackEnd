package com.example.smartridetrajetservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrajetEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishStatusChanged(Long trajetId, String statut) {
        TrajetStatusChangedEvent event = new TrajetStatusChangedEvent(trajetId, statut);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RK_TRAJET_STATUS,
                event
        );
        log.info("[RabbitMQ →] TrajetStatusChangedEvent publié (trajetId={}, statut={})", trajetId, statut);
    }

    public void publishTerminated(Long userId, Long chauffeurId, Long trajetId) {
        TrajetTerminatedEvent event = new TrajetTerminatedEvent(userId, chauffeurId, trajetId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RK_TRAJET_TERMINATED,
                event
        );
        log.info("[RabbitMQ →] TrajetTerminatedEvent publié (trajetId={}, userId={}, chauffeurId={})",
                trajetId, userId, chauffeurId);
    }
}
