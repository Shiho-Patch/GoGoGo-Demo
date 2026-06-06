package com.zcshou.gogogo.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zcshou.gogogo.data.entity.Route;
import com.zcshou.gogogo.data.repository.RouteRepository;
import com.zcshou.gogogo.data.repository.RepositoryManager;
import com.zcshou.gogogo.ui.base.BaseViewModel;
import com.zcshou.gogogo.utils.RouteIOUtils;

import java.io.File;
import java.util.List;

/**
 * 路线管理 ViewModel
 */
public class RouteViewModel extends BaseViewModel {

    private final RouteRepository routeRepository;
    private final LiveData<List<Route>> allRoutes;

    public RouteViewModel(@NonNull Application application) {
        super();
        routeRepository = RepositoryManager.getInstance(application).getRouteRepository();
        allRoutes = routeRepository.getAllRoutes();
    }

    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }

    public void saveRoute(String routeName, String pointsJson) {
        Route route = new Route(routeName, pointsJson, System.currentTimeMillis());
        routeRepository.saveRoute(route);
        setSuccess("路线保存成功");
    }

    public void saveRoute(Route route) {
        if (route != null) {
            routeRepository.saveRoute(route);
            setSuccess("路线保存成功");
        }
    }

    public void deleteRoute(Route route) {
        routeRepository.deleteRoute(route);
        setSuccess("路线已删除");
    }

    public void deleteRoute(int routeId) {
        routeRepository.deleteRoute(routeId);
        setSuccess("路线已删除");
    }
    
    /**
     * 从 JSON 字符串导入路线
     */
    public boolean importRouteFromJson(String jsonString) {
        Route route = RouteIOUtils.jsonToRoute(jsonString);
        if (route != null) {
            saveRoute(route);
            return true;
        } else {
            setError("路线格式无效");
            return false;
        }
    }
    
    /**
     * 从文件导入路线
     */
    public boolean importRouteFromFile(File file) {
        Route route = RouteIOUtils.importRoute(file);
        if (route != null) {
            saveRoute(route);
            return true;
        } else {
            setError("无法从文件导入路线");
            return false;
        }
    }
    
    /**
     * 将路线导出为文件
     */
    public boolean exportRouteToFile(Route route) {
        if (route == null) {
            setError("路线无效");
            return false;
        }
        
        boolean success = RouteIOUtils.exportRoute(getApplication(), route);
        if (success) {
            setSuccess("路线导出成功");
        } else {
            setError("路线导出失败");
        }
        return success;
    }
    
    /**
     * 将路线转换为 JSON 字符串
     */
    public String routeToJson(Route route) {
        return RouteIOUtils.routeToJson(route);
    }
}
