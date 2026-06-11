package esprit.driver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "trajet-s")
public interface TrajetClient {

    @GetMapping("/api/trajets")
    List<Trajet> getAllTrajets();

    @GetMapping("/api/trajets/{id}")
    Trajet getTrajetById(@PathVariable("id") int id);

}