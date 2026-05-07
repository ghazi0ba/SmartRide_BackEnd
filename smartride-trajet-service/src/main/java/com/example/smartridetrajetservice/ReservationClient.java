package com.example.smartridetrajetservice;

import com.example.smartridetrajetservice.dto.ReservationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "smartride-reservation-service")
    public interface ReservationClient {

    @GetMapping("api/reservations/{id}")
    ReservationDto  getReservation(@PathVariable String id);
    }

