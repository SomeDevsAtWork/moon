package net.somedevsatwork.moonmod;

public class MoonPlayer {
    private int tick;
    private int oxygenatedRegion;

    public MoonPlayer() {
        this.tick = 0;
        this.oxygenatedRegion = -1;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getOxygenatedRegion() {
        return oxygenatedRegion;
    }

    public void setOxygenatedRegion(int oxygenatedRegion) {
        this.oxygenatedRegion = oxygenatedRegion;
    }
}
