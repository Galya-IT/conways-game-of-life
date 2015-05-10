package com.sirma.training.galya.conwaysgameoflife;

import java.security.InvalidParameterException;

public class Cell {
    private float left;
    private float top;
    private float right;
    private float bottom;

    public Cell(float[] coordinates) {
        if (coordinates == null && coordinates.length < 4) {
            throw new InvalidParameterException("The array doesn't contain all coordinates needed.");
        }
        left = coordinates[0];
        top = coordinates[1];
        right = coordinates[2];
        bottom = coordinates[3];
    }

    public float[] getCoordinates() {
        return new float[] { left, top, right, bottom };
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    @Override
    public boolean equals(Object obj) {
        Cell cell2 = (Cell) obj;
        return this.hashCode() == cell2.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Float.floatToIntBits(left);
        hash = 31 * hash + Float.floatToIntBits(top);
        hash = 31 * hash + Float.floatToIntBits(right);
        hash = 31 * hash + Float.floatToIntBits(bottom);
        return hash;
    }
}
