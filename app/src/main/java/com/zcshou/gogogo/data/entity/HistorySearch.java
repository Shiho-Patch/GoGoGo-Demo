package com.zcshou.gogogo.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 历史搜索记录实体
 */
@Entity(
    tableName = "HistorySearch",
    indices = {
        @Index(value = "searchKey", unique = true)
    }
)
public class HistorySearch {
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String searchKey;
    
    public String description;
    
    public long timestamp;
    
    public int isLocation; // 0: 搜索关键字, 1: 位置搜索结果
    
    public String longitudeWgs84;
    
    public String latitudeWgs84;
    
    public String longitudeCustom;
    
    public String latitudeCustom;

    public HistorySearch() {
    }

    public HistorySearch(String searchKey, String description, long timestamp, int isLocation,
                        String longitudeWgs84, String latitudeWgs84, 
                        String longitudeCustom, String latitudeCustom) {
        this.searchKey = searchKey;
        this.description = description;
        this.timestamp = timestamp;
        this.isLocation = isLocation;
        this.longitudeWgs84 = longitudeWgs84;
        this.latitudeWgs84 = latitudeWgs84;
        this.longitudeCustom = longitudeCustom;
        this.latitudeCustom = latitudeCustom;
    }
}
