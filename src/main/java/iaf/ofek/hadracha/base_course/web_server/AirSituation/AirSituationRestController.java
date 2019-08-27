package iaf.ofek.hadracha.base_course.web_server.AirSituation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/airSituation")
public class AirSituationRestController {

    private AirSituationProvider airSituationProvider;

    public AirSituationRestController( @Autowired AirSituationProvider airSituationProvider) {
        this.airSituationProvider = airSituationProvider;
    }

    @GetMapping("/airSituation")
    public List<Airplane> getAirSituation(){
        return airSituationProvider.getAllAirplanes();
    }
}
