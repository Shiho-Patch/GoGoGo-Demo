package com.zcshou.gogogo.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 路线记录实体
 */
@Entity(tableName = "RouteHistory")
public class Route {
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String routeName;
    
    public String pointsJson;
    
    public long timestamp;

    public Route() {
    }

    public Route(String routeName, String pointsJson, long timestamp) {
        this.routeName = routeName;
        this.pointsJson = pointsJson;
        this.timestamp = timestamp;
    }
}
