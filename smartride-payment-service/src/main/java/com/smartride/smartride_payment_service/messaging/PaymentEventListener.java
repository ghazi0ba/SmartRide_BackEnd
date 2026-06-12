package com.smartride.smartride_payment_service.messaging;

import com.smartride.smartride_payment_service.entity.Payment;
import com.smartride.smartride_payment_service.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher publisher;

    public PaymentEventListener(PaymentRepository paymentRepository, PaymentEventPublisher publisher) {
        this.paymentRepository = paymentRepository;
        this.publisher = publisher;
    }

    /** Scénario 2 : réservation confirmée → créer le paiement, puis notifier (scénario 3). */
    @RabbitListener(queues = RabbitMQConfig.Q_RESERVATION_CONFIRMED)
    public void onReservationConfirmed(ReservationConfirmedEvent event) {
        log.info("[RabbitMQ <-] ReservationConfirmedEvent recu (reservationId={}, montant={})",
                event.getReservationId(), event.getMontant());

        Payment payment = new Payment();
        payment.setRideId(event.getTrajetId());
        payment.setAmount(event.getMontant());
        payment.setStatus("SUCCESS");
        payment.setReservationId(event.getReservationId());
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
        log.info("Paiement cree pour la reservation {}", event.getReservationId());

        // Scénario 3 : notifier reservation-service que le paiement est abouti
        publisher.publishCompleted(
                new PaymentCompletedEvent(event.getReservationId(), "SUCCESS", event.getMontant())
        );
    }
}
