import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { ConfigModule } from '@nestjs/config';
import { RatingModule } from './ratings/rating.module';
import { RabbitMQService } from './rabbitmq.consumer';
import { RatingService } from './ratings/rating.service';
import { Rating, RatingSchema } from './ratings/schemas/rating.schema';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    MongooseModule.forRoot('mongodb://localhost:27017/rating-service'),
    MongooseModule.forFeature([{ name: Rating.name, schema: RatingSchema }]),
    RatingModule,
  ],
  providers: [RabbitMQService, RatingService],
})
export class AppModule {}