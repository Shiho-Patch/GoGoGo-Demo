package com.zcshou.service;

import android.location.Location;

import java.util.List;
import java.util.Random;

public class RouteSimulator {
    private List<double[]> routePoints;
    private int currentIndex = 0;
    private float distanceTraveledInSegment = 0;
    private int targetLoopCount = 1;
    private int currentLoopCount = 0;
    private double speed = 1.2;
    private boolean speedFloatEnabled = false;
    private Random random = new Random();
    private boolean isSimulating = false;
    private double currentLng = 0;
    private double currentLat = 0;
    private float currentBearing = 0;

    public RouteSimulator() {}

    public void startRoute(List<double[]> points, double speed, int loopCount, boolean speedFloat) {
        if (points == null || points.isEmpty()) {
            return;
        }
        this.routePoints = points;
        this.speed = speed;
        this.targetLoopCount = loopCount;
        this.currentLoopCount = 0;
        this.speedFloatEnabled = speedFloat;
        this.currentIndex = 0;
        this.distanceTraveledInSegment = 0;
        this.currentLng = points.get(0)[0];
        this.currentLat = points.get(0)[1];
        this.isSimulating = true;
    }

    public void stopRoute() {
        isSimulating = false;
    }

    public boolean isSimulating() {
        return isSimulating;
    }

    public void processStep() {
        if (!isSimulating || routePoints == null || routePoints.size() < 2) {
            return;
        }

        if (currentIndex >= routePoints.size() - 1) {
            currentLoopCount++;
            if (currentLoopCount < targetLoopCount) {
                currentIndex = 0;
                distanceTraveledInSegment = 0;
                double[] start = routePoints.get(0);
                currentLng = start[0];
                currentLat = start[1];
            } else {
                isSimulating = false;
            }
            return;
        }

        double[] startPoint = routePoints.get(currentIndex);
        double[] endPoint = routePoints.get(currentIndex + 1);

        float[] results = new float[2];
        Location.distanceBetween(startPoint[1], startPoint[0], endPoint[1], endPoint[0], results);
        float totalSegmentDistance = results[0];
        float bearing = results[1];

        double currentStepSpeed = speed;
        if (speedFloatEnabled) {
            double factor = 0.9 + (random.nextDouble() * 0.2);
            currentStepSpeed = speed * factor;
        }

        double stepDistance = currentStepSpeed * 0.1;
        distanceTraveledInSegment += stepDistance;

        if (distanceTraveledInSegment >= totalSegmentDistance) {
            currentIndex++;
            distanceTraveledInSegment = 0;
            currentLng = endPoint[0];
            currentLat = endPoint[1];
            currentBearing = bearing;
        } else {
            double ratio = distanceTraveledInSegment / totalSegmentDistance;
            currentLat = startPoint[1] + (endPoint[1] - startPoint[1]) * ratio;
            currentLng = startPoint[0] + (endPoint[0] - startPoint[0]) * ratio;
            currentBearing = bearing;
        }
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public float getCurrentBearing() {
        return currentBearing;
    }
}
