package sec.geo.shape;

import org.gavaghan.geodesy.GlobalPosition;

public class Point {

    private final double longitudeDegrees;
    private final double latitudeDegrees;
    private final double altitudeMeters;

    public Point(double longitudeDegrees, double latitudeDegrees) {
        this(longitudeDegrees, latitudeDegrees, 0);
    }

    public Point(double longitudeDegrees, double latitudeDegrees, double altitudeMeters) {
        this.longitudeDegrees = longitudeDegrees;
        this.latitudeDegrees = latitudeDegrees;
        this.altitudeMeters = altitudeMeters;
    }

    public double getLongitude() {
        return longitudeDegrees;
    }

    public double getLatitude() {
        return latitudeDegrees;
    }

    public double getAltitude() {
        return altitudeMeters;
    }

    public GlobalPosition toGlobalPos() {
        return new GlobalPosition(getLatitude(), getLongitude(), getAltitude());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.longitudeDegrees) ^ (Double.doubleToLongBits(this.longitudeDegrees) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.latitudeDegrees) ^ (Double.doubleToLongBits(this.latitudeDegrees) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.altitudeMeters) ^ (Double.doubleToLongBits(this.altitudeMeters) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        final Point other = (Point) obj;
        if (Double.doubleToLongBits(this.longitudeDegrees) != Double.doubleToLongBits(other.longitudeDegrees)) {
            return false;
        }
        if (Double.doubleToLongBits(this.latitudeDegrees) != Double.doubleToLongBits(other.latitudeDegrees)) {
            return false;
        }
        if (Double.doubleToLongBits(this.altitudeMeters) != Double.doubleToLongBits(other.altitudeMeters)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[" + longitudeDegrees + "," + latitudeDegrees + "," + altitudeMeters + "]";
    }
}
