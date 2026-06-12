import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Rating, RatingDocument } from './schemas/rating.schema';
import { CreateRatingDto } from './dto/create-rating.dto';
import { UpdateRatingDto } from './dto/update-rating.dto';

@Injectable()
export class RatingService {

  constructor(
    @InjectModel(Rating.name)
    private ratingModel: Model<RatingDocument>
  ) {}

  // ── CREATE ─────────────────────────────
  async create(dto: CreateRatingDto): Promise<Rating> {
    const rating = new this.ratingModel(dto);
    return rating.save();
  }

  // ── READ ALL ───────────────────────────
  async findAll(): Promise<Rating[]> {
    return this.ratingModel.find().exec();
  }

  // ── READ BY ID ─────────────────────────
  async findOne(id: string): Promise<Rating> {
    const rating = await this.ratingModel.findById(id).exec();
    if (!rating) throw new NotFoundException(`Rating ${id} non trouvé`);
    return rating;
  }

  // ── READ BY CHAUFFEUR ──────────────────
  async findByChauffeur(chauffeurId: string): Promise<Rating[]> {
    return this.ratingModel.find({ chauffeurId }).exec();
  }

  // ── MOYENNE CHAUFFEUR ──────────────────
  async getMoyenne(chauffeurId: string): Promise<object> {
    const ratings = await this.ratingModel.find({
      chauffeurId,
      statut: 'approved'
    }).exec();

    if (ratings.length === 0) {
      return { chauffeurId, moyenne: 0, totalAvis: 0 };
    }

    const moyenne = ratings.reduce((sum, r) => sum + r.note, 0) / ratings.length;

    return {
      chauffeurId,
      moyenne: Math.round(moyenne * 10) / 10,
      totalAvis: ratings.length
    };
  }

  // ── UPDATE ─────────────────────────────
  async update(id: string, dto: UpdateRatingDto): Promise<Rating> {
    const rating = await this.ratingModel
      .findByIdAndUpdate(id, dto, { new: true })
      .exec();
    if (!rating) throw new NotFoundException(`Rating ${id} non trouvé`);
    return rating;
  }

  // ── DELETE ─────────────────────────────
  async remove(id: string): Promise<object> {
    await this.ratingModel.findByIdAndDelete(id).exec();
    return { message: `Rating ${id} supprimé` };
  }

  // ── MODERATION ─────────────────────────
  async approuver(id: string): Promise<Rating> {
    return this.update(id, { statut: 'approved' });
  }

  async rejeter(id: string): Promise<Rating> {
    return this.update(id, { statut: 'rejected' });
  }
}