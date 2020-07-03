package net.somedevsatwork.moonmod.capability;

public class OxygenStorage implements IOxygenStorage {
    protected int capacity;
    protected int oxygenLevel;

    public OxygenStorage(int givenCapacity, int givenLevel) {
        capacity = givenCapacity;
        oxygenLevel = givenLevel;
    }

    @Override
    public int getLevel() {
        return oxygenLevel;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int accept(int amount) {
        int canAccept = capacity - oxygenLevel;
        int accepted = Math.min(canAccept, amount);
        oxygenLevel += accepted;

        return amount - accepted;
    }

    @Override
    public int extract(int amount) {
        int actualExtract = Math.min(oxygenLevel, amount);

        oxygenLevel -= actualExtract;

        return actualExtract;
    }

    @Override
    public boolean isFull() {
        return oxygenLevel == capacity;
    }
}