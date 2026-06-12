import { Controller, Get, Post, Put, Delete, Body, Param } from '@nestjs/common';
import { RatingService } from './rating.service';
import { CreateRatingDto } from './dto/create-rating.dto';
import { UpdateRatingDto } from './dto/update-rating.dto';
import { RabbitMQService } from '../rabbitmq.consumer';

@Controller('ratings')
export class RatingController {

  constructor(
    private readonly ratingService: RatingService,
    private readonly rabbitMQService: RabbitMQService,
  ) {}

  // ── SIMULATION TRAJET TERMINÉ ──────────
  @Post('simulate/trajet-termine')
  simulateTrajet(@Body() data: any) {
    return this.rabbitMQService.simulateTrajetTermine(data);
  }

  // POST /ratings
  @Post()
  create(@Body() dto: CreateRatingDto) {
    return this.ratingService.create(dto);
  }

  // GET /ratings
  @Get()
  findAll() {
    return this.ratingService.findAll();
  }

  // GET /ratings/chauffeur/:id
  @Get('chauffeur/:id')
  findByChauffeur(@Param('id') id: string) {
    return this.ratingService.findByChauffeur(id);
  }

  // GET /ratings/moyenne/:id
  @Get('moyenne/:id')
  getMoyenne(@Param('id') id: string) {
    return this.ratingService.getMoyenne(id);
  }

  // GET /ratings/:id
  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.ratingService.findOne(id);
  }

  // PUT /ratings/:id
  @Put(':id')
  update(@Param('id') id: string, @Body() dto: UpdateRatingDto) {
    return this.ratingService.update(id, dto);
  }

  // DELETE /ratings/:id
  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.ratingService.remove(id);
  }

  // PUT /ratings/:id/approuver
  @Put(':id/approuver')
  approuver(@Param('id') id: string) {
    return this.ratingService.approuver(id);
  }

  // PUT /ratings/:id/rejeter
  @Put(':id/rejeter')
  rejeter(@Param('id') id: string) {
    return this.ratingService.rejeter(id);
  }
}