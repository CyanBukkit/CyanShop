package cn.cyanbukkit.shop.cyanlib.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 注册主指令注解反射用于
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterCommand {
    String name();

    String permission() default "";
}
