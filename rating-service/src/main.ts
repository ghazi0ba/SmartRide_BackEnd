import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import eurekaClient from './eureka.config';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.enableCors();
  app.setGlobalPrefix('api');
  await app.listen(8087);
  console.log(`🚀 Rating Service running on http://localhost:8087`);

  // Démarrer Eureka
  eurekaClient.start((error: Error) => {
    if (error) {
      console.log('❌ Eureka connection failed:', error);
    } else {
      console.log('✅ Registered in Eureka successfully!');
    }
  });
}
bootstrap();