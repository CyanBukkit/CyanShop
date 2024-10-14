// Decompiled with: CFR 0.152
// Class Version: 8
package cn.cyanbukkit.shop.cyanlib.loader.lanternmc.Logger.adapters;


import cn.cyanbukkit.shop.cyanlib.loader.lanternmc.Logger.LogLevel;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDKLogAdapter implements LogAdapter {
    private final Logger logger;

    static final int[] SwitchMap;

    static {
        SwitchMap = new int[LogLevel.values().length];
        try {
            JDKLogAdapter.SwitchMap[LogLevel.DEBUG.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            JDKLogAdapter.SwitchMap[LogLevel.INFO.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            JDKLogAdapter.SwitchMap[LogLevel.WARN.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            JDKLogAdapter.SwitchMap[LogLevel.ERROR.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }

    public JDKLogAdapter(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @Override
    public void log(LogLevel level, String message) {
        switch (Objects.requireNonNull(level, "level")) {
            case DEBUG: {
                this.logger.log(Level.FINE, message);
                break;
            }
            case INFO: {
                this.logger.log(Level.INFO, message);
                break;
            }
            case WARN: {
                this.logger.log(Level.WARNING, message);
                break;
            }
            case ERROR: {
                this.logger.log(Level.SEVERE, message);
            }
        }
    }

    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        switch (Objects.requireNonNull(level, "level")) {
            case DEBUG: {
                this.logger.log(Level.FINE, message, throwable);
                break;
            }
            case INFO: {
                this.logger.log(Level.INFO, message, throwable);
                break;
            }
            case WARN: {
                this.logger.log(Level.WARNING, message, throwable);
                break;
            }
            case ERROR: {
                this.logger.log(Level.SEVERE, message, throwable);
            }
        }
    }
}
