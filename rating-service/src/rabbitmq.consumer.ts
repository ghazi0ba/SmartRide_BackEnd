import { Injectable, Logger, OnModuleInit } from '@nestjs/common';
import * as amqp from 'amqplib';
import { RatingService } from './ratings/rating.service';

@Injectable()
export class RabbitMQService implements OnModuleInit {
  private readonly logger = new Logger(RabbitMQService.name);
  private connection: any;
  private channel: any;

  private readonly RABBITMQ_URL = 'amqp://localhost:5672';
  private readonly QUEUE = 'trajet.terminated';

  constructor(private readonly ratingService: RatingService) {}

  async onModuleInit() {
    await this.connect();
    await this.consumeTrajetEvents();
  }

  async connect() {
    try {
      this.connection = await amqp.connect(this.RABBITMQ_URL);
      this.channel = await this.connection.createChannel();
      await this.channel.assertQueue(this.QUEUE, { durable: true });
      this.logger.log('✅ Connected to RabbitMQ');
    } catch (error: any) {
      this.logger.error(`❌ RabbitMQ error: ${error.message}`);
    }
  }

  // ── CONSUMER — Reçoit event Trajet Service ──
  async consumeTrajetEvents() {
    this.channel.consume(this.QUEUE, async (msg: any) => {
      if (msg !== null) {
        const event = JSON.parse(msg.content.toString());
        this.logger.log(`📨 Trajet terminé reçu: ${JSON.stringify(event)}`);

        // Créer avis vide automatiquement
        await this.ratingService.create({
          userId: event.userId,
          chauffeurId: event.chauffeurId,
          trajetId: event.trajetId,
          note: 0,
          commentaire: 'En attente de notation',
        });

        this.logger.log(`✅ Avis créé pour trajet: ${event.trajetId}`);
        this.channel.ack(msg);
      }
    });
    this.logger.log(`👂 Listening on queue: ${this.QUEUE}`);
  }

  // ── SIMULATION — Pour tester sans Trajet Service ──
  async simulateTrajetTermine(data: any) {
    const event = {
      userId: data.userId,
      chauffeurId: data.chauffeurId,
      trajetId: data.trajetId,
      event: 'TRAJET_TERMINATED',
    };

    this.channel.sendToQueue(
      this.QUEUE,
      Buffer.from(JSON.stringify(event)),
      { persistent: true }
    );

    this.logger.log(`📤 Simulation: ${JSON.stringify(event)}`);
    return { message: 'Trajet terminé simulé !', event };
  }
}