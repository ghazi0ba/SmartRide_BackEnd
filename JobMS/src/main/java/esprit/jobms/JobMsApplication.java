package esprit.jobms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JobMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobMsApplication.class, args);
    }

}
