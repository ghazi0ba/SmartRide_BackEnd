package esprit.driver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "trajet-s")
public interface TrajetClient {

    @GetMapping("/smartride_trajet/api/trajets")
    List<Trajet> getAllTrajets();

    @GetMapping("/smartride_trajet/api/trajets/{id}")
    Trajet getTrajetById(@PathVariable("id") Long id);

}
