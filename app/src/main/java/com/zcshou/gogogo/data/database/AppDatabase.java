package com.zcshou.gogogo.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zcshou.gogogo.data.dao.FavoriteLocationDao;
import com.zcshou.gogogo.data.dao.HistoryLocationDao;
import com.zcshou.gogogo.data.dao.HistorySearchDao;
import com.zcshou.gogogo.data.dao.RouteDao;
import com.zcshou.gogogo.data.entity.FavoriteLocation;
import com.zcshou.gogogo.data.entity.HistoryLocation;
import com.zcshou.gogogo.data.entity.HistorySearch;
import com.zcshou.gogogo.data.entity.Route;

/**
 * Room 数据库主类
 */
@Database(
    entities = {
        HistoryLocation.class,
        HistorySearch.class,
        Route.class,
        FavoriteLocation.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "gogogo_database.db";
    private static volatile AppDatabase INSTANCE;
    
    public abstract HistoryLocationDao historyLocationDao();
    public abstract HistorySearchDao historySearchDao();
    public abstract RouteDao routeDao();
    public abstract FavoriteLocationDao favoriteLocationDao();
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                        )
                        .addMigrations(MIGRATION_1_2)
                        .fallbackToDestructiveMigration()
                        .build();
                }
            }
        }
        return INSTANCE;
    }
    
    // 数据库迁移示例
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 未来迁移逻辑
        }
    };
}
