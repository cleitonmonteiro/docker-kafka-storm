package io.github.cleitonmonteiro.helper;

import io.github.cleitonmonteiro.model.PositionModel;

public class GeolocationHelper {
    // Return the distance in meters.
    public static double distanceBetween(PositionModel start, PositionModel end) {
        double earthRadius = 6378137.0;
        double dLat = _toRadians(end.getLatitude() - start.getLatitude());
        double dLon = _toRadians(end.getLongitude() - start.getLongitude());

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(_toRadians(start.getLatitude())) *
                        Math.cos(_toRadians(end.getLatitude()));
        double c = 2 * Math.asin(Math.sqrt(a));

        return earthRadius * c;
    }

    public static double _toRadians(double degree) {
        return degree * Math.PI / 180;
    }
}
