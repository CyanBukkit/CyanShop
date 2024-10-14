import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

val group = " cn.cyanbukkit.shop" // 先更改这里
version = "1.2.1"

bukkit {
    name = rootProject.name // 设置插件的名字 已设置跟随项目名
    description = "An example plugin for CyanBukkit" // 设置插件的描述
    authors = listOf("Your Name") // 设置插件作者
    website = "https://cyanbukkit.cn" // 设置插件的网站
    main = "${group}.cyanlib.launcher.CyanPluginLauncher" // 设置插件的主类 修改请到group修改
    val a = permissions.register("CyanShop.+")
    a.configure {
        default = BukkitPluginDescription.Permission.Default.OP
        description = "Cyan Shop permissions."
    }
    depend = listOf("Citizens", "Vault", "PlayerPoints", "PlaceholderAPI")
}

plugins {
    java
    kotlin("jvm") version "2.0.20"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    maven("https://nexus.cyanbukkit.cn/repository/maven-public/")
    maven("https://maven.elmakers.com/repository")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("net.citizensnpcs:citizensapi:2.0.30-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly(fileTree("libs") { include("*.jar") })
}




kotlin {
    jvmToolchain(8)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        archiveFileName.set("${rootProject.name}-${version}.jar")
    }
}


