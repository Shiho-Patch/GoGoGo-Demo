package com.zcshou.gogogo.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 历史位置记录实体
 */
@Entity(
    tableName = "HistoryLocation",
    indices = {
        @Index(value = {"longitudeWgs84", "latitudeWgs84"}, unique = true)
    }
)
public class HistoryLocation {
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String location;
    
    public String longitudeWgs84;
    
    public String latitudeWgs84;
    
    public long timestamp;
    
    public String longitudeCustom;
    
    public String latitudeCustom;

    public HistoryLocation() {
    }

    public HistoryLocation(String location, String longitudeWgs84, String latitudeWgs84, 
                          long timestamp, String longitudeCustom, String latitudeCustom) {
        this.location = location;
        this.longitudeWgs84 = longitudeWgs84;
        this.latitudeWgs84 = latitudeWgs84;
        this.timestamp = timestamp;
        this.longitudeCustom = longitudeCustom;
        this.latitudeCustom = latitudeCustom;
    }
}
