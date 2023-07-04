plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

repositories {
    // aliyun
    maven("https://nexus.cyanbukkit.cn/repository/maven-public")
    maven {
        url = uri("https://maven.citizensnpcs.co/repo")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.15")
    // placeholderapi
    compileOnly("me.clip:placeholderapi:2.11.3")
    //
    //citizens
    compileOnly("net.citizensnpcs:citizensapi:2.0.30-SNAPSHOT")
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // implementation File XSeries-9.4.0.jar in root
    implementation(files("XSeries-9.4.0.jar"))



}


version = "1.1.0"

kotlin {
    jvmToolchain(8)
}
tasks {
    shadowJar {
        relocate("com.cryptomorin.xseries", "cn.cyanbukkit.xseries")
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }

}