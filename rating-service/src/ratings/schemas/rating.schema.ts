import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

export type RatingDocument = Rating & Document;

@Schema({ timestamps: true })
export class Rating {

  @Prop({ required: true })
  userId!: string;

  @Prop({ required: true })
  chauffeurId!: string;

  @Prop({ required: true })
  trajetId!: string;

 

@Prop({ required: true, min: 0, max: 5 })
note: number;

  @Prop()
  commentaire!: string;

  @Prop({ default: 'pending', enum: ['pending', 'approved', 'rejected'] })
  statut!: string;
}

export const RatingSchema = SchemaFactory.createForClass(Rating);