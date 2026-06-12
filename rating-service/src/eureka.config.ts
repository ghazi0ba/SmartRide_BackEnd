import Eureka from 'eureka-js-client';

const eurekaClient = new Eureka({
  instance: {
    app: 'RATING-SERVICE',
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    statusPageUrl: 'http://localhost:8087/info',
    healthCheckUrl: 'http://localhost:8087/health',
    homePageUrl: 'http://localhost:8087/',
    port: {
      '$': 8087,
      '@enabled': true,
    },
    vipAddress: 'RATING-SERVICE',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: 'localhost',
    port: 8761,
    servicePath: '/eureka/apps/',
    maxRetries: 10,
    requestRetryDelay: 2000,
  },
});

export default eurekaClient;