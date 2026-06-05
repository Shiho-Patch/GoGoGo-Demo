package com.zcshou.gogogo.data.repository;

import com.elvishew.xlog.XLog;

/**
 * Repository 基类
 * <p>
 * 提供通用的 Repository 功能
 * - 数据操作
 * - 日志记录
 * - 错误处理
 */
public abstract class BaseRepository {

    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 记录调试日志
     */
    protected void logDebug(String message) {
        XLog.d(TAG + " " + message);
    }

    /**
     * 记录错误日志
     */
    protected void logError(String message, Throwable throwable) {
        XLog.e(TAG + " Error: " + message, throwable);
    }

    /**
     * 记录错误日志
     */
    protected void logError(String message) {
        XLog.e(TAG + " Error: " + message);
    }
}
