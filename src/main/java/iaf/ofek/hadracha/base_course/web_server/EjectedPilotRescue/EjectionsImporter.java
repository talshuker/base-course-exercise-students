package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.Data.CrudDataBase;
import iaf.ofek.hadracha.base_course.web_server.Data.Entity;
import iaf.ofek.hadracha.base_course.web_server.Utilities.ListOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class EjectionsImporter {

    @Value("${ejections.server.url}")
    public String EJECTION_SERVER_URL;
    @Value("${ejections.namespace}")
    public String NAMESPACE;

    private AirplanesAllocationManager airplanesAllocationManager;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final RestTemplate restTemplate;
    private final CrudDataBase dataBase;
    private final ListOperations listOperations;
    private static final Double SHIFT_NORTH = 1.7;

    public EjectionsImporter(RestTemplateBuilder restTemplateBuilder, CrudDataBase dataBase, ListOperations listOperations, @Autowired AirplanesAllocationManager airplanesAllocationManager) {
        restTemplate = restTemplateBuilder.build();
        this.dataBase = dataBase;
        this.listOperations = listOperations;
        executor.scheduleAtFixedRate(this::updateEjections, 1, 1, TimeUnit.SECONDS);
        this.airplanesAllocationManager = airplanesAllocationManager;

    }

    private void updateEjections() {
        try {
            List<EjectedPilotInfo> ejectionsFromServer = getEjectionsFromServer();
            if (ejectionsFromServer != null) {
                shiftAirPlanes(ejectionsFromServer);
            }

            List<EjectedPilotInfo> previousEjections = dataBase.getAllOfType(EjectedPilotInfo.class);
            ejectionsDifference(ejectionsFromServer, previousEjections);

        } catch (RestClientException e) {
            System.err.println("Could not get ejections: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void shiftAirPlanes(List<EjectedPilotInfo> ejectionsFromServer) {
        for (EjectedPilotInfo ejectedPilotInfo : ejectionsFromServer) {
            ejectedPilotInfo.getCoordinates().lat += SHIFT_NORTH;
        }
    }

    private List<EjectedPilotInfo> getEjectionsFromServer() {
        List<EjectedPilotInfo> ejectionsFromServer;
        ResponseEntity<List<EjectedPilotInfo>> responseEntity = restTemplate.exchange(
                EJECTION_SERVER_URL + "/ejections?name=" + NAMESPACE, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<EjectedPilotInfo>>() {
                });
        ejectionsFromServer = responseEntity.getBody();
        return ejectionsFromServer;
    }

    private void ejectionsDifference(List<EjectedPilotInfo> updatedEjections, List<EjectedPilotInfo> previousEjections) {
        List<EjectedPilotInfo> addedEjections = listOperations.subtract(updatedEjections, previousEjections, new Entity.ByIdEqualizer<>());
        addedEjections.forEach(dataBase::create);
        List<EjectedPilotInfo> removedEjections = listOperations.subtract(previousEjections, updatedEjections, new Entity.ByIdEqualizer<>());
        removedEjections.stream().map(EjectedPilotInfo::getId).forEach(id -> dataBase.delete(id, EjectedPilotInfo.class));
    }

    public List<EjectedPilotInfo> getAllEjections() {
        return dataBase.getAllOfType(EjectedPilotInfo.class);
    }

    public void takeResponsibility(int ejectionId, String clientId) {
        EjectedPilotInfo ejectedPilot = dataBase.getByID(ejectionId, EjectedPilotInfo.class);
        if (ejectedPilot != null && ejectedPilot.getRescuedBy() == null) {
            ejectedPilot.setRescuedBy(clientId);
            airplanesAllocationManager.allocateAirplanesForEjection(ejectedPilot, clientId);
            dataBase.update(ejectedPilot);
        }
    }


}
