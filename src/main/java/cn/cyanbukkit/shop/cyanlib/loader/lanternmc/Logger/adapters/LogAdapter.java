// Decompiled with: CFR 0.152
// Class Version: 8
package cn.cyanbukkit.shop.cyanlib.loader.lanternmc.Logger.adapters;


import cn.cyanbukkit.shop.cyanlib.loader.lanternmc.Logger.LogLevel;

public interface LogAdapter {
    public void log(LogLevel var1, String var2);

    public void log(LogLevel var1, String var2, Throwable var3);
}
