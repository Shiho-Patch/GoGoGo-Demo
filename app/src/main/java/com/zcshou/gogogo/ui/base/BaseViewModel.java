package com.zcshou.gogogo.ui.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elvishew.xlog.XLog;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ViewModel 基类
 * <p>
 * 提供通用的 ViewModel 功能
 * - 加载状态管理
 * - 错误处理
 * - 消息通知
 * - 生命周期感知
 */
public class BaseViewModel extends ViewModel {

    protected final String TAG = this.getClass().getSimpleName();

    // 加载状态
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    // 错误信息
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // 成功消息
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    
    // 防止重复加载
    private final AtomicBoolean isLoadingData = new AtomicBoolean(false);

    /**
     * 获取加载状态的 LiveData
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * 获取错误信息的 LiveData
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * 获取成功消息的 LiveData
     */
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    /**
     * 设置加载状态
     */
    protected void setLoading(boolean loading) {
        isLoading.setValue(loading);
        isLoadingData.set(loading);
    }

    /**
     * 发送错误信息
     */
    protected void setError(String message) {
        XLog.e(TAG + " Error: " + message);
        errorMessage.setValue(message);
        setLoading(false);
    }

    /**
     * 发送成功消息
     */
    protected void setSuccess(String message) {
        XLog.d(TAG + " Success: " + message);
        successMessage.setValue(message);
    }

    /**
     * 检查是否正在加载
     */
    protected boolean isLoadingData() {
        return isLoadingData.get();
    }

    /**
     * 清理错误信息
     */
    public void clearError() {
        errorMessage.setValue(null);
    }

    /**
     * 清理成功消息
     */
    public void clearSuccess() {
        successMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        XLog.d(TAG + " onCleared");
        // 清理资源，取消任务等
    }
}
