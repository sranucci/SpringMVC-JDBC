package ar.edu.itba.paw.models.unit;

public class Unit {
    private long id;
    private String unitName;

    public Unit(long id, String unitName) {
        this.id = id;
        this.unitName = unitName;
    }

    public long getId() {
        return id;
    }

    public String getUnitName() {
        return unitName;
    }
}
