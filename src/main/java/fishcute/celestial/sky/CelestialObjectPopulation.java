package fishcute.celestial.sky;

import java.util.ArrayList;

public class CelestialObjectPopulation extends CelestialObject {
    //Lazy way of implementing a performance upgrade
    //Maybe it will work

    public ArrayList<CelestialObject> population;

    public CelestialObject baseObject;

    public final boolean perObjectCalculation;

    public CelestialObjectPopulation(ArrayList<CelestialObject> population, CelestialObject baseObject, boolean perObjectCalculation) {
        super(null, null, null, 0, 0, 0, 0, null, 0, 0, 0, 0, null, null, null, null, null, null);
        this.population = population;
        this.baseObject = baseObject;
        this.perObjectCalculation = perObjectCalculation;
    }

    @Override
    public boolean isPopulation() {
        return true;
    }
}
