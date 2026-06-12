import { Injectable, Logger, OnModuleInit } from '@nestjs/common';
import * as amqp from 'amqplib';
import { RatingService } from './ratings/rating.service';

@Injectable()
export class RabbitMQService implements OnModuleInit {
  private readonly logger = new Logger(RabbitMQService.name);
  private connection: any;
  private channel: any;

  private readonly RABBITMQ_URL = 'amqp://localhost:5672';

  // Aligné sur la topologie du backend SmartRide (exchange topic partagé)
  private readonly EXCHANGE = 'smartride.exchange';
  private readonly ROUTING_KEY = 'trajet.terminated';
  private readonly QUEUE = 'rating.trajet-terminated.queue';

  constructor(private readonly ratingService: RatingService) {}

  async onModuleInit() {
    await this.connect();
    await this.consumeTrajetEvents();
  }

  async connect() {
    try {
      this.connection = await amqp.connect(this.RABBITMQ_URL);
      this.channel = await this.connection.createChannel();
      // Exchange topic partagé + file dédiée au rating, liée par la clé de routage
      await this.channel.assertExchange(this.EXCHANGE, 'topic', { durable: true });
      await this.channel.assertQueue(this.QUEUE, { durable: true });
      await this.channel.bindQueue(this.QUEUE, this.EXCHANGE, this.ROUTING_KEY);
      this.logger.log('✅ Connected to RabbitMQ');
    } catch (error: any) {
      this.logger.error(`❌ RabbitMQ error: ${error.message}`);
    }
  }

  // ── CONSUMER — Reçoit l'événement "trajet terminé" du trajet-service ──
  async consumeTrajetEvents() {
    this.channel.consume(this.QUEUE, async (msg: any) => {
      if (msg !== null) {
        const event = JSON.parse(msg.content.toString());
        this.logger.log(`📨 Trajet terminé reçu: ${JSON.stringify(event)}`);

        // Crée automatiquement un avis "en attente de notation"
        await this.ratingService.create({
          userId: String(event.userId),
          chauffeurId: String(event.chauffeurId),
          trajetId: String(event.trajetId),
          note: 0,
          commentaire: 'En attente de notation',
        });

        this.logger.log(`✅ Avis créé pour trajet: ${event.trajetId}`);
        this.channel.ack(msg);
      }
    });
    this.logger.log(`👂 Listening on queue: ${this.QUEUE} (key: ${this.ROUTING_KEY})`);
  }

  // ── SIMULATION — Pour tester sans le trajet-service ──
  async simulateTrajetTermine(data: any) {
    const event = {
      userId: data.userId,
      chauffeurId: data.chauffeurId,
      trajetId: data.trajetId,
      event: 'TRAJET_TERMINATED',
    };

    this.channel.publish(
      this.EXCHANGE,
      this.ROUTING_KEY,
      Buffer.from(JSON.stringify(event)),
      { persistent: true }
    );

    this.logger.log(`📤 Simulation: ${JSON.stringify(event)}`);
    return { message: 'Trajet terminé simulé !', event };
  }
}
