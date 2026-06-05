package com.zcshou.gogogo.data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elvishew.xlog.XLog;
import com.zcshou.gogogo.data.entity.FavoriteLocation;
import com.zcshou.gogogo.data.entity.HistoryLocation;
import com.zcshou.gogogo.data.entity.HistorySearch;
import com.zcshou.gogogo.data.entity.Route;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库迁移辅助类
 * 从旧的 SQLite 数据库迁移到 Room
 */
public class DatabaseMigrationHelper {
    
    private static final String TAG = "DatabaseMigrationHelper";
    
    private Context context;
    
    public DatabaseMigrationHelper(Context context) {
        this.context = context;
    }
    
    /**
     * 迁移所有旧数据库数据到 Room
     */
    public void migrateAllDatabases(AppDatabase newDb) {
        XLog.d(TAG, "Starting database migration...");
        
        try {
            migrateHistoryLocation(newDb);
            migrateHistorySearch(newDb);
            migrateRouteHistory(newDb);
            XLog.d(TAG, "Database migration completed successfully");
        } catch (Exception e) {
            XLog.e(TAG, "Database migration failed", e);
        }
    }
    
    /**
     * 迁移历史位置数据
     */
    private void migrateHistoryLocation(AppDatabase newDb) {
        File oldDbFile = context.getDatabasePath("HistoryLocation.db");
        if (!oldDbFile.exists()) {
            XLog.d(TAG, "Old HistoryLocation.db not found, skipping");
            return;
        }
        
        XLog.d(TAG, "Migrating HistoryLocation.db...");
        
        try (SQLiteDatabase oldDb = SQLiteDatabase.openDatabase(
                oldDbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY)) {
            
            List<HistoryLocation> locations = new ArrayList<>();
            
            try (Cursor cursor = oldDb.query(
                    "HistoryLocation",
                    null, null, null, null, null,
                    "DB_COLUMN_TIMESTAMP DESC")) {
                
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        HistoryLocation location = new HistoryLocation();
                        
                        int idIndex = cursor.getColumnIndex("DB_COLUMN_ID");
                        int locationIndex = cursor.getColumnIndex("DB_COLUMN_LOCATION");
                        int lngWgsIndex = cursor.getColumnIndex("DB_COLUMN_LONGITUDE_WGS84");
                        int latWgsIndex = cursor.getColumnIndex("DB_COLUMN_LATITUDE_WGS84");
                        int timestampIndex = cursor.getColumnIndex("DB_COLUMN_TIMESTAMP");
                        int lngCustomIndex = cursor.getColumnIndex("DB_COLUMN_LONGITUDE_CUSTOM");
                        int latCustomIndex = cursor.getColumnIndex("DB_COLUMN_LATITUDE_CUSTOM");
                        
                        if (idIndex >= 0) location.id = cursor.getInt(idIndex);
                        if (locationIndex >= 0) location.location = cursor.getString(locationIndex);
                        if (lngWgsIndex >= 0) location.longitudeWgs84 = cursor.getString(lngWgsIndex);
                        if (latWgsIndex >= 0) location.latitudeWgs84 = cursor.getString(latWgsIndex);
                        if (timestampIndex >= 0) location.timestamp = cursor.getLong(timestampIndex);
                        if (lngCustomIndex >= 0) location.longitudeCustom = cursor.getString(lngCustomIndex);
                        if (latCustomIndex >= 0) location.latitudeCustom = cursor.getString(latCustomIndex);
                        
                        locations.add(location);
                    } while (cursor.moveToNext());
                }
            }
            
            // 插入到新数据库
            for (HistoryLocation location : locations) {
                try {
                    newDb.historyLocationDao().insert(location);
                } catch (Exception e) {
                    XLog.e(TAG, "Failed to insert location", e);
                }
            }
            
            XLog.d(TAG, "Migrated " + locations.size() + " history locations");
            
        } catch (Exception e) {
            XLog.e(TAG, "Failed to migrate HistoryLocation.db", e);
        }
    }
    
    /**
     * 迁移历史搜索数据
     */
    private void migrateHistorySearch(AppDatabase newDb) {
        File oldDbFile = context.getDatabasePath("HistorySearch.db");
        if (!oldDbFile.exists()) {
            XLog.d(TAG, "Old HistorySearch.db not found, skipping");
            return;
        }
        
        XLog.d(TAG, "Migrating HistorySearch.db...");
        
        try (SQLiteDatabase oldDb = SQLiteDatabase.openDatabase(
                oldDbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY)) {
            
            List<HistorySearch> searches = new ArrayList<>();
            
            try (Cursor cursor = oldDb.query(
                    "HistorySearch",
                    null, null, null, null, null,
                    "DB_COLUMN_TIMESTAMP DESC")) {
                
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        HistorySearch search = new HistorySearch();
                        
                        int idIndex = cursor.getColumnIndex("DB_COLUMN_ID");
                        int keyIndex = cursor.getColumnIndex("DB_COLUMN_KEY");
                        int descIndex = cursor.getColumnIndex("DB_COLUMN_DESCRIPTION");
                        int timestampIndex = cursor.getColumnIndex("DB_COLUMN_TIMESTAMP");
                        int isLocationIndex = cursor.getColumnIndex("DB_COLUMN_IS_LOCATION");
                        int lngWgsIndex = cursor.getColumnIndex("DB_COLUMN_LONGITUDE_WGS84");
                        int latWgsIndex = cursor.getColumnIndex("DB_COLUMN_LATITUDE_WGS84");
                        int lngCustomIndex = cursor.getColumnIndex("DB_COLUMN_LONGITUDE_CUSTOM");
                        int latCustomIndex = cursor.getColumnIndex("DB_COLUMN_LATITUDE_CUSTOM");
                        
                        if (idIndex >= 0) search.id = cursor.getInt(idIndex);
                        if (keyIndex >= 0) search.searchKey = cursor.getString(keyIndex);
                        if (descIndex >= 0) search.description = cursor.getString(descIndex);
                        if (timestampIndex >= 0) search.timestamp = cursor.getLong(timestampIndex);
                        if (isLocationIndex >= 0) search.isLocation = cursor.getInt(isLocationIndex);
                        if (lngWgsIndex >= 0) search.longitudeWgs84 = cursor.getString(lngWgsIndex);
                        if (latWgsIndex >= 0) search.latitudeWgs84 = cursor.getString(latWgsIndex);
                        if (lngCustomIndex >= 0) search.longitudeCustom = cursor.getString(lngCustomIndex);
                        if (latCustomIndex >= 0) search.latitudeCustom = cursor.getString(latCustomIndex);
                        
                        searches.add(search);
                    } while (cursor.moveToNext());
                }
            }
            
            // 插入到新数据库
            for (HistorySearch search : searches) {
                try {
                    newDb.historySearchDao().insert(search);
                } catch (Exception e) {
                    XLog.e(TAG, "Failed to insert search", e);
                }
            }
            
            XLog.d(TAG, "Migrated " + searches.size() + " history searches");
            
        } catch (Exception e) {
            XLog.e(TAG, "Failed to migrate HistorySearch.db", e);
        }
    }
    
    /**
     * 迁移路线历史数据
     */
    private void migrateRouteHistory(AppDatabase newDb) {
        File oldDbFile = context.getDatabasePath("RouteHistory.db");
        if (!oldDbFile.exists()) {
            XLog.d(TAG, "Old RouteHistory.db not found, skipping");
            return;
        }
        
        XLog.d(TAG, "Migrating RouteHistory.db...");
        
        try (SQLiteDatabase oldDb = SQLiteDatabase.openDatabase(
                oldDbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY)) {
            
            List<Route> routes = new ArrayList<>();
            
            try (Cursor cursor = oldDb.query(
                    "RouteHistory",
                    null, null, null, null, null,
                    "TIMESTAMP DESC")) {
                
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Route route = new Route();
                        
                        int idIndex = cursor.getColumnIndex("ID");
                        int nameIndex = cursor.getColumnIndex("ROUTE_NAME");
                        int pointsIndex = cursor.getColumnIndex("POINTS_JSON");
                        int timestampIndex = cursor.getColumnIndex("TIMESTAMP");
                        
                        if (idIndex >= 0) route.id = cursor.getInt(idIndex);
                        if (nameIndex >= 0) route.routeName = cursor.getString(nameIndex);
                        if (pointsIndex >= 0) route.pointsJson = cursor.getString(pointsIndex);
                        if (timestampIndex >= 0) route.timestamp = cursor.getLong(timestampIndex);
                        
                        routes.add(route);
                    } while (cursor.moveToNext());
                }
            }
            
            // 插入到新数据库
            for (Route route : routes) {
                try {
                    newDb.routeDao().insert(route);
                } catch (Exception e) {
                    XLog.e(TAG, "Failed to insert route", e);
                }
            }
            
            XLog.d(TAG, "Migrated " + routes.size() + " routes");
            
        } catch (Exception e) {
            XLog.e(TAG, "Failed to migrate RouteHistory.db", e);
        }
    }
}
