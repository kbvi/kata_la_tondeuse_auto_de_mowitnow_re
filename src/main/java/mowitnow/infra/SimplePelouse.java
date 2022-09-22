package mowitnow.infra;

import mowitnow.Pelouse;

public record SimplePelouse(Integer maxX, Integer maxY) implements Pelouse {
    @Override
    public Integer getMaxX() {
        return maxX;
    }

    @Override
    public Integer getMaxY() {
        return maxY;
    }
}