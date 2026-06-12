package com.example.smartridereservationservice.client;

import com.example.smartridereservationservice.dto.TrajetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "trajet-s", path = "/smartride_trajet/api/trajets")
public interface TrajetClient {

    @GetMapping("/{id}")
    TrajetDTO getTrajetById(@PathVariable("id") Long id);
}
