package mowitnow.infra;

import mowitnow.Orientation;
import mowitnow.Tondeuse;

public record SimpleTondeuse(Integer x, Integer y, Orientation orientation) implements Tondeuse {
    @Override
    public Integer getX() {
        return x;
    }

    @Override
    public Integer getY() {
        return y;
    }

    @Override
    public Orientation getOrientation() {
        return orientation;
    }
}