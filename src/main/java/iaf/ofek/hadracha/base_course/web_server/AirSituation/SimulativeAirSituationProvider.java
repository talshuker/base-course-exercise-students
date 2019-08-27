package iaf.ofek.hadracha.base_course.web_server.AirSituation;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simulator that manages airplanes and randomly advances them in position over time
 */
@Service
public class SimulativeAirSituationProvider implements AirSituationProvider {

    private static final int INITIAL_NUM_OF_AIRPLANES = 50;
    private static final double CHANCE_FOR_NUMBER_CHANGE = 0.0;
    private static final int STEP_SIZE = 10;
    private static final int SIMULATION_INTERVAL_MILLIS = 100;
    private static final double LAT_MIN = 28.000;
    private static final double LAT_MAX = 32.000;
    private static final double LON_MIN = 34.000;
    private static final double LON_MAX = 36.000;


    // Scheduler to run advancement task
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    // Object that acts as a lock for the updates
    private final Object lock = new Object();
    private final Random random = new Random();

    // The current air situation
    private List<Airplane> airplanes = new ArrayList<>();
    private int lastId = 0;


    public SimulativeAirSituationProvider() {
        for (int i = 0; i < INITIAL_NUM_OF_AIRPLANES; i++) {
            addNewAirplane();
        }
        lastId=INITIAL_NUM_OF_AIRPLANES;

        executor.scheduleAtFixedRate(this::UpdateSituation, 0, SIMULATION_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
    }

    // all airplane kinds that can be used
    private List<AirplaneKind> airplaneKinds = AirplaneKind.LeafKinds();

    private void addNewAirplane() {
        AirplaneKind kind = airplaneKinds.get(random.nextInt(airplaneKinds.size()));
        Airplane airplane = new Airplane(kind, lastId++);
        airplane.latitude = generateRandomDoubleInRange(LAT_MIN, LAT_MAX);
        airplane.longitude = generateRandomDoubleInRange(LON_MIN, LON_MAX);
        airplane.azimuth = generateRandomDoubleInRange(0,360);
        airplane.velocity = generateRandomDoubleInRange(20, 200);
        airplanes.add(airplane);
    }

    private void UpdateSituation() {
        synchronized (lock) {
            if (random.nextDouble() < CHANCE_FOR_NUMBER_CHANGE) { // chance to remove an airplane
                int indexToRemove = random.nextInt(airplanes.size());
                airplanes.remove(indexToRemove);
            }


            airplanes.forEach(airplane -> {
                airplane.azimuth += generateRandomDoubleInRange(-STEP_SIZE, STEP_SIZE)/8;
                airplane.latitude += Math.sin(worldAzimuthToEuclidRadians(airplane.azimuth)) * airplane.velocity/100000;
                airplane.longitude += Math.cos(worldAzimuthToEuclidRadians(airplane.azimuth)) * airplane.velocity/100000;
                if (airplane.latitude<LAT_MIN || airplane.latitude>LAT_MAX || airplane.longitude<LON_MIN || airplane.longitude>LON_MAX)
                    airplane.azimuth+=180;
            });

            if (random.nextDouble() < CHANCE_FOR_NUMBER_CHANGE) { // chance to add an airplane
                addNewAirplane();
            }
        }
    }

    /**
     * Gets world azimuth - degrees in which 0 is up and increases clockwise and converts it to
     * radians in which 0 is right and increases counter clockwise.
     */
    private double worldAzimuthToEuclidRadians(double azimuth) {
        double inEuclidDegrees = -azimuth + 90;
        return inEuclidDegrees * Math.PI / 180;
//        return azimuth * Math.PI /180;
    }

    /**
     * Generate a double between min (inclusive) and max (exclusive).
     */
    private double generateRandomDoubleInRange(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    @Override
    public List<Airplane> getAllAirplanes() {
        synchronized (lock){
            return new ArrayList<>(airplanes);
        }
    }
}
