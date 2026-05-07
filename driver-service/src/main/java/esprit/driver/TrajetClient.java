package esprit.driver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "trajet-s")
public interface TrajetClient {


        @RequestMapping("api/trajets")
        public List<Trajet> getAllTrajets();
        @RequestMapping("jobs/{id}")
        public Trajet getTrajetById(@PathVariable int id);


}
