package com.zcshou.gogogo.data.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 路线信息数据模型
 */
public class RouteInfo {
    private long id;
    private String name;
    private String description;
    private List<LocationInfo> points;
    private double speed;
    private int loopCount;
    private boolean speedFluctuation;
    private long createTime;
    private long updateTime;

    public RouteInfo() {
        this.points = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }

    public RouteInfo(String name) {
        this();
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LocationInfo> getPoints() {
        return points;
    }

    public void setPoints(List<LocationInfo> points) {
        this.points = points;
    }

    public void addPoint(LocationInfo point) {
        if (this.points == null) {
            this.points = new ArrayList<>();
        }
        this.points.add(point);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public boolean isSpeedFluctuation() {
        return speedFluctuation;
    }

    public void setSpeedFluctuation(boolean speedFluctuation) {
        this.speedFluctuation = speedFluctuation;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getPointCount() {
        return points != null ? points.size() : 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "RouteInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pointCount=" + getPointCount() +
                '}';
    }
}
