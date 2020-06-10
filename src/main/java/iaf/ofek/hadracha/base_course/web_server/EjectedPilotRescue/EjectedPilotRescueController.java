package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.AirSituation.AirSituationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ejectedPilotRescue")
public class EjectedPilotRescueController {
    private EjectionsImporter ejectionsImporter;

    public EjectedPilotRescueController( @Autowired EjectionsImporter ejectionsImporter) {
        this.ejectionsImporter = ejectionsImporter;
    }

    @GetMapping("/infos")
    public List<EjectedPilotInfo> getInfo(){
        return ejectionsImporter.getAllEjections();
    }
}
