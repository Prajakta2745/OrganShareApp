package com.example.organshare.maps;

import android.graphics.Color;
import com.example.organshare.models.Checkpoint;
import com.example.organshare.models.DeliveryModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapTrackingHelper {

    public static void displayDeliveryRoute(GoogleMap map, DeliveryModel delivery) {
        if (map == null || delivery == null) return;
        map.clear();

        LatLng pickup = new LatLng(delivery.getPickupLatitude(), delivery.getPickupLongitude());
        LatLng destination = new LatLng(delivery.getDestinationLatitude(), delivery.getDestinationLongitude());

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> routePoints = new ArrayList<>();

        // Add Pickup Marker
        map.addMarker(new MarkerOptions()
                .position(pickup)
                .title("Pickup: " + delivery.getPickupAddress())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        builder.include(pickup);
        routePoints.add(pickup);

        // Add Checkpoint Markers
        if (delivery.getCheckpoints() != null) {
            for (Checkpoint cp : delivery.getCheckpoints()) {
                LatLng cpPoint = new LatLng(cp.getLatitude(), cp.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(cpPoint)
                        .title("Checkpoint: " + cp.getLocationName())
                        .snippet(cp.getStatusAtCheckpoint() + " (" + cp.getTimestamp() + ")")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                builder.include(cpPoint);
                routePoints.add(cpPoint);
            }
        }

        // Add Current Last Known Location Marker
        if (delivery.getLastKnownLatitude() != 0 && delivery.getLastKnownLongitude() != 0) {
            LatLng lastLoc = new LatLng(delivery.getLastKnownLatitude(), delivery.getLastKnownLongitude());
            map.addMarker(new MarkerOptions()
                    .position(lastLoc)
                    .title("Live Transit Location")
                    .snippet("Status: " + delivery.getCurrentStatus())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            builder.include(lastLoc);
            routePoints.add(lastLoc);
        }

        // Add Destination Marker
        map.addMarker(new MarkerOptions()
                .position(destination)
                .title("Destination: " + delivery.getDestinationHospitalName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        builder.include(destination);
        routePoints.add(destination);

        // Draw Polyline connecting route checkpoints
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(routePoints)
                .width(10f)
                .color(Color.parseColor("#006A6A"))
                .geodesic(true);
        map.addPolyline(polylineOptions);

        try {
            LatLngBounds bounds = builder.build();
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
        } catch (Exception e) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(pickup, 12f));
        }
    }
}
