package com.smartride.smartride_payment_service.client;

import com.smartride.smartride_payment_service.dto.TrajetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "trajet-s")
public interface TrajetClient {

    @GetMapping("/api/trajets/{id}")
    TrajetDTO getTrajetById(@PathVariable Long id);
}