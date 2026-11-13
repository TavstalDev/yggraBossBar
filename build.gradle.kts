plugins {
    id("java")
}

allprojects {
    group = "io.github.tavstaldev.yggraBossBar"
    version = "0.1.0"
}

val jetbrainsAnnotationsVersion = project.property("jetbrainsAnnotationsVersion") as String
val lombokVersion = project.property("lombokVersion") as String
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
        sourceCompatibility = JavaVersion.toVersion(25)
        targetCompatibility = JavaVersion.toVersion(25)
    }
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
        maven("https://repo.alessiodp.com/releases")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:${jetbrainsAnnotationsVersion}")
        compileOnly("org.projectlombok:lombok:${lombokVersion}")
        annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    }

    configurations.configureEach {
        if (name.endsWith("RuntimeClasspath") || name.endsWith("TestRuntimeClasspath")) {
            attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
        }
    }
}