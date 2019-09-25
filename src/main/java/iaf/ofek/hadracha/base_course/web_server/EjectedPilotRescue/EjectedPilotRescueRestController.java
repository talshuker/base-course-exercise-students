package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.Data.CrudDataBase;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ejectedPilotRescue")
public class EjectedPilotRescueRestController {
    private CrudDataBase dataBase;
    private final AirplanesAllocationManager airplanesAllocationManager;

    public EjectedPilotRescueRestController(CrudDataBase dataBase, AirplanesAllocationManager airplanesAllocationManager) {
        this.dataBase = dataBase;
        this.airplanesAllocationManager = airplanesAllocationManager;
    }

    @GetMapping("/infos")
    public List<EjectedPilotInfo> getInfos(){
        return dataBase.getAllOfType(EjectedPilotInfo.class);
    }

    @GetMapping("/takeResponsibility")
    public void takeResponsibility(@RequestParam("ejectionId") int ejectionId, @CookieValue("client-id") String clientId){
        EjectedPilotInfo ejectedPilotInfo = dataBase.getByID(ejectionId, EjectedPilotInfo.class);
        if (ejectedPilotInfo == null) {
            throw new IllegalArgumentException("No such ejection");
        }

        // If someone is already responsible, don't do anything
        if (ejectedPilotInfo.rescuedBy!=null)
            return;

        ejectedPilotInfo.rescuedBy = clientId;
        dataBase.update(ejectedPilotInfo);
        airplanesAllocationManager.allocateAirplanesForEjection(ejectedPilotInfo, clientId);
    }
}
