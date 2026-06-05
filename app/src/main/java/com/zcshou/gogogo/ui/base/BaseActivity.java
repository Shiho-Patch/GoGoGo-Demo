package com.zcshou.gogogo.ui.base;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.elvishew.xlog.XLog;

/**
 * Activity 基类
 * 提供通用的功能和生命周期管理
 * - ViewModel 支持
 * - 加载状态观察
 * - 错误处理
 * - 消息显示
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    private ViewModelProvider viewModelProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XLog.d(TAG + " onCreate");
        
        initViewModel();
        initViews();
        initData();
        initObservers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        XLog.d(TAG + " onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        XLog.d(TAG + " onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        XLog.d(TAG + " onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        XLog.d(TAG + " onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XLog.d(TAG + " onDestroy");
    }

    /**
     * 获取 ViewModelProvider
     */
    protected ViewModelProvider getViewModelProvider() {
        if (viewModelProvider == null) {
            viewModelProvider = new ViewModelProvider(this);
        }
        return viewModelProvider;
    }

    /**
     * 初始化 ViewModel
     * 子类可重写此方法来初始化特定的 ViewModel
     */
    protected void initViewModel() {
        // 默认空实现，子类按需重写
    }

    /**
     * 初始化视图
     */
    protected abstract void initViews();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化观察者（LiveData 等）
     */
    protected void initObservers() {
        // 默认空实现，子类按需重写
    }

    /**
     * 显示 Toast 消息
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示长 Toast 消息
     */
    protected void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
