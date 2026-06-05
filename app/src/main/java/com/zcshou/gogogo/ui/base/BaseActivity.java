package com.zcshou.gogogo.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.elvishew.xlog.XLog;

/**
 * Activity 基类
 * 提供通用的功能和生命周期管理
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XLog.d(TAG + " onCreate");
        
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
}
