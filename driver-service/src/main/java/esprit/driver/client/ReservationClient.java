package esprit.driver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "smartride-reservation-service")
public interface ReservationClient {

    @GetMapping("/api/reservations/driver/{driverId}")
    List<Map<String, Object>> getReservationsByDriverId(@PathVariable("driverId") Long driverId);
}
