package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.Data.CrudDataBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EjectedPilotInfosRepository {
    private CrudDataBase dataBase;

    public EjectedPilotInfosRepository(@Autowired CrudDataBase dataBase) {
        this.dataBase = dataBase;
    }

    public List<EjectedPilotInfo> getAllInfos(){
        return dataBase.getAllOfType(EjectedPilotInfo.class);
    }

    public void createEjectedPilotInfo(){
        dataBase.create(new EjectedPilotInfo());
    }
}
