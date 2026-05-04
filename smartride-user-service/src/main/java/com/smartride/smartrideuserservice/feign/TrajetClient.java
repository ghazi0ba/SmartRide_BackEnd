package com.smartride.smartrideuserservice.feign;

import com.smartride.smartrideuserservice.models.Trajet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "smartride-trajet-service", url = "http://localhost:8082/smartride_trajet")
public interface TrajetClient {

    @GetMapping("/api/trajets")
    List<Trajet> getAllTrajets();

    @GetMapping("/api/trajets/{id}")
    Trajet getTrajetById(@PathVariable Long id);

    @GetMapping("/api/trajets/passager/{passagerId}")
    List<Trajet> getTrajetsByPassager(@PathVariable Long passagerId);
}