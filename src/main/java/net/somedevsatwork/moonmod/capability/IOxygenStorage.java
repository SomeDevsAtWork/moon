package net.somedevsatwork.moonmod.capability;

public interface IOxygenStorage {
    /**
     * Returns the level of oxygen this storage has
     * @return level of oxygen
     */
    int getLevel();

    /**
     * Get the maximum amount of oxygen this storage can hold
     * @return the maximum amount of oxygen
     */
    int getCapacity();

    /**
     * Receives an amount of oxygen and returns the amount that wasn't received.
     * @param amount the amount of oxygen taken
     * @return the amount of oxygen that was not taken
     */
    int accept(int amount);

    /**
     * Removes an amount of oxygen and returns the amount that was removed.
     * @param amount the amount of oxygen taken
     * @return the amount of oxygen that was taken
     */
    int extract(int amount);

    /**
     * Checks if this oxygen store is holding its capacity
     * @return this oxygen store is full
     */
    boolean isFull();
}
