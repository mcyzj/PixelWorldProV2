import org.gradle.jvm.tasks.Jar
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    java
    id("com.github.johnrengelman.shadow") version ("7.1.2")
    kotlin("jvm") version "1.8.0"
    id("com.xbaimiao.easylib") version ("1.1.0")
    `maven-publish`
}

group = "com.mcyzj"
version = "2.0.0-a2"

easylib {
    version = "2.3.9"
    nbt = false
    hikariCP = true
    ormlite = true
    userMinecraftLib = false
    minecraftVersion = "1.12.2"
    isPaper = false
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    //implementation(kotlin("stdlib-jdk8"))
    compileOnly("redis.clients:jedis:3.7.0")
    compileOnly("com.github.oshi:oshi-core:3.12.2")
    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly("org.bouncycastle:bcprov-lts8on:2.73.3")
    implementation(fileTree("shadowLibs"))
    compileOnly(fileTree("libs"))
    compileOnly("public:paper:1.16.5")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    shadowJar {
        arrayListOf(
            "io.papermc.lib=papermc.lib",
            "com.xbaimiao.easylib=easylib",
            "kotlin=kotlin"
        ).forEach {
            val args = it.split("=")
            relocate(args[0], "${project.group}.shadow.${args[1]}")
        }
        dependencies {
            exclude(dependency("org.slf4j:"))
            exclude(dependency("com.google.code.gson:gson:"))
        }
        exclude("LICENSE")
        exclude("META-INF/*.SF")
        archiveClassifier.set("")
    }
    processResources {
        val props = ArrayList<Pair<String, Any>>()
        props.add("version" to version)
        props.add("main" to "${project.group}.${project.name}")
        props.add("name" to project.name)
        expand(*props.toTypedArray())
    }
    artifacts {
        archives(shadowJar)
    }
}