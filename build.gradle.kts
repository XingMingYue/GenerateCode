plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("org.beryx.runtime") version "1.12.1"
}
group = "com.xingmingyue"
version = "1.0"

val tornadofxVersion: String by rootProject

javafx {
    modules = listOf("javafx.base", "javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.web")
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.example.MainKt")
}

val javaFXOptions = the<org.openjfx.gradle.JavaFXOptions>()

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.31")

    org.openjfx.gradle.JavaFXPlatform.values().forEach { platform ->
        val cfg = configurations.create("javafx_" + platform.classifier)
        org.openjfx.gradle.JavaFXModule.getJavaFXModules(javaFXOptions.modules).forEach { m ->
            project.dependencies.add(
                cfg.name,
                String.format("org.openjfx:%s:%s:%s", m.artifactName, javaFXOptions.version, platform.classifier)
            )
        }
    }

    // https://github.com/edvin/tornadofx
    implementation("no.tornado:tornadofx:1.7.20") {
        exclude("org.jetbrains.kotlin")
    }

    // 富文本组件 https://github.com/FXMisc/RichTextFX
    implementation("org.fxmisc.richtext:richtextfx:0.10.7")

    // https://github.com/dlsc-software-consulting-gmbh/PreferencesFX
    implementation("com.dlsc.preferencesfx:preferencesfx-core:11.8.0")

    // jmetro
    implementation("org.jfxtras:jmetro:11.6.15")

    // jna https://github.com/java-native-access/jna
    implementation("net.java.dev.jna:jna:5.9.0")
    implementation("net.java.dev.jna:jna-platform:5.9.0")

    // 程序运行时提供Css重载的能力 https://github.com/McFoggy/cssfx
    implementation("fr.brouillard.oss:cssfx:11.5.0")

    // 动画库 https://github.com/Typhon0/AnimateFX
    implementation("io.github.typhon0:AnimateFX:1.2.1")

    // 图标库 http://kordamp.org/ikonli/
    implementation(platform("org.kordamp.ikonli:ikonli-bom:12.2.0"))
    implementation("org.kordamp.ikonli:ikonli-javafx:12.2.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome-pack:12.2.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome5-pack:12.2.0")
    implementation("org.kordamp.ikonli:ikonli-antdesignicons-pack:12.2.0")
    implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:12.2.0")

    // 组件库 https://github.com/sshahine/JFoenix
    implementation("com.jfoenix:jfoenix:9.0.10")

    // 工具包 https://hutool.cn/docs/#/
    implementation("cn.hutool:hutool-all:5.7.18")

    // freemarker
    implementation("org.freemarker:freemarker:2.3.31")

    // 数据库连接池
    implementation("com.zaxxer:HikariCP:5.0.0")

    // 数据库驱动
    implementation("mysql:mysql-connector-java:8.0.27")
    implementation("com.oracle.database.jdbc:ojdbc11:21.4.0.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}


tasks.test {
    useJUnit()
}

runtime {
    // https://badass-runtime-plugin.beryx.org/releases/latest/
    imageZip.set(project.file("${project.buildDir}/image-zip/${rootProject.name}-image.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    modules.set(listOf("java.desktop", "jdk.unsupported", "java.scripting", "java.logging", "java.xml"))

//    targetPlatform("linux", System.getenv("JDK_LINUX_HOME"))
//    targetPlatform("mac", System.getenv("JDK_MAC_HOME"))
//    targetPlatform("win", System.getenv("JAVA_HOME"))

    jpackage {
        // 不添加此参数，打包成exe后，https协议的网络图片资源无法加载
        jvmArgs.add("-Dhttps.protocols=TLSv1.1,TLSv1.2")
        val current = org.gradle.internal.os.OperatingSystem.current()
        imageOptions.add("--resource-dir"); imageOptions.add("src/main/resources")
        imageOptions.add("--vendor"); imageOptions.add("XingMingYue")
        imageOptions.add("--description"); imageOptions.add("代码生成器")
        imageOptions.add("--copyright"); imageOptions.add("Copyright 2021, All rights reserved")
        imageOptions.add("--app-version"); imageOptions.add(version.toString())
        when {
            current.isWindows -> {
                imageOptions.add("--icon");imageOptions.add("src/main/resources/GenerateCode.ico")
                installerOptions.add("--temp"); installerOptions.add("${project.buildDir}/tempDir")
                installerOptions.add("--win-per-user-install")
                installerOptions.add("--win-dir-chooser")
                installerOptions.add("--win-menu")
                installerOptions.add("--win-shortcut")
            }
            current.isLinux -> {
                imageOptions.add("--icon");imageOptions.add("src/main/resources/GenerateCode.ico")
                installerOptions.add("--linux-package-name")
                installerOptions.add(rootProject.name)
                installerOptions.add("--linux-shortcut")
            }
            current.isMacOsX -> {
                imageOptions.add("--icon");imageOptions.add("src/main/resources/GenerateCode.icns")
//                installerOptions.add(rootProject.name)
            }
        }
    }
}



tasks.withType(CreateStartScripts::class).forEach { script ->
    script.doFirst {
        script.classpath = files("lib/*")
    }
}

tasks["runtime"].doLast {
    org.openjfx.gradle.JavaFXPlatform.values().forEach { platform ->
        val cfg = configurations["javafx_" + platform.classifier]
        cfg.resolvedConfiguration.files.forEach { f ->
            copy {
                from(f)
                into("build/image/${rootProject.name}-${platform.classifier}/lib")
            }
        }
    }
}