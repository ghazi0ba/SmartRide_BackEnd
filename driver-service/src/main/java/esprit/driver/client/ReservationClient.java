package esprit.driver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "reservation-service", url = "http://localhost:8086")
public interface ReservationClient {

    @GetMapping("/api/reservations/driver/{driverId}")
    List<Map<String, Object>> getReservationsByDriverId(@PathVariable("driverId") Long driverId);
}
