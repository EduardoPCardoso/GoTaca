package com.gotaca.gotaca;

public class GeoPoint extends com.google.firebase.firestore.GeoPoint{

    private double longitude, latitude;

    public GeoPoint(double longitude, double latitude) {
        super(longitude, latitude);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String toString() {
        return new String("logintude: " + longitude + ", latitude: " + latitude);
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double distance(GeoPoint point) {
        final double RADIO_TIERRA = 6371000; // em metros
        double dLat = Math.toRadians(latitude - point.latitude);
        double dLon = Math.toRadians(longitude - point.longitude);
        double lat1 = Math.toRadians(point.latitude);
        double lat2 = Math.toRadians(latitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) +
                Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * RADIO_TIERRA;
    }
}
