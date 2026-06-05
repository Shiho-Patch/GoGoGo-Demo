package com.zcshou.gogogo.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 收藏位置实体（新功能）
 */
@Entity(tableName = "FavoriteLocation")
public class FavoriteLocation {
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    
    public String address;
    
    public String longitudeWgs84;
    
    public String latitudeWgs84;
    
    public String longitudeCustom;
    
    public String latitudeCustom;
    
    public long timestamp;
    
    public int sortOrder; // 排序顺序

    public FavoriteLocation() {
    }

    public FavoriteLocation(String name, String address, String longitudeWgs84, String latitudeWgs84,
                          String longitudeCustom, String latitudeCustom, long timestamp, int sortOrder) {
        this.name = name;
        this.address = address;
        this.longitudeWgs84 = longitudeWgs84;
        this.latitudeWgs84 = latitudeWgs84;
        this.longitudeCustom = longitudeCustom;
        this.latitudeCustom = latitudeCustom;
        this.timestamp = timestamp;
        this.sortOrder = sortOrder;
    }
}
