// Decompiled with: CFR 0.152
// Class Version: 8
package cn.cyanbukkit.shop.cyanlib.loader.lanternmc.Logger;


import cn.cyanbukkit.shop.cyanlib.loader.lanternmc.Logger.adapters.LogAdapter;

import java.util.Objects;

public class Logger {
    private final LogAdapter adapter;
    private LogLevel level = LogLevel.INFO;

    public Logger(LogAdapter adapter) {
        this.adapter = Objects.requireNonNull(adapter, "adapter");
    }

    public LogLevel getLevel() {
        return this.level;
    }

    public void setLevel(LogLevel level) {
        this.level = Objects.requireNonNull(level, "level");
    }

    private boolean canLog(LogLevel level) {
        return Objects.requireNonNull(level, "level").compareTo(this.level) >= 0;
    }

    public void log(LogLevel level, String message) {
        if (this.canLog(level)) {
            this.adapter.log(level, message);
        }
    }

    public void log(LogLevel level, String message, Throwable throwable) {
        if (this.canLog(level)) {
            this.adapter.log(level, message, throwable);
        }
    }

    public void debug(String message) {
        this.log(LogLevel.DEBUG, message);
    }

    public void debug(String message, Throwable throwable) {
        this.log(LogLevel.DEBUG, message, throwable);
    }

    public void info(String message) {
        this.log(LogLevel.INFO, message);
    }

    public void info(String message, Throwable throwable) {
        this.log(LogLevel.INFO, message, throwable);
    }

    public void warn(String message) {
        this.log(LogLevel.WARN, message);
    }

    public void warn(String message, Throwable throwable) {
        this.log(LogLevel.WARN, message, throwable);
    }

    public void error(String message) {
        this.log(LogLevel.ERROR, message);
    }

    public void error(String message, Throwable throwable) {
        this.log(LogLevel.ERROR, message, throwable);
    }
}
