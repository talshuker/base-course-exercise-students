package iaf.ofek.hadracha.base_course.web_server.Launches;

import iaf.ofek.hadracha.base_course.web_server.Data.Entity;
import org.springframework.util.Assert;

public class Launch implements Entity<Launch> {

    private int id;
    public Nz launchPoint;
    public Nz impactPoint;

    public Launch(Nz launchPoint, Nz impactPoint) {
        this.launchPoint = launchPoint;
        this.impactPoint = impactPoint;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Launch clone() {
        try {
            return (Launch) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Not possible - Entity implements Cloneable");
        }
    }
}
