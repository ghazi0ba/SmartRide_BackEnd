import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { RatingController } from './rating.controller';
import { RatingService } from './rating.service';
import { Rating, RatingSchema } from './schemas/rating.schema';
import { RabbitMQService } from '../rabbitmq.consumer';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: Rating.name, schema: RatingSchema }])
  ],
  controllers: [RatingController],
  providers: [RatingService, RabbitMQService],
})
export class RatingModule {}