package de.derredstoner.anticheat.util;

import lombok.Getter;

@Getter
public final class BoundingBox {

    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;

    public BoundingBox(final double minX, final double maxX, final double minY, final double maxY, final double minZ, final double maxZ) {
        if (minX < maxX) {
            this.minX = minX;
            this.maxX = maxX;
        } else {
            this.minX = maxX;
            this.maxX = minX;
        }
        if (minY < maxY) {
            this.minY = minY;
            this.maxY = maxY;
        } else {
            this.minY = maxY;
            this.maxY = minY;
        }
        if (minZ < maxZ) {
            this.minZ = minZ;
            this.maxZ = maxZ;
        } else {
            this.minZ = maxZ;
            this.maxZ = minZ;
        }
    }

    public BoundingBox move(final double x, final double y, final double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;

        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    public BoundingBox expand(final double x, final double y, final double z) {
        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;

        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    public double collidesD(RayTrace ray, double tmin, double tmax) {
        for (int i = 0; i < 3; i++) {
            double d = 1 / ray.direction(i);
            double t0 = (min(i) - ray.origin(i)) * d;
            double t1 = (max(i) - ray.origin(i)) * d;
            if (d < 0) {
                double t = t0;
                t0 = t1;
                t1 = t;
            }
            tmin = Math.max(t0, tmin);
            tmax = Math.min(t1, tmax);
            if (tmax <= tmin) return 0;
        }
        return tmin;
    }

    public double min(int i) {
        switch (i) {
            case 0:
                return minX;
            case 1:
                return minY;
            case 2:
                return minZ;
            default:
                return 0;
        }
    }

    public double max(int i) {
        switch (i) {
            case 0:
                return maxX;
            case 1:
                return maxY;
            case 2:
                return maxZ;
            default:
                return 0;
        }
    }

}
