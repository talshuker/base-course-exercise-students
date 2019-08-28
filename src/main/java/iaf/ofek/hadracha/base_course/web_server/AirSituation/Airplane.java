package iaf.ofek.hadracha.base_course.web_server.AirSituation;

import iaf.ofek.hadracha.base_course.web_server.Data.Entity;

public class Airplane implements Entity<Airplane> {
    private AirplaneKind airplaneKind;

    public int id;
    /**
     * North component of GEO-WGS84 coordination. Negative is south hemisphere.
     */
    public double latitude;
    /**
     * East component of GEO-WGS84 coordination. Negative is west hemisphere.
     */
    public double longitude;
    /**
     * Azimuth in degrees. 0 indicates north, 90 indicates east
     */
    public double azimuth;
    /**
     * Velocity in KPH.
     */
    public double velocity;

    /**
     * The acceleration of change of azimuth
     */
    double radialAcceleration;

    public Airplane(AirplaneKind airplaneKind, int id) {
        this.airplaneKind = airplaneKind;
        this.id = id;
    }

    public AirplaneKind getAirplaneKind() {
        return airplaneKind;
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
    public Airplane clone() {
        try {
            return (Airplane) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Not possible - Entity implements Cloneable");
        }
    }
}
