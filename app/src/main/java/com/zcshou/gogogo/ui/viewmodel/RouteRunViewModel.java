package com.zcshou.gogogo.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zcshou.gogogo.data.entity.Route;
import com.zcshou.gogogo.data.repository.RouteRepository;
import com.zcshou.gogogo.data.repository.RepositoryManager;
import com.zcshou.gogogo.ui.base.BaseViewModel;

import java.util.List;

/**
 * 路线运行 ViewModel
 */
public class RouteRunViewModel extends BaseViewModel {

    private final RouteRepository routeRepository;
    private final MutableLiveData<Route> currentRoute = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSimulating = new MutableLiveData<>(false);
    private final MutableLiveData<Double> speed = new MutableLiveData<>(3.8);
    private final MutableLiveData<Integer> loopCount = new MutableLiveData<>(10);
    private final MutableLiveData<Boolean> speedFloat = new MutableLiveData<>(true);

    public RouteRunViewModel(@NonNull Application application) {
        super();
        routeRepository = RepositoryManager.getInstance(application).getRouteRepository();
    }

    public LiveData<List<Route>> getAllRoutes() {
        return routeRepository.getAllRoutes();
    }

    public LiveData<Route> getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(Route route) {
        currentRoute.setValue(route);
    }

    public LiveData<Boolean> getIsSimulating() {
        return isSimulating;
    }

    public void setIsSimulating(boolean simulating) {
        isSimulating.setValue(simulating);
    }

    public LiveData<Double> getSpeed() {
        return speed;
    }

    public void setSpeed(double s) {
        speed.setValue(s);
    }

    public LiveData<Integer> getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int count) {
        loopCount.setValue(count);
    }

    public LiveData<Boolean> getSpeedFloat() {
        return speedFloat;
    }

    public void setSpeedFloat(boolean sf) {
        speedFloat.setValue(sf);
    }
}
