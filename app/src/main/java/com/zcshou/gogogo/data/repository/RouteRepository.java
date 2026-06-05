package com.zcshou.gogogo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.zcshou.gogogo.data.dao.RouteDao;
import com.zcshou.gogogo.data.database.AppDatabase;
import com.zcshou.gogogo.data.entity.Route;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 路线数据仓库
 */
public class RouteRepository extends BaseRepository {
    
    private RouteDao routeDao;
    private LiveData<List<Route>> allRoutes;
    private ExecutorService executorService;
    
    public RouteRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        routeDao = db.routeDao();
        allRoutes = routeDao.getAllRoutes();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 获取所有路线（LiveData）
     */
    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }
    
    /**
     * 获取所有路线（同步）
     */
    public List<Route> getAllRoutesSync() {
        return routeDao.getAllRoutesSync();
    }
    
    /**
     * 保存路线
     */
    public void saveRoute(Route route) {
        executorService.execute(() -> {
            try {
                if (route.id > 0) {
                    // 更新现有路线
                    routeDao.update(route);
                    logDebug("Updated route: " + route.routeName);
                } else {
                    // 插入新路线
                    routeDao.insert(route);
                    logDebug("Inserted route: " + route.routeName);
                }
            } catch (Exception e) {
                logError("Failed to save route", e);
            }
        });
    }
    
    /**
     * 根据ID获取路线
     */
    public Route getRouteById(int id) {
        try {
            return routeDao.getRouteById(id);
        } catch (Exception e) {
            logError("Failed to get route by id", e);
            return null;
        }
    }
    
    /**
     * 删除路线
     */
    public void deleteRoute(int id) {
        executorService.execute(() -> {
            try {
                routeDao.deleteById(id);
                logDebug("Deleted route with id: " + id);
            } catch (Exception e) {
                logError("Failed to delete route", e);
            }
        });
    }
    
    /**
     * 删除路线（对象）
     */
    public void deleteRoute(Route route) {
        executorService.execute(() -> {
            try {
                routeDao.delete(route);
                logDebug("Deleted route: " + route.routeName);
            } catch (Exception e) {
                logError("Failed to delete route", e);
            }
        });
    }
    
    /**
     * 清空所有路线
     */
    public void clearAllRoutes() {
        executorService.execute(() -> {
            try {
                routeDao.deleteAll();
                logDebug("Cleared all routes");
            } catch (Exception e) {
                logError("Failed to clear routes", e);
            }
        });
    }
}
